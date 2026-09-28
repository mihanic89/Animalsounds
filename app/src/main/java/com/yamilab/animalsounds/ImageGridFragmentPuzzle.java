package com.yamilab.animalsounds;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;

/**
 * "Пазл": случайная картинка животного режется на кусочки, которые лежат в лотке (снизу
 * в портрете, справа в ландшафте) — их нужно перенести на поле-контур сверху/слева. Кусочек,
 * поднесённый почти к своему месту, примагничивается со звуком; по окончании звучит звук
 * животного, тап по собранной картинке его повторяет. Кнопка-подсказка включает контуры
 * отдельных кусочков на поле; внизу/сбоку выбирается число кусочков. Очки — число собранных
 * картинок.
 */
public class ImageGridFragmentPuzzle extends Fragment implements PuzzleView.Listener {

    private static final String KEY_SOLVED_COUNTER = "puzzleSolvedCounter";
    private static final String KEY_SIZE_LEVEL = "puzzleSizeLevel";
    private static final String KEY_OUTLINES = "puzzleOutlines";
    private static final int MAX_SOLVED_SHOWN = 999;

    // Состояние раунда на случай пересоздания фрагмента (поворот экрана): та же картинка и
    // те же уже поставленные кусочки, а не новое животное.
    private static final String STATE_ANIMAL_INDEX = "puzzleAnimalIndex";
    private static final String STATE_ANIMAL_NAME = "puzzleAnimalName";
    private static final String STATE_PLACED = "puzzlePlaced";
    private static final String STATE_HINT_USED = "puzzleHintUsed";

    // Диапазоны числа кусочков {мин, макс}: до 6 (2x3 / 3x2), до 16 (от 2x4 до 4x4) и, только
    // на планшетах, до 30. Конкретная сетка подбирается под пропорции картинки и экрана.
    private static final int[][] LEVEL_RANGES = {{6, 6}, {8, 16}, {20, 30}};
    // Пауза между звуком «собрано» и звуком животного (SoundPool играет один поток).
    private static final long ANIMAL_SOUND_DELAY_MS = 700;
    // Со салютом звучит дольше — звук животного начинается после него.
    private static final long ANIMAL_SOUND_DELAY_FIREWORKS_MS = 1900;
    // Сколько ждать без действий пользователя после сборки картинки, прежде чем перейти
    // к следующему заданию самостоятельно (тап по собранной картинке или по лотку откладывает
    // этот переход/делает его немедленным — см. onSolvedTapped/onTrayTappedWhenSolved).
    private static final long AUTO_NEXT_DELAY_MS = 5000;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable autoNextRunnable = this::autoNext;

    private ArrayList<Animal> animals;
    private FirebaseAnalytics mFirebaseAnalytics;

    private PuzzleView puzzleView;
    private Button counterView;
    private ImageButton hintButton;
    private final View[] sizeViews = new View[3];

    private int solvedTotal = 0;
    private int sizeLevel = 0;
    private int sizeLevelCount = 2;
    private boolean outlines = false;
    // Пользовались ли подсказкой (контурами) в этом раунде: салют только за сборку без неё.
    private boolean hintUsed = false;
    // Поставленные кусочки, восстановленные после пересоздания: применяются при первой нарезке.
    private boolean[] restoredPlaced;

    // Колода индексов животных: раунды идут по перемешанной колоде, чтобы картинки не повторялись
    // подряд.
    private final ArrayList<Integer> deck = new ArrayList<>();
    private Animal current;
    private Bitmap currentBitmap;
    private CustomTarget<Bitmap> loadTarget;
    // Токен раунда: устаревшие загрузки картинки и отложенные звуки отбрасываются.
    private int roundToken = 0;

    public ImageGridFragmentPuzzle() {
    }

