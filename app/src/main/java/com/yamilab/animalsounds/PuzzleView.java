package com.yamilab.animalsounds;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Поле пазла: контур картинки, кусочки-«пазлинки» и лоток с ещё не поставленными кусочками.
 * В портрете поле сверху, лоток снизу; в ландшафте поле слева, лоток справа. Кусочки
 * перетаскиваются пальцем и, если отпущены близко к своему месту, примагничиваются к нему.
 * Подсказка (контуры кусочков на поле) включается через {@link #setOutlinesVisible}.
 */
public class PuzzleView extends View {

    public interface Listener {
        /** Кусочек встал на место (не последний). */
        void onPieceSnapped();

        /** Встал последний кусочек — картинка собрана. */
        void onSolved();

        /** Тап по уже собранной картинке (на поле): повторяет звук животного. */
        void onSolvedTapped();

        /** Тап по лотку, когда картинка уже собрана: сразу следующее задание. */
        void onTrayTappedWhenSolved();
    }

    // Форма выступа (кривые Безье в долях длины ребра): 3 сегмента по (c1, c2, конец),
    // выступ уходит на ~0.25 длины ребра.
    private static final float[][] TAB = {
            {0.2f, 0f}, {0.5f, -0.1f}, {0.4f, 0.1f},
            {0.3f, 0.3f}, {0.7f, 0.3f}, {0.6f, 0.1f},
            {0.5f, -0.1f}, {0.8f, 0f}, {1f, 0f}};
    private static final float TAB_EXTENT = 0.27f;
    // Насколько близко (в долях меньшей стороны ячейки) надо отпустить кусочек, чтобы он встал.
    private static final float SNAP_FRACTION = 0.34f;
    private static final long SNAP_MS = 110;
    private static final long SOLVED_MS = 800;

    private static final class Piece {
        int col;
        int row;
        Bitmap bmp;
        Path path;      // контур в координатах bmp
        float x;        // левый верхний угол bmp на экране
        float y;
        float slotX;
        float slotY;
        boolean placed;
        boolean snapping;
        // Центр кусочка в лотке в долях размеров лотка — чтобы пережить смену размера.
        float relX;
        float relY;
    }

    private final float density = getResources().getDisplayMetrics().density;
    private final Random random = new Random();
    private final Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint solvedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix shaderMatrix = new Matrix();

    private Bitmap source;
    private Bitmap scaled;
    private BitmapShader scaledShader;
    private int cols;
    private int rows;
    // Знак выступа на внутренних рёбрах: hEdge[r][c] — ребро над ячейкой (c, r), vEdge[c][r] —
    // ребро слева от ячейки (c, r).
    private int[][] hEdge;
    private int[][] vEdge;
    private final ArrayList<Piece> pieces = new ArrayList<>();

    private boolean outlines = false;
    private boolean solved = false;
    private float solvedT = 1f;
    private float bounce = 1f;
    private ValueAnimator solvedAnimator;

    private final RectF trayRect = new RectF();
    private final RectF boardRect = new RectF();
    private float cellW;
    private float cellH;
    private float margin;

    private Piece dragged;
    private float grabDx;
    private float grabDy;
    private float downX;
    private float downY;

    private Listener listener;

    public PuzzleView(Context context) {
        super(context);
        init();
    }

    public PuzzleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PuzzleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        shadowPaint.setColorFilter(new PorterDuffColorFilter(0x66000000, PorterDuff.Mode.SRC_IN));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setOutlinesVisible(boolean visible) {
        outlines = visible;
        invalidate();
    }

    /** Нарезает картинку на cols×rows кусочков и рассыпает их в лоток. */
    public void setPuzzle(Bitmap image, int cols, int rows) {
        setPuzzle(image, cols, rows, null);
    }

    /**
     * То же, но часть кусочков сразу стоит на месте: placed[row * cols + col] (например, после
     * поворота экрана). Массив другого размера игнорируется.
     */
    public void setPuzzle(Bitmap image, int cols, int rows, @Nullable boolean[] placed) {
        cancelAnimators();
        this.source = image;
        this.cols = cols;
        this.rows = rows;
        solved = false;
        solvedT = 1f;
        bounce = 1f;
        dragged = null;

        hEdge = new int[rows + 1][cols];
        vEdge = new int[cols + 1][rows];
        for (int r = 1; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                hEdge[r][c] = random.nextBoolean() ? 1 : -1;
            }
        }
        for (int c = 1; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                vEdge[c][r] = random.nextBoolean() ? 1 : -1;
            }
        }

        pieces.clear();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Piece p = new Piece();
                p.col = c;
                p.row = r;
                pieces.add(p);
            }
        }
        if (placed != null && placed.length == cols * rows) {
            boolean all = true;
            for (Piece p : pieces) {
                p.placed = placed[p.row * cols + p.col];
                all &= p.placed;
            }
            if (all) {
                solved = true;
                solvedT = 1f;
            }
        }
        if (getWidth() > 0 && getHeight() > 0) {
            layoutPuzzle();
        }
        invalidate();
    }

    /** Какие кусочки уже стоят на месте: индекс row * cols + col. Null, если пазл не задан. */
    @Nullable
    public boolean[] getPlacedState() {
        if (source == null) {
            return null;
        }
        boolean[] state = new boolean[cols * rows];
        for (Piece p : pieces) {
            state[p.row * cols + p.col] = p.placed;
        }
        return state;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (source != null && w > 0 && h > 0) {
            layoutPuzzle();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimators();
        super.onDetachedFromWindow();
    }

    private void cancelAnimators() {
        stopFireworks();
        if (solvedAnimator != null) {
            solvedAnimator.cancel();
            solvedAnimator = null;
        }
        for (Piece p : pieces) {
            p.snapping = false;
        }
    }

    // ------------------------------------------------------------------ раскладка

    /**
     * Делит область на поле и лоток и подбирает такое положение границы, при котором
     * картинка получается наибольшей, а в лотке всё ещё хватает места для всех кусочков.
     * Затем строит битмапы кусочков под получившийся масштаб.
     */
    private void layoutPuzzle() {
        float w = getWidth();
        float h = getHeight();
        float pad = 8 * density;
        boolean side = w > h; // в ландшафте лоток справа от поля
        float srcW = source.getWidth();
        float srcH = source.getHeight();

        // Пересчёт раскладки посреди игры (например, загрузился баннер): запоминаем, где в
        // лотке лежал каждый кусочек, и потом ставим его в то же относительное место, а не
        // перемешиваем лоток заново. Новый пазл (битмапов ещё нет) раскладывается с нуля.
        boolean fresh = pieces.isEmpty() || pieces.get(0).bmp == null || trayRect.width() <= 0;
        if (!fresh) {
            for (Piece p : pieces) {
                if (!p.placed) {
                    p.relX = (p.x + p.bmp.getWidth() / 2f - trayRect.left) / trayRect.width();
                    p.relY = (p.y + p.bmp.getHeight() / 2f - trayRect.top) / trayRect.height();
                }
            }
        }

        // Для лотка требуем место под каждую деталь с запасом на выступы (1.25 ширины ячейки);
        // если так не получается, постепенно смягчаем запас.
        float bestF = 0.5f;
        int total = cols * rows;
        search:
        for (float slack : new float[]{1.25f, 1.1f, 0.95f}) {
            float bestScale = -1;
            for (float f = 0.30f; f <= 0.72f; f += 0.02f) {
                float bw, bh, tw, th;
                if (side) {
                    float avail = w - 3 * pad;
                    bw = avail * f;
                    tw = avail - bw;
                    bh = th = h - 2 * pad;
                } else {
                    float avail = h - 3 * pad;
                    bh = avail * f;
                    th = avail - bh;
                    bw = tw = w - 2 * pad;
                }
                float scale = Math.min(bw / srcW, bh / srcH);
                float pieceW = srcW * scale / cols * slack;
                float pieceH = srcH * scale / rows * slack;
                if (trayFit(tw, th, pieceW, pieceH, total) >= 1f && scale > bestScale) {
                    bestScale = scale;
                    bestF = f;
                }
            }
            if (bestScale > 0) {
                break search;
            }
        }

        float bw, bh;
        if (side) {
            float avail = w - 3 * pad;
            bw = avail * bestF;
            bh = h - 2 * pad;
            float scale = Math.min(bw / srcW, bh / srcH);
            float iw = srcW * scale;
            float ih = srcH * scale;
            boardRect.set(pad + (bw - iw) / 2, (h - ih) / 2, pad + (bw - iw) / 2 + iw, (h - ih) / 2 + ih);
            trayRect.set(2 * pad + bw, pad, w - pad, h - pad);
        } else {
            float avail = h - 3 * pad;
            bh = avail * bestF;
            float scale = Math.min((w - 2 * pad) / srcW, bh / srcH);
            float iw = srcW * scale;
            float ih = srcH * scale;
            boardRect.set((w - iw) / 2, pad + (bh - ih) / 2, (w - iw) / 2 + iw, pad + (bh - ih) / 2 + ih);
            trayRect.set(pad, 2 * pad + bh, w - pad, h - pad);
        }

        int iw = Math.max(1, Math.round(boardRect.width()));
        int ih = Math.max(1, Math.round(boardRect.height()));
        scaled = Bitmap.createScaledBitmap(source, iw, ih, true);
        scaledShader = new BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        solvedPaint.setShader(scaledShader);

        cellW = boardRect.width() / cols;
        cellH = boardRect.height() / rows;
        margin = TAB_EXTENT * Math.max(cellW, cellH) + 2 * density;

        for (Piece p : pieces) {
            buildPiece(p);
            p.slotX = boardRect.left + p.col * cellW - margin;
            p.slotY = boardRect.top + p.row * cellH - margin;
            if (p.placed || solved) {
                p.x = p.slotX;
                p.y = p.slotY;
            }
        }
        if (fresh) {
            scatterLoose();
        } else {
            for (Piece p : pieces) {
                if (!p.placed) {
                    float cx = trayRect.left + Math.max(0f, Math.min(1f, p.relX)) * trayRect.width();
                    float cy = trayRect.top + Math.max(0f, Math.min(1f, p.relY)) * trayRect.height();
                    p.x = cx - p.bmp.getWidth() / 2f;
                    p.y = cy - p.bmp.getHeight() / 2f;
                }
            }
        }
        invalidate();
    }

    /** Во сколько раз лоток tw×th больше, чем нужно для n деталей pw×ph в лучшей сетке (≥1 — влезают). */
    private static float trayFit(float tw, float th, float pw, float ph, int n) {
        float best = 0;
        for (int c = 1; c <= n; c++) {
            int r = (n + c - 1) / c;
            best = Math.max(best, Math.min(tw / (c * pw), th / (r * ph)));
        }
        return best;
    }

    private void buildPiece(Piece p) {
        int pw = (int) Math.ceil(cellW + 2 * margin);
        int ph = (int) Math.ceil(cellH + 2 * margin);
        float ox = p.col * cellW - margin;
        float oy = p.row * cellH - margin;

        Path path = piecePath(p.col, p.row);
        path.offset(-ox, -oy);
        p.path = path;

        Bitmap b = Bitmap.createBitmap(pw, ph, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(b);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        cv.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setFilterBitmap(true);
        cv.drawBitmap(scaled, -ox, -oy, paint);
        paint.setXfermode(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.4f * density);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(0x99000000);
        cv.drawPath(path, paint);
        p.bmp = b;
    }

    /** Контур кусочка (c, r) в координатах картинки. */
    private Path piecePath(int c, int r) {
        float x0 = c * cellW;
        float y0 = r * cellH;
        float x1 = x0 + cellW;
        float y1 = y0 + cellH;
        Path p = new Path();
        p.moveTo(x0, y0);
        if (r == 0) {
            p.lineTo(x1, y0);
        } else {
            addEdge(p, true, x0, y0, cellW, hEdge[r][c], false);
        }
        if (c == cols - 1) {
            p.lineTo(x1, y1);
        } else {
            addEdge(p, false, x1, y0, cellH, vEdge[c + 1][r], false);
        }
        if (r == rows - 1) {
            p.lineTo(x0, y1);
        } else {
            addEdge(p, true, x0, y1, cellW, hEdge[r + 1][c], true);
        }
        if (c == 0) {
            p.lineTo(x0, y0);
        } else {
            addEdge(p, false, x0, y0, cellH, vEdge[c][r], true);
        }
        p.close();
        return p;
    }

    /**
     * Добавляет ребро с выступом. Ребро задано в каноническом направлении (горизонтальное —
     * слева направо, вертикальное — сверху вниз), sign определяет, в какую сторону выступ;
     * reverse проходит то же ребро в обратную сторону — так два соседних кусочка совпадают.
     */
    private static void addEdge(Path path, boolean horizontal, float ox, float oy, float len,
                                float sign, boolean reverse) {
        float[] px = new float[10];
        float[] py = new float[10];
        for (int i = 0; i < 10; i++) {
            float u = i == 0 ? 0f : TAB[i - 1][0];
            float v = i == 0 ? 0f : TAB[i - 1][1];
            if (horizontal) {
                px[i] = ox + u * len;
                py[i] = oy + sign * v * len;
            } else {
                px[i] = ox + sign * v * len;
                py[i] = oy + u * len;
            }
        }
        if (!reverse) {
            for (int s = 0; s < 3; s++) {
                path.cubicTo(px[3 * s + 1], py[3 * s + 1], px[3 * s + 2], py[3 * s + 2],
                        px[3 * s + 3], py[3 * s + 3]);
            }
        } else {
            for (int s = 2; s >= 0; s--) {
                path.cubicTo(px[3 * s + 2], py[3 * s + 2], px[3 * s + 1], py[3 * s + 1],
                        px[3 * s], py[3 * s]);
            }
        }
    }

    /** Рассыпает ещё не поставленные кусочки по лотку в перемешанном порядке. */
    private void scatterLoose() {
        ArrayList<Piece> loose = new ArrayList<>();
        for (Piece p : pieces) {
            if (!p.placed) {
                loose.add(p);
            }
        }
        int n = loose.size();
        if (n == 0) {
            return;
        }
        Collections.shuffle(loose, random);

        int bestCols = 1;
        float bestFit = -1;
        for (int c = 1; c <= n; c++) {
            int r = (n + c - 1) / c;
            float fit = Math.min(trayRect.width() / (c * cellW), trayRect.height() / (r * cellH));
            if (fit > bestFit + 0.001f) {
                bestFit = fit;
                bestCols = c;
            }
        }
        int rowsT = (n + bestCols - 1) / bestCols;
        float cw = trayRect.width() / bestCols;
        float chh = trayRect.height() / rowsT;
        for (int i = 0; i < n; i++) {
            Piece p = loose.get(i);
            int gx = i % bestCols;
            int gy = i / bestCols;
            int inRow = Math.min(bestCols, n - gy * bestCols);
            float rowShift = (bestCols - inRow) * cw / 2f;
            float jx = (random.nextFloat() * 2 - 1) * Math.max(0f, cw - cellW) * 0.3f;
            float jy = (random.nextFloat() * 2 - 1) * Math.max(0f, chh - cellH) * 0.3f;
            float cx = trayRect.left + rowShift + (gx + 0.5f) * cw + jx;
            float cy = trayRect.top + (gy + 0.5f) * chh + jy;
            p.x = cx - p.bmp.getWidth() / 2f;
            p.y = cy - p.bmp.getHeight() / 2f;
            // Порядок наложения = порядок в списке.
            pieces.remove(p);
            pieces.add(p);
        }
    }

    // ------------------------------------------------------------------ рисование

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (source == null || scaled == null) {
            return;
        }
        float corner = 12 * density;

        // Лоток
        fillPaint.setColor(0x14000000);
        canvas.drawRoundRect(trayRect, corner, corner, fillPaint);

        canvas.save();
        if (bounce != 1f) {
            canvas.scale(bounce, bounce, boardRect.centerX(), boardRect.centerY());
        }

        // Поле
        fillPaint.setColor(0x22FFFFFF);
        canvas.drawRoundRect(boardRect, 6 * density, 6 * density, fillPaint);

        // Подсказка: контуры кусочков на своих местах
        if (outlines && !solved) {
            for (Piece p : pieces) {
                if (!p.placed) {
                    drawOutline(canvas, p, 1.2f * density);
                }
            }
        }

        // Уже поставленные кусочки
        for (Piece p : pieces) {
            if (p.placed && !p.snapping) {
                canvas.drawBitmap(p.bmp, p.x, p.y, bitmapPaint);
            }
        }

        // Собранная картинка целиком — плавно проявляется поверх кусочков
        if (solved) {
            solvedPaint.setAlpha(Math.round(255 * Math.min(1f, solvedT * 1.5f)));
            shaderMatrix.setTranslate(boardRect.left, boardRect.top);
            scaledShader.setLocalMatrix(shaderMatrix);
            canvas.drawRoundRect(boardRect, 8 * density, 8 * density, solvedPaint);
        }

        // Общий контур картинки
        strokePaint.setColor(0x99FFFFFF);
        strokePaint.setStrokeWidth(5 * density);
        canvas.drawRoundRect(boardRect, 8 * density, 8 * density, strokePaint);
        strokePaint.setColor(0xAA37474F);
        strokePaint.setStrokeWidth(2.2f * density);
        canvas.drawRoundRect(boardRect, 8 * density, 8 * density, strokePaint);
        canvas.restore();

        // Кусочки в лотке и «примагничивающиеся»; перетаскиваемый — последним, с тенью
        for (Piece p : pieces) {
            if (!p.placed && p != dragged) {
                canvas.drawBitmap(p.bmp, p.x, p.y, bitmapPaint);
            }
        }
        if (dragged != null && !dragged.placed) {
            float cx = dragged.x + dragged.bmp.getWidth() / 2f;
            float cy = dragged.y + dragged.bmp.getHeight() / 2f;
            canvas.save();
            canvas.scale(1.06f, 1.06f, cx, cy);
            canvas.drawBitmap(dragged.bmp, dragged.x + 3 * density, dragged.y + 5 * density, shadowPaint);
            canvas.drawBitmap(dragged.bmp, dragged.x, dragged.y, bitmapPaint);
            canvas.restore();
        }
        drawSparks(canvas);
    }

    private void drawOutline(Canvas canvas, Piece p, float width) {
        canvas.save();
        canvas.translate(p.slotX, p.slotY);
        strokePaint.setColor(0xB3FFFFFF);
        strokePaint.setStrokeWidth(width * 2.6f);
        canvas.drawPath(p.path, strokePaint);
        strokePaint.setColor(0xCC37474F);
        strokePaint.setStrokeWidth(width);
        canvas.drawPath(p.path, strokePaint);
        canvas.restore();
    }

    // ------------------------------------------------------------------ салют

    private static final class Spark {
        float x, y, vx, vy, life, maxLife;
        int color;
    }

    private static final float[] BURST_TIMES = {0.35f, 0.75f, 1.1f, 1.45f};
    private final ArrayList<Spark> sparks = new ArrayList<>();
    private ValueAnimator fireworksAnimator;
    private int nextBurst;
    private float lastFxTime;

    /** Небольшой салют поверх экрана: несколько залпов, совпадающих по времени со звуком. */
    public void launchFireworks() {
        stopFireworks();
        nextBurst = 0;
        lastFxTime = 0f;
        fireworksAnimator = ValueAnimator.ofFloat(0f, 1f);
        fireworksAnimator.setDuration(2800);
        fireworksAnimator.addUpdateListener(anim -> {
            float t = anim.getCurrentPlayTime() / 1000f;
            float dt = Math.min(0.05f, t - lastFxTime);
            lastFxTime = t;
            while (nextBurst < BURST_TIMES.length && t >= BURST_TIMES[nextBurst]) {
                burst();
                nextBurst++;
            }
            stepSparks(dt);
            invalidate();
        });
        fireworksAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                sparks.clear();
                invalidate();
            }
        });
        fireworksAnimator.start();
    }

    private void stopFireworks() {
        if (fireworksAnimator != null) {
            fireworksAnimator.cancel();
            fireworksAnimator = null;
        }
        sparks.clear();
    }

    private void burst() {
        float cx = getWidth() * (0.2f + 0.6f * random.nextFloat());
        float cy = getHeight() * (0.15f + 0.4f * random.nextFloat());
        int color = Color.HSVToColor(new float[]{random.nextFloat() * 360f, 0.75f, 1f});
        int n = 72;
        for (int k = 0; k < n; k++) {
            double ang = 2 * Math.PI * k / n + random.nextFloat() * 0.15f;
            float speed = (120 + random.nextFloat() * 260) * density;
            Spark s = new Spark();
            s.x = cx;
            s.y = cy;
            s.vx = (float) Math.cos(ang) * speed;
            s.vy = (float) Math.sin(ang) * speed;
            s.maxLife = 1.1f + random.nextFloat() * 0.8f;
            s.color = k % 5 == 0 ? Color.WHITE : color;
            sparks.add(s);
        }
    }

    private void stepSparks(float dt) {
        for (int i = sparks.size() - 1; i >= 0; i--) {
            Spark s = sparks.get(i);
            s.life += dt;
            if (s.life >= s.maxLife) {
                sparks.remove(i);
                continue;
            }
            s.vy += 220 * density * dt;
            float drag = Math.max(0f, 1f - 1.3f * dt);
            s.vx *= drag;
            s.vy *= drag;
            s.x += s.vx * dt;
            s.y += s.vy * dt;
        }
    }

    private void drawSparks(Canvas canvas) {
        if (sparks.isEmpty()) {
            return;
        }
        strokePaint.setStrokeWidth(2.6f * density);
        for (Spark s : sparks) {
            float frac = 1f - s.life / s.maxLife;
            int alpha = Math.round(255 * Math.min(1f, frac * 1.6f));
            int color = (s.color & 0x00FFFFFF) | (alpha << 24);
            strokePaint.setColor(color);
            canvas.drawLine(s.x, s.y, s.x - s.vx * 0.05f, s.y - s.vy * 0.05f, strokePaint);
            fillPaint.setColor(color);
            canvas.drawCircle(s.x, s.y, (2.4f + 3.2f * frac) * density, fillPaint);
        }
    }

    // ------------------------------------------------------------------ касания

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (source == null) {
            return false;
        }
        float x = e.getX();
        float y = e.getY();
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downY = y;
                if (solved) {
                    return true;
                }
                dragged = hit(x, y);
                if (dragged != null) {
                    if (getParent() != null) {
                        // Не даём ViewPager2 перехватить перетаскивание кусочка как свайп вкладок;
                        // по пустому месту вкладки по-прежнему листаются.
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    pieces.remove(dragged);
                    pieces.add(dragged);
                    grabDx = x - dragged.x;
                    grabDy = y - dragged.y;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (dragged != null) {
                    float nx = x - grabDx;
                    float ny = y - grabDy;
                    float pw = dragged.bmp.getWidth();
                    float ph = dragged.bmp.getHeight();
                    dragged.x = Math.max(-margin, Math.min(getWidth() - pw + margin, nx));
                    dragged.y = Math.max(-margin, Math.min(getHeight() - ph + margin, ny));
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (solved) {
                    float slop = 12 * density;
                    if (Math.abs(x - downX) < slop && Math.abs(y - downY) < slop) {
                        if (boardRect.contains(x, y)) {
                            playBounce();
                            if (listener != null) {
                                listener.onSolvedTapped();
                            }
                        } else if (trayRect.contains(x, y) && listener != null) {
                            listener.onTrayTappedWhenSolved();
                        }
                    }
                    return true;
                }
                if (dragged != null) {
                    Piece p = dragged;
                    dragged = null;
                    trySnap(p);
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                dragged = null;
                invalidate();
                return true;
            default:
                return true;
        }
    }

    /** Верхний из кусочков под пальцем: сама ячейка кусочка или непрозрачный пиксель выступа. */
    private Piece hit(float x, float y) {
        for (int i = pieces.size() - 1; i >= 0; i--) {
            Piece p = pieces.get(i);
            if (p.placed || p.snapping) {
                continue;
            }
            float lx = x - p.x;
            float ly = y - p.y;
            boolean inCell = lx >= margin && lx <= margin + cellW && ly >= margin && ly <= margin + cellH;
            if (inCell) {
                return p;
            }
            int ix = (int) lx;
            int iy = (int) ly;
            if (ix >= 0 && iy >= 0 && ix < p.bmp.getWidth() && iy < p.bmp.getHeight()
                    && (p.bmp.getPixel(ix, iy) >>> 24) > 40) {
                return p;
            }
        }
        return null;
    }

    private void trySnap(final Piece p) {
        float tol = SNAP_FRACTION * Math.min(cellW, cellH);
        float dx = p.slotX - p.x;
        float dy = p.slotY - p.y;
        if (Math.hypot(dx, dy) > tol) {
            return;
        }
        final float fromX = p.x;
        final float fromY = p.y;
        p.snapping = true;
        ValueAnimator a = ValueAnimator.ofFloat(0f, 1f);
        a.setDuration(SNAP_MS);
        a.setInterpolator(new DecelerateInterpolator());
        a.addUpdateListener(anim -> {
            float t = (float) anim.getAnimatedValue();
            p.x = fromX + (p.slotX - fromX) * t;
            p.y = fromY + (p.slotY - fromY) * t;
            invalidate();
        });
        a.addListener(new AnimatorListenerAdapter() {
            private boolean cancelled = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                cancelled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (cancelled || !p.snapping) {
                    return;
                }
                p.snapping = false;
                p.placed = true;
                p.x = p.slotX;
                p.y = p.slotY;
                invalidate();
                onPiecePlaced();
            }
        });
        // Пока кусочек едет, рисуем его как «в лотке» (он ещё не placed): порядок наложения тот же.
        a.start();
    }

    private void onPiecePlaced() {
        for (Piece p : pieces) {
            if (!p.placed) {
                if (listener != null) {
                    listener.onPieceSnapped();
                }
                return;
            }
        }
        solved = true;
        solvedT = 0f;
        solvedAnimator = ValueAnimator.ofFloat(0f, 1f);
        solvedAnimator.setDuration(SOLVED_MS);
        solvedAnimator.addUpdateListener(anim -> {
            solvedT = (float) anim.getAnimatedValue();
            bounce = 1f + 0.05f * (float) Math.sin(Math.PI * solvedT);
            invalidate();
        });
        solvedAnimator.start();
        if (listener != null) {
            listener.onSolved();
        }
    }

    private void playBounce() {
        if (solvedAnimator != null) {
            solvedAnimator.cancel();
        }
        solvedT = 1f;
        solvedAnimator = ValueAnimator.ofFloat(0f, 1f);
        solvedAnimator.setDuration(450);
        solvedAnimator.addUpdateListener(anim -> {
            float t = (float) anim.getAnimatedValue();
            bounce = 1f + 0.05f * (float) Math.sin(Math.PI * t);
            invalidate();
        });
        solvedAnimator.start();
    }
}
