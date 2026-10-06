package com.yamilab.animalsounds;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;

/**
 * "Память": на поле лежат рубашкой вверх парные карточки с животными — нужно открывать
 * по две и находить пары. Размер поля подбирается под ориентацию и размер экрана, внизу
 * можно переключиться между двумя вариантами (меньше/больше карточек). Очки — общее
 * число собранных пар; когда все пары раскладки найдены, тут же замешивается новая.
 */
public class ImageGridFragmentMemory extends Fragment {

    private static final String KEY_PAIRS_COUNTER = "memoryPairsCounter";
    private static final String KEY_SIZE_LEVEL = "memorySizeLevel";

    // Карточки с картинками 400x591 — держим ту же пропорцию у рубашки, чтобы
    // лицевая сторона не имела полей и не "прыгала" размером при переворачивании.
    // Верхний предел счётчика на экране (кнопка-«таблетка» рассчитана на три цифры).
    private static final int MAX_PAIRS_SHOWN = 999;

    private static final float CARD_ASPECT = 591f / 400f;
    private static final int GAP_DP = 8;
    private static final int CORNER_DP = 16;

    private static final long FLIP_HALF_MS = 120;
    // Сколько после открытия второй карточки держим её на виду перед закрытием пары.
    private static final long MISMATCH_SHOW_MS = 800;
    // Пауза после последней найденной пары перед новой раскладкой.
    private static final long NEW_DEAL_DELAY_MS = 1100;

    // Диапазоны числа карточек {мин, макс} для двух базовых размеров (одинаковы для телефона
    // и планшета): не больше 8 и не больше 16. Точное чётное число и сетка подбираются под
    // размер поля — см. bestGrid(). На планшете (от 7", smallestScreenWidthDp >= 600)
    // добавляется третий размер: средняя сетка плюс ряд и колонка — см. enlarge().
    private static final int[] RANGE_SMALL = {6, 8};
    private static final int[] RANGE_MEDIUM = {12, 16};

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private ArrayList<Animal> animals;
    private FirebaseAnalytics mFirebaseAnalytics;

    private FrameLayout board;
    private Button pairsCounterView;
    private final TextView[] sizeViews = new TextView[3];

    private final ArrayList<Card> cards = new ArrayList<>();
    private int pairsTotal = 0;
    private int sizeLevel = 0;
    private int sizeLevelCount = 2;
    private int firstOpen = -1;
    // true, пока разбирается пара / идёт новая раскладка: тапы по карточкам игнорируются.
    private boolean inputLocked = false;
    private int pairsLeft = 0;
    private boolean boardBuilt = false;
    // Размер поля, под который сейчас расставлены карточки: если он поменялся (например,
    // загрузился рекламный баннер и контейнер стал ниже) — карточки надо перерасставить.
    private int builtBoardW = 0;
    private int builtBoardH = 0;
    // Сетки для каждого уровня размера, посчитанные при последней раздаче: подписи кнопок и
    // текущая раскладка берутся отсюда, поэтому всегда совпадают друг с другом.
    private final int[][] levelGrids = new int[3][];
    private int gridCols = 0;
    private int gridRows = 0;
    private boolean tablet = false;
    // Токен раскладки: отложенные колбэки и анимации от прошлой раскладки сами
    // становятся пустыми, если раскладка успела смениться (смена размера, новая партия).
    private int dealToken = 0;

    private static final class Card {
        FrameLayout view;
        ImageView back;
        ImageView front;
        int animalIndex;
        boolean faceUp;
        boolean matched;
    }

    public ImageGridFragmentMemory() {
    }

