package com.yamilab.animalsounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Random;

/**
 * "Крот": по норам в случайном порядке ненадолго появляются картинки животных.
 * Перед раундом проигрывается звук цели — нужно тапнуть по норе, в которой в этот
 * момент показано именно это животное, пока оно не спряталось обратно.
 */
public class ImageGridFragmentGame4 extends Fragment {

    private static final int HOLE_COUNT = 4;
    private static final String KEY_CORRECT_COUNTER = "correctCounter4";
    private static final String KEY_WRONG_COUNTER = "wrongCounter4";

    // Сколько зверей могут показываться одновременно. Спавн проверяется по одному
    // за тик (см. spawnMole), поэтому вторая картинка появляется с задержкой
    // относительно первой, а не в тот же момент.
    private static final int MAX_ACTIVE_HOLES = 2;
    // Пауза между попытками заспавнить нового зверя. Рассчитано на маленьких детей,
    // но чуть живее самой первой (слишком медленной) версии.
    private static final int SPAWN_MIN_DELAY_MS = 800;
    private static final int SPAWN_MAX_DELAY_MS = 1800;
    // Как долго зверь виден, прежде чем спрятаться; медленно уменьшается по ходу сессии.
    private static final long POP_DURATION_START_MS = 6000;
    private static final long POP_DURATION_FLOOR_MS = 3000;
    private static final long POP_DURATION_STEP_MS = 120;
    // Пауза перед стартом следующего раунда после правильного ответа.
    private static final long NEXT_ROUND_DELAY_MS = 2000;
    // Как часто (в среднем) среди спавнов должно попадаться именно целевое животное.
    private static final int CORRECT_SPAWN_CHANCE_PERCENT = 40;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private ArrayList<Animal> animals;
    private TTSListener ttsListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    private final ImageButton[] moleButtons = new ImageButton[HOLE_COUNT];
    private final int[] moleAnimalIndex = new int[HOLE_COUNT];
    // Токен "поколения" показа для каждой норы: отложенный hideMole() из showMole()
    // прячет нору только если с момента показа её никто больше не трогал (не было
    // ни нового спавна, ни ручного скрытия) — иначе протухший колбэк мог бы погасить
    // уже другое, свежее животное в той же норе (например, из следующего раунда).
    private final int[] moleShowToken = new int[HOLE_COUNT];

    private Button correctCounterView;
    private Button wrongCounterView;
    private ImageButton buttonSound;

    private int correctInt = 0;
    private int wrongInt = 0;
    private boolean wrongHasTry = false;
    private boolean roundActive = false;
    private int correctAnswerIndex = 0;
    private int roundsThisSession = 0;
    private int screenWidth = 0;
    // Как в Game/Game2/Game3: чтобы одно и то же животное не выпадало верным
    // ответом раунд за раундом подряд (см. AnimalGamePicker.pickNext).
    private final ArrayList<Integer> recentlyUsed = new ArrayList<>();

    public ImageGridFragmentGame4() {
    }