    public static ImageGridFragmentPuzzle newInstance(ArrayList<Animal> array, int screenWidth) {
        ImageGridFragmentPuzzle fragment = new ImageGridFragmentPuzzle();
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
        View rootView = inflater.inflate(R.layout.fragment_puzzle, container, false);

        animals = BundleCompat.getSerializable(getArguments(), "key", ArrayList.class);
        if (animals == null || animals.isEmpty()) {
            return rootView;
        }

        puzzleView = rootView.findViewById(R.id.puzzleView);
        counterView = rootView.findViewById(R.id.puzzleCounter);
        hintButton = rootView.findViewById(R.id.puzzleHint);
        ImageButton nextButton = rootView.findViewById(R.id.puzzleNext);
        sizeViews[0] = rootView.findViewById(R.id.sizeSmall);
        sizeViews[1] = rootView.findViewById(R.id.sizeLarge);
        sizeViews[2] = rootView.findViewById(R.id.sizeExtra);

        boolean tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        sizeLevelCount = tablet ? 3 : 2;
        sizeViews[2].setVisibility(tablet ? View.VISIBLE : View.GONE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        solvedTotal = Math.min(prefs.getInt(KEY_SOLVED_COUNTER, 0), MAX_SOLVED_SHOWN);
        sizeLevel = Math.max(0, Math.min(prefs.getInt(KEY_SIZE_LEVEL, 0), sizeLevelCount - 1));
        outlines = prefs.getBoolean(KEY_OUTLINES, false);
        counterView.setText(String.valueOf(solvedTotal));

        hintButton.setContentDescription("Show piece outlines");
        nextButton.setContentDescription("Next picture");
        applyOutlines();
        hintButton.setOnClickListener(v -> {
            outlines = !outlines;
            hintUsed |= outlines;
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit().putBoolean(KEY_OUTLINES, outlines).apply();
            applyOutlines();
        });
        nextButton.setOnClickListener(v -> startRound());
        for (int i = 0; i < sizeViews.length; i++) {
            final int level = i;
            sizeViews[i].setOnClickListener(v -> selectSizeLevel(level));
        }

        puzzleView.setListener(this);
        if (!restoreRound(savedInstanceState)) {
            startRound();
        }
        return rootView;
    }

    private void applyOutlines() {
        puzzleView.setOutlinesVisible(outlines);
        hintButton.setAlpha(outlines ? 1f : 0.5f);
        hintButton.setSelected(outlines);
    }

    // ------------------------------------------------------------------ раунды

    /** Новая картинка: учитывается как раунд (реклама, аналитика). */
    private void startRound() {
        if (!isAdded() || animals == null || animals.isEmpty()) {
            return;
        }
        if (deck.isEmpty()) {
            for (int i = 0; i < animals.size(); i++) {
                deck.add(i);
            }
            Collections.shuffle(deck);
            // Не начинаем новую колоду с той же картинки, на которой закончилась прошлая.
            if (current != null && deck.size() > 1 && animals.get(deck.get(deck.size() - 1)) == current) {
                Collections.swap(deck, 0, deck.size() - 1);
            }
        }
        current = animals.get(deck.remove(deck.size() - 1));
        hintUsed = outlines;
        restoredPlaced = null;

        MainActivity activity = MainActivity.from(this);
        if (activity != null) {
            activity.incAdCounter();
            if (activity.getAdCounter() > MainActivity.adShowInt) {
                activity.showInterstitial();
                if (mFirebaseAnalytics != null) {
                    mFirebaseAnalytics.logEvent("puzzle_ad", null);
                }
            }
        }
        if (mFirebaseAnalytics != null) {
            mFirebaseAnalytics.logEvent("new_round_puzzle", null);
        }
        loadCurrentImage();
    }

    private void loadCurrentImage() {
        final int token = ++roundToken;
        uiHandler.removeCallbacksAndMessages(null);
        clearLoad();
        currentBitmap = null;
        loadTarget = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (!isAdded() || token != roundToken) {
                    return;
                }
                // Свой экземпляр: Glide может вернуть resource в пул после clear() цели, а пазл
                // держит картинку на всё время раунда (пересчёт при смене размера).
                Bitmap own = resource.copy(Bitmap.Config.ARGB_8888, false);
                currentBitmap = own != null ? own : resource;
                cut();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        };
        GlideApp.with(this).asBitmap().load(current.getImageSmall()).into(loadTarget);
    }

    private void clearLoad() {
        if (loadTarget != null) {
            GlideApp.with(this).clear(loadTarget);
            loadTarget = null;
        }
    }

    /** Нарезает текущую картинку по выбранному размеру (без учёта как нового раунда). */
    private void cut() {
        if (currentBitmap == null || puzzleView == null) {
            return;
        }
        float aspect = currentBitmap.getHeight() / (float) currentBitmap.getWidth();
        for (int i = 0; i < sizeLevelCount; i++) {
            int[] g = gridFor(i, aspect);
            sizeViews[i].setContentDescription(g[0] * g[1] + " pieces");
            sizeViews[i].setSelected(sizeLevel == i);
        }
        int[] grid = gridFor(sizeLevel, aspect);
        puzzleView.setPuzzle(currentBitmap, grid[0], grid[1], restoredPlaced);
        restoredPlaced = null;
    }

    /**
     * Сетка колонки×строки с числом кусочков из диапазона уровня, у которой ячейки ближе всего
     * к квадрату для картинки данной пропорции (высота/ширина). При равенстве — меньше кусочков.
     */
    private static int[] gridFor(int level, float aspect) {
        int[] range = LEVEL_RANGES[level];
        int[] best = {2, 3};
        double bestScore = Double.MAX_VALUE;
        for (int cols = 2; cols <= 8; cols++) {
            for (int rows = 2; rows <= 8; rows++) {
                int count = cols * rows;
                if (count < range[0] || count > range[1]) {
                    continue;
                }
                // Ячейка ближе к квадрату лучше, но при почти равных вариантах берём больше кусочков.
                double score = Math.abs(Math.log(aspect * cols / rows)) - 0.08 * Math.log(count);
                if (score < bestScore - 1e-9) {
                    bestScore = score;
                    best = new int[]{cols, rows};
                }
            }
        }
        return best;
    }