    public static ImageGridFragmentMemory newInstance(ArrayList<Animal> array, int screenWidth) {
        ImageGridFragmentMemory fragment = new ImageGridFragmentMemory();
        Bundle args = new Bundle();
        args.putSerializable("key", array);
        args.putInt("width", screenWidth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext().getApplicationContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_memory, container, false);

        animals = BundleCompat.getSerializable(getArguments(), "key", ArrayList.class);
        if (animals == null || animals.size() < 2) {
            return rootView;
        }

        board = rootView.findViewById(R.id.memoryBoard);
        pairsCounterView = rootView.findViewById(R.id.pairsCounter);
        sizeViews[0] = rootView.findViewById(R.id.sizeSmall);
        sizeViews[1] = rootView.findViewById(R.id.sizeLarge);
        sizeViews[2] = rootView.findViewById(R.id.sizeExtra);

        tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        sizeLevelCount = tablet ? 3 : 2;
        sizeViews[2].setVisibility(tablet ? View.VISIBLE : View.GONE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        pairsTotal = prefs.getInt(KEY_PAIRS_COUNTER, 0);
        pairsTotal = Math.min(pairsTotal, MAX_PAIRS_SHOWN);
        sizeLevel = Math.max(0, Math.min(prefs.getInt(KEY_SIZE_LEVEL, 0), sizeLevelCount - 1));
        pairsCounterView.setText(String.valueOf(pairsTotal));

        for (int i = 0; i < sizeViews.length; i++) {
            final int level = i;
            sizeViews[i].setOnClickListener(v -> selectSizeLevel(level));
        }

        // Размер поля известен только после layout: первый раз — раздаём карточки, потом
        // следим за изменением (например, когда загрузился баннер и контейнер стал ниже).
        board.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) ->
                onBoardLaidOut(r - l, b - t));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Подстраховка: если layout прошёл раньше, чем фрагмент стал "added",
        // слушатель выше карточки не раздал — делаем это здесь.
        if (board != null) {
            onBoardLaidOut(board.getWidth(), board.getHeight());
        }
    }

    private void onBoardLaidOut(int w, int h) {
        if (!isAdded() || w <= 0 || h <= 0) {
            return;
        }
        if (!boardBuilt) {
            boardBuilt = true;
            builtBoardW = w;
            builtBoardH = h;
            // post: нельзя добавлять view прямо внутри колбэка layout — раздаём после него.
            board.post(() -> deal(false));
        } else if (w != builtBoardW || h != builtBoardH) {
            builtBoardW = w;
            builtBoardH = h;
            board.post(this::onBoardResized);
        }
    }

    /**
     * Поле изменило размер после раздачи. Если игрок ещё ни во что не играл — просто
     * раздаём заново под новый размер (заодно пересчитываются размеры на кнопках); иначе
     * не сбиваем партию и только перерасставляем уже лежащие карточки.
     */
    private void onBoardResized() {
        if (!isAdded() || cards.isEmpty()) {
            return;
        }
        boolean untouched = true;
        for (Card c : cards) {
            if (c.faceUp || c.matched) {
                untouched = false;
                break;
            }
        }
        if (untouched) {
            rebuild(false);
        } else {
            positionCards();
        }
    }

    private int[] gridForLevel(int level) {
        if (level == 0) {
            return bestGrid(RANGE_SMALL);
        }
        int[] medium = bestGrid(RANGE_MEDIUM);
        return level == 1 ? medium : enlarge(medium);
    }

    /**
     * Третий (планшетный) размер: средняя сетка плюс одна колонка и один ряд. Если карточек
     * получилось нечётное число (пары не сложить), добавляем ещё колонку или ряд — туда,
     * где карточка выходит крупнее.
     */
    private int[] enlarge(int[] grid) {
        int gap = Math.round(GAP_DP * getResources().getDisplayMetrics().density);
        int cols = grid[0] + 1;
        int rows = grid[1] + 1;
        if ((cols * rows) % 2 != 0) {
            if (cardWidthFor(cols + 1, rows, gap) >= cardWidthFor(cols, rows + 1, gap)) {
                cols++;
            } else {
                rows++;
            }
        }
        return new int[]{cols, rows};
    }

    /**
     * Перебирает сетки колонки×строки с чётным числом карточек из диапазона и берёт ту,
     * что закрывает карточками наибольшую площадь поля (размер карточки × их число, с
     * учётом пропорции и отступов). Так поле заполняется без больших пустых мест: на
     * низком широком ландшафте получается, например, 6×2, на высоком портретном 3×4, а
     * на планшете число карточек подстраивается под форму экрана. При равенстве
     * берётся вариант с меньшим числом карточек.
     */
    private int[] bestGrid(int[] countRange) {
        int gap = Math.round(GAP_DP * getResources().getDisplayMetrics().density);
        int[] best = {2, countRange[0] / 2};
        float bestCovered = -1;
        for (int count = countRange[0]; count <= countRange[1]; count++) {
            if (count % 2 != 0) {
                continue;
            }
            for (int cols = 1; cols <= count; cols++) {
                if (count % cols != 0) {
                    continue;
                }
                int rows = count / cols;
                float width = cardWidthFor(cols, rows, gap);
                float covered = width * width * CARD_ASPECT * count;
                if (covered > bestCovered) {
                    bestCovered = covered;
                    best = new int[]{cols, rows};
                }
            }
        }
        return best;
    }

    private float cardWidthFor(int cols, int rows, int gap) {
        float maxW = (board.getWidth() - (cols + 1) * gap) / (float) cols;
        float maxH = (board.getHeight() - (rows + 1) * gap) / (float) rows / CARD_ASPECT;
        return Math.min(maxW, maxH);
    }

    private void updateSizeLabels() {
        for (int i = 0; i < sizeLevelCount; i++) {
            int[] grid = gridForLevel(i);
            levelGrids[i] = grid;
            sizeViews[i].setText(grid[0] + "×" + grid[1]);
            sizeViews[i].setSelected(sizeLevel == i);
        }
    }

    private void selectSizeLevel(int level) {
        if (level == sizeLevel || !boardBuilt) {
            return;
        }
        sizeLevel = level;
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .edit().putInt(KEY_SIZE_LEVEL, level).apply();
        deal(false);
    }

    /** Новая партия: учитывается как раунд (реклама, аналитика) и раздаётся заново. */
    private void deal(boolean animate) {
        if (!isAdded() || board == null || animals == null) {
            return;
        }
        rebuild(animate);

        MainActivity activity = MainActivity.from(this);
        if (activity != null) {
            activity.incAdCounter();
            if (activity.getAdCounter() > MainActivity.adShowInt) {
                activity.showInterstitial();
                if (mFirebaseAnalytics != null) {
                    mFirebaseAnalytics.logEvent("memory_ad", null);
                }
            }
        }
        if (mFirebaseAnalytics != null) {
            mFirebaseAnalytics.logEvent("new_round_memory", null);
        }
    }

    /** Замешивает и раскладывает новый набор карточек под текущий размер поля. */
    private void rebuild(boolean animate) {
        if (!isAdded() || board == null || animals == null) {
            return;
        }
        final int token = ++dealToken;
        uiHandler.removeCallbacksAndMessages(null);
        inputLocked = true;
        firstOpen = -1;

        Runnable build = () -> {
            if (!isAdded() || token != dealToken) {
                return;
            }
            updateSizeLabels();
            buildBoard();
            inputLocked = false;
            if (animate) {
                board.animate().alpha(1f).setDuration(180).start();
            } else {
                board.setAlpha(1f);
            }
        };

        if (animate) {
            board.animate().alpha(0f).setDuration(180).withEndAction(build).start();
        } else {
            board.animate().cancel();
            build.run();
        }
    }

    private void buildBoard() {
        for (Card c : cards) {
            c.view.animate().cancel();
        }
        board.removeAllViews();
        cards.clear();

        int[] grid = levelGrids[sizeLevel];
        gridCols = grid[0];
        gridRows = grid[1];
        int pairs = Math.min(gridCols * gridRows / 2, animals.size());

        // Выбираем случайные различные виды животных и удваиваем — каждая пара это два
        // экземпляра одного и того же животного; затем перемешиваем позиции.
        ArrayList<Integer> pool = new ArrayList<>();
        for (int i = 0; i < animals.size(); i++) {
            pool.add(i);
        }
        Collections.shuffle(pool);
        ArrayList<Integer> deck = new ArrayList<>();
        for (int i = 0; i < pairs; i++) {
            deck.add(pool.get(i));
            deck.add(pool.get(i));
        }
        Collections.shuffle(deck);
        pairsLeft = pairs;

        for (int i = 0; i < deck.size(); i++) {
            final int position = i;
            Card card = new Card();
            card.animalIndex = deck.get(i);

            // Абсолютные (LEFT/TOP) координаты: в RTL-локалях Gravity.START поставил бы
            // все карточки к правому краю и проигнорировал бы leftMargin.
            card.view = new FrameLayout(board.getContext());

            card.front = new ImageView(board.getContext());
            card.front.setScaleType(ImageView.ScaleType.FIT_CENTER);
            card.front.setVisibility(View.INVISIBLE);
            card.front.setContentDescription(animals.get(card.animalIndex).getName());
            card.view.addView(card.front, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            card.back = new ImageView(board.getContext());
            card.back.setScaleType(ImageView.ScaleType.FIT_XY);
            card.back.setImageResource(R.drawable.memory_card_back);
            card.view.addView(card.back, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            card.view.setOnClickListener(v -> onCardTapped(position));
            board.addView(card.view);
            cards.add(card);
        }

        positionCards();

        // Лицевую сторону подгружаем сразу (пока карточка закрыта), чтобы при
        // переворачивании картинка уже была готова, а не появлялась с задержкой.
        // FitCenter до RoundedCorners: скругляется уже картинка нужного размера,
        // поэтому радиус углов совпадает с рубашкой.
        int radius = Math.round(CORNER_DP * getResources().getDisplayMetrics().density);
        int cardW = cards.get(0).view.getLayoutParams().width;
        int cardH = cards.get(0).view.getLayoutParams().height;
        for (Card card : cards) {
            GlideApp.with(this)
                    .load(animals.get(card.animalIndex).getImageSmall())
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(cardW, cardH)
                    .transform(new FitCenter(), new RoundedCorners(radius))
                    .into(card.front);
        }
    }

    /**
     * Ставит уже созданные карточки в сетку gridCols×gridRows по текущему размеру поля:
     * размер карточки ограничен и шириной, и (с учётом пропорции) высотой поля. Вызывается
     * и при раздаче, и при изменении размера поля посреди партии.
     */
    private void positionCards() {
        if (cards.isEmpty() || gridCols == 0 || gridRows == 0) {
            return;
        }
        int gap = Math.round(GAP_DP * getResources().getDisplayMetrics().density);
        int cardW = Math.max(1, (int) cardWidthFor(gridCols, gridRows, gap));
        int cardH = Math.round(cardW * CARD_ASPECT);
        int gridW = gridCols * cardW + (gridCols - 1) * gap;
        int gridH = gridRows * cardH + (gridRows - 1) * gap;
        int offsetX = (board.getWidth() - gridW) / 2;
        int offsetY = (board.getHeight() - gridH) / 2;

        for (int i = 0; i < cards.size(); i++) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(cardW, cardH);
            lp.gravity = Gravity.LEFT | Gravity.TOP;
            lp.leftMargin = offsetX + (i % gridCols) * (cardW + gap);
            lp.topMargin = offsetY + (i / gridCols) * (cardH + gap);
            cards.get(i).view.setLayoutParams(lp);
        }
    }

    private void onCardTapped(int position) {
        if (inputLocked || position >= cards.size()) {
            return;
        }
        Card card = cards.get(position);
        if (card.faceUp || card.matched) {
            return;
        }

        SoundPlay.playSP(requireContext(), R.raw.card_flip);
        setFace(card, true, null);

        if (firstOpen < 0) {
            firstOpen = position;
            return;
        }

        final int first = firstOpen;
        final int second = position;
        firstOpen = -1;
        inputLocked = true;
        final int token = dealToken;

        // Ждём, пока вторая карточка докрутится, и только потом объявляем результат.
        uiHandler.postDelayed(() -> {
            if (!isAdded() || token != dealToken) {
                return;
            }
            resolvePair(first, second, token);
        }, FLIP_HALF_MS * 2 + 40);
    }

    private void resolvePair(int first, int second, int token) {
        Card a = cards.get(first);
        Card b = cards.get(second);

        if (a.animalIndex == b.animalIndex) {
            a.matched = true;
            b.matched = true;
            pairsTotal = Math.min(pairsTotal + 1, MAX_PAIRS_SHOWN);
            pairsLeft--;
            pairsCounterView.setText(String.valueOf(pairsTotal));
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit().putInt(KEY_PAIRS_COUNTER, pairsTotal).apply();
            MainActivity activity = MainActivity.from(this);
            if (activity != null) {
                activity.incrementUnlockCounter();
            }
            if (mFirebaseAnalytics != null) {
                Bundle params = new Bundle();
                params.putLong("size_level", sizeLevel);
                mFirebaseAnalytics.logEvent("memory_pair", params);
            }
            SoundPlay.playSP(requireContext(), R.raw.correct);
            pulse(a);
            pulse(b);

            if (pairsLeft <= 0) {
                uiHandler.postDelayed(() -> {
                    if (isAdded() && token == dealToken) {
                        deal(true);
                    }
                }, NEW_DEAL_DELAY_MS);
            } else {
                inputLocked = false;
            }
        } else {
            SoundPlay.playSP(requireContext(), R.raw.error);
            uiHandler.postDelayed(() -> {
                if (!isAdded() || token != dealToken) {
                    return;
                }
                SoundPlay.playSP(requireContext(), R.raw.card_flip);
                setFace(a, false, null);
                setFace(b, false, () -> {
                    if (isAdded() && token == dealToken) {
                        inputLocked = false;
                    }
                });
            }, MISMATCH_SHOW_MS);
        }
    }

    /** Переворот: сжимаем карточку по X до нуля, меняем сторону и разворачиваем обратно. */
    private void setFace(Card card, boolean faceUp, Runnable onDone) {
        card.faceUp = faceUp;
        final int token = dealToken;
        card.view.animate().cancel();
        card.view.animate().scaleX(0f).setDuration(FLIP_HALF_MS).withEndAction(() -> {
            if (!isAdded() || token != dealToken) {
                return;
            }
            card.front.setVisibility(faceUp ? View.VISIBLE : View.INVISIBLE);
            card.back.setVisibility(faceUp ? View.INVISIBLE : View.VISIBLE);
            card.view.animate().scaleX(1f).setDuration(FLIP_HALF_MS).withEndAction(() -> {
                if (onDone != null) {
                    onDone.run();
                }
            }).start();
        }).start();
    }

    private void pulse(Card card) {
        card.view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(140)
                .withEndAction(() -> card.view.animate().scaleX(1f).scaleY(1f).setDuration(140).start())
                .start();
    }

    @Override
    public void onDestroyView() {
        dealToken++;
        uiHandler.removeCallbacksAndMessages(null);
        for (Card c : cards) {
            c.view.animate().cancel();
        }
        cards.clear();
        boardBuilt = false;
        super.onDestroyView();
    }
}