    public static ImageGridFragmentGame4 newInstance(ArrayList<Animal> array, int screenWidth) {
        ImageGridFragmentGame4 fragment = new ImageGridFragmentGame4();
        Bundle args = new Bundle();
        args.putSerializable("key", array);
        args.putInt("width", screenWidth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity() != null ? getActivity() : getContext();
        if (ttsListener == null && context instanceof TTSListener) {
            ttsListener = (TTSListener) context;
        }
        if (getContext() != null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext().getApplicationContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game4, container, false);

        animals = BundleCompat.getSerializable(getArguments(), "key", ArrayList.class);
        if (animals == null || animals.isEmpty()) {
            return rootView;
        }
        screenWidth = getArguments() != null ? getArguments().getInt("width", 0) : 0;

        moleButtons[0] = rootView.findViewById(R.id.mole0);
        moleButtons[1] = rootView.findViewById(R.id.mole1);
        moleButtons[2] = rootView.findViewById(R.id.mole2);
        moleButtons[3] = rootView.findViewById(R.id.mole3);

        correctCounterView = rootView.findViewById(R.id.correctCounter);
        wrongCounterView = rootView.findViewById(R.id.wrongCounter);
        buttonSound = rootView.findViewById(R.id.buttonSound);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        correctInt = prefs.getInt(KEY_CORRECT_COUNTER, 0);
        wrongInt = prefs.getInt(KEY_WRONG_COUNTER, 0);
        if (correctInt > 999 || wrongInt > 999) {
            correctInt = 0;
            wrongInt = 0;
        }
        correctCounterView.setText(String.valueOf(correctInt));
        wrongCounterView.setText(String.valueOf(wrongInt));

        for (int i = 0; i < HOLE_COUNT; i++) {
            final int hole = i;
            moleButtons[i].setOnClickListener(v -> onMoleTapped(hole));
        }

        buttonSound.setOnClickListener(v -> {
            if (getContext() != null) {
                SoundPlay.playSP(getContext(), animals.get(correctAnswerIndex).getSound());
            }
        });

        startRound();

        return rootView;
    }

    private void startRound() {
        if (!isAdded() || animals == null || animals.isEmpty()) {
            return;
        }

        // Отменяем всё, что осталось от предыдущего раунда (в первую очередь —
        // отложенный hideMole() из showMole(), который иначе мог сработать уже после
        // того, как эта нора получила новое животное, и погасить его раньше времени).
        uiHandler.removeCallbacksAndMessages(null);

        wrongHasTry = false;
        roundActive = true;
        roundsThisSession++;
        hideAllMoles();

        MainActivity activity = MainActivity.from(this);
        if (activity != null) {
            activity.incAdCounter();
            if (activity.getAdCounter() > MainActivity.adShowInt) {
                activity.showInterstitial();
                mFirebaseAnalytics.logEvent("game4_ad", null);
            }
        }

        correctAnswerIndex = AnimalGamePicker.pickNext(animals, recentlyUsed)[0];
        SoundPlay.playSP(getContext(), animals.get(correctAnswerIndex).getSound());

        scheduleNextSpawn();

        Bundle params = new Bundle();
        params.putString("new_round4", "New round start 4");
        mFirebaseAnalytics.logEvent("new_round_4", params);
    }

    private void scheduleNextSpawn() {
        if (!isAdded() || !roundActive) {
            return;
        }
        int delay = SPAWN_MIN_DELAY_MS + random.nextInt(SPAWN_MAX_DELAY_MS - SPAWN_MIN_DELAY_MS);
        uiHandler.postDelayed(this::spawnMole, delay);
    }

    private void spawnMole() {
        if (!isAdded() || !roundActive) {
            return;
        }

        ArrayList<Integer> idleHoles = new ArrayList<>();
        int activeCount = 0;
        boolean correctAlreadyShown = false;
        for (int i = 0; i < HOLE_COUNT; i++) {
            if (moleAnimalIndex[i] == -1) {
                idleHoles.add(i);
            } else {
                activeCount++;
                if (moleAnimalIndex[i] == correctAnswerIndex) {
                    correctAlreadyShown = true;
                }
            }
        }

        if (!idleHoles.isEmpty() && activeCount < MAX_ACTIVE_HOLES) {
            int hole = idleHoles.get(random.nextInt(idleHoles.size()));
            // Верный ответ не должен появляться в двух норах одновременно: и когда мы
            // намеренно показываем decoy, и когда решаем НЕ показывать верного зверя,
            // индекс должен гарантированно не совпасть с correctAnswerIndex.
            boolean showCorrect = !correctAlreadyShown && random.nextInt(100) < CORRECT_SPAWN_CHANCE_PERCENT;
            int animalIndex = showCorrect ? correctAnswerIndex : randomDecoyIndex();
            showMole(hole, animalIndex);
        }

        scheduleNextSpawn();
    }

    private int randomDecoyIndex() {
        if (animals.size() <= 1) {
            return correctAnswerIndex;
        }
        return AnimalGamePicker.nextExcluding(animals.size(), correctAnswerIndex);
    }

    private void showMole(int hole, int animalIndex) {
        moleAnimalIndex[hole] = animalIndex;
        setImageGlide(moleButtons[hole], animals.get(animalIndex).getImageSmall());

        int token = ++moleShowToken[hole];
        long duration = currentPopDurationMs();
        uiHandler.postDelayed(() -> hideMoleIfStillCurrent(hole, token), duration);
    }

    private void hideMoleIfStillCurrent(int hole, int expectedToken) {
        if (!isAdded() || moleShowToken[hole] != expectedToken) {
            return;
        }
        hideMoleNow(hole);
    }

    private void hideMoleNow(int hole) {
        moleShowToken[hole]++;
        moleAnimalIndex[hole] = -1;
        if (moleButtons[hole] != null) {
            moleButtons[hole].setImageDrawable(null);
        }
    }

    private void hideAllMoles() {
        for (int i = 0; i < HOLE_COUNT; i++) {
            hideMoleNow(i);
        }
    }

    private long currentPopDurationMs() {
        long duration = POP_DURATION_START_MS - (long) roundsThisSession * POP_DURATION_STEP_MS;
        return Math.max(duration, POP_DURATION_FLOOR_MS);
    }

    private void onMoleTapped(int hole) {
        if (!roundActive) {
            return;
        }

        int animalIndex = moleAnimalIndex[hole];

        // Пустая нора — мимо, а не ошибка: почти все норы пустуют в любой момент
        // времени (одновременно видно максимум MAX_ACTIVE_HOLES зверей), и штрафовать
        // за тычок в пустоту так же, как за тычок в неверного зверя, было бы слишком
        // строго для этой игры.
        if (animalIndex == -1) {
            return;
        }

        if (animalIndex == correctAnswerIndex) {
            roundActive = false;
            uiHandler.removeCallbacksAndMessages(null);
            // Верный ответ засчитывается всегда, даже если до этого в раунде уже был
            // промах — в отличие от викторин, здесь промах по пустой норе или по
            // decoy случается почти в каждом раунде, и это не должно "сжигать" очко.
            correctInt++;
            correctCounterView.setText(String.valueOf(correctInt));
            saveInt(KEY_CORRECT_COUNTER, correctInt);
            MainActivity activity = MainActivity.from(this);
            if (activity != null) {
                activity.incrementUnlockCounter();
            }

            SoundPlay.playSP(getContext(), R.raw.correct);
            hideAllMoles();
            showMole(hole, animalIndex);
            if (ttsListener != null) {
                ttsListener.speak(animals.get(animalIndex).getName(), animals.get(animalIndex).getSound());
            }

            uiHandler.postDelayed(this::startRound, NEXT_ROUND_DELAY_MS);
        } else {
            setWrongInt();
            SoundPlay.playSP(getContext(), R.raw.error);
            hideMoleNow(hole);
        }
    }

    private void setImageGlide(ImageView imageView, int image) {
        int radius = (int) (16 * getResources().getDisplayMetrics().density);
        // Уменьшаем картинку до размера ячейки ДО скругления — иначе Glide скругляет
        // углы на полноразмерном оригинале, а fitCenter потом сжимает уже готовый
        // (скруглённый) битмап вместе с рамкой, из-за чего радиус визуально пропадает.
        int targetWidth = screenWidth > 0 ? screenWidth / 3 : 400;
        GlideApp.with(imageView.getContext())
                .load(image)
                .priority(Priority.LOW)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new RoundedCorners(radius))
                .apply(new RequestOptions().override(targetWidth))
                .into(imageView);
    }

    private void setWrongInt() {
        if (!wrongHasTry) {
            wrongInt++;
            wrongCounterView.setText(String.valueOf(wrongInt));
            saveInt(KEY_WRONG_COUNTER, wrongInt);
            wrongHasTry = true;
        }
    }

    private void saveInt(String key, int value) {
        if (getContext() == null) {
            return;
        }
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getContext()).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        roundActive = false;
        uiHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