    private void selectSizeLevel(int level) {
        if (level == sizeLevel) {
            return;
        }
        sizeLevel = level;
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .edit().putInt(KEY_SIZE_LEVEL, level).apply();
        if (puzzleView != null && puzzleView.isSolved()) {
            // Собранную картинку не пересобираем заново (это давало бы очки за одну и ту же
            // картинку): выбор размера после победы начинает новый раунд с новым животным.
            startRound();
            return;
        }
        // Посреди игры — та же картинка, другая нарезка.
        uiHandler.removeCallbacksAndMessages(null);
        cut();
    }

    // ------------------------------------------------------------------ PuzzleView.Listener

    @Override
    public void onPieceSnapped() {
        if (isAdded()) {
            SoundPlay.playSP(requireContext(), R.raw.puzzle_snap);
        }
    }

    @Override
    public void onSolved() {
        if (!isAdded()) {
            return;
        }
        solvedTotal = Math.min(solvedTotal + 1, MAX_SOLVED_SHOWN);
        counterView.setText(String.valueOf(solvedTotal));
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .edit().putInt(KEY_SOLVED_COUNTER, solvedTotal).apply();
        MainActivity activity = MainActivity.from(this);
        if (activity != null) {
            activity.incrementUnlockCounter();
        }
        boolean fireworks = !hintUsed;
        if (fireworks) {
            SoundPlay.playSP(requireContext(), R.raw.puzzle_fireworks);
            puzzleView.launchFireworks();
        } else {
            SoundPlay.playSP(requireContext(), R.raw.correct);
        }
        final int token = roundToken;
        uiHandler.postDelayed(() -> {
            // isResumed: ViewPager2 держит RESUMED только текущую страницу — после ухода с
            // вкладки звук животного не должен играть поверх другой.
            if (isAdded() && isResumed() && token == roundToken) {
                playAnimalSound();
            }
        }, fireworks ? ANIMAL_SOUND_DELAY_FIREWORKS_MS : ANIMAL_SOUND_DELAY_MS);
        scheduleAutoNext();
    }

    @Override
    public void onSolvedTapped() {
        playAnimalSound();
        // Повтор звука — тоже действие пользователя: не переходим дальше, пока он ещё смотрит.
        scheduleAutoNext();
    }

    @Override
    public void onTrayTappedWhenSolved() {
        // Тап по пустому месту в лотке на собранной картинке — понятный жест «дальше», не ждём таймер.
        startRound();
    }

    private void playAnimalSound() {
        if (current != null && current.getSound() != null && isAdded()) {
            SoundPlay.playSP(requireContext(), current.getSound());
        }
    }

    /** Через AUTO_NEXT_DELAY_MS без действий пользователя переходит к следующему заданию. */
    private void scheduleAutoNext() {
        if (!isAdded()) {
            return;
        }
        uiHandler.removeCallbacks(autoNextRunnable);
        uiHandler.postDelayed(autoNextRunnable, AUTO_NEXT_DELAY_MS);
    }

    private void autoNext() {
        // isResumed: не листаем картинку самостоятельно, пока пользователь смотрит другую вкладку.
        if (isAdded() && isResumed() && puzzleView != null && puzzleView.isSolved()) {
            startRound();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (current != null && animals != null && puzzleView != null) {
            outState.putInt(STATE_ANIMAL_INDEX, animals.indexOf(current));
            outState.putString(STATE_ANIMAL_NAME, current.getName());
            outState.putBooleanArray(STATE_PLACED, puzzleView.getPlacedState());
            outState.putBoolean(STATE_HINT_USED, hintUsed);
        }
    }

    /** Возвращает раунд, сохранённый при пересоздании (поворот экрана); false — начинать новый. */
    private boolean restoreRound(@Nullable Bundle state) {
        if (state == null || !state.containsKey(STATE_ANIMAL_INDEX)) {
            return false;
        }
        int index = state.getInt(STATE_ANIMAL_INDEX, -1);
        if (index < 0 || index >= animals.size()
                || !animals.get(index).getName().equals(state.getString(STATE_ANIMAL_NAME))) {
            return false;
        }
        current = animals.get(index);
        hintUsed = state.getBoolean(STATE_HINT_USED, false) || outlines;
        restoredPlaced = state.getBooleanArray(STATE_PLACED);
        loadCurrentImage();
        return true;
    }

    @Override
    public void onDestroyView() {
        roundToken++;
        uiHandler.removeCallbacksAndMessages(null);
        clearLoad();
        currentBitmap = null;
        puzzleView = null;
        super.onDestroyView();
    }
}
