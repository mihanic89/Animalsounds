package com.yamilab.animalsounds;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity implements TTSListener {

    private final boolean ads_default = false;
    private boolean backPressedToExitOnce;
    private boolean ratingDialogWasShown;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static final String ADS_DISABLE_KEY = "ads_disable_enabled";
    private static final String GRID_MINIMIZATION_KEY = "grid_minimization";
    private static final String PRESSED_BACK_ONCE_KEY = "show_rating_dialog";
    private static final String RATING_DIALOG_WAS_SHOWN_KEY = "rating_dialog_was_shown";
    private static final String NUMBER_OF_RATING_START_KEY = "number_of_rating_start";
    private static final String DONT_SHOW_RATING_DIALOF_KEY = "dont_show_rating_dialog";
    private static final String KEY_TO_UNLOCK_FAIRY = "unlockFairy";
    private static final String REVIEW_ENABLED = "ReviewEnabled";
    private static final String WIKI_EN = "http://en.m.wikipedia.org/wiki/";
    private static final String WIKI_AR = "http://ar.m.wikipedia.org/wiki/";
    private static final String WIKI_BG = "http://bg.m.wikipedia.org/wiki/";
    private static final String WIKI_CS = "http://cs.m.wikipedia.org/wiki/";
    private static final String WIKI_DE = "http://de.m.wikipedia.org/wiki/";
    private static final String WIKI_EL = "http://el.m.wikipedia.org/wiki/";
    private static final String WIKI_ES = "http://es.m.wikipedia.org/wiki/";
    private static final String WIKI_FI = "http://fi.m.wikipedia.org/wiki/";
    private static final String WIKI_FR = "http://fr.m.wikipedia.org/wiki/";
    private static final String WIKI_HI = "http://hi.m.wikipedia.org/wiki/";
    private static final String WIKI_HU = "http://hu.m.wikipedia.org/wiki/";
    private static final String WIKI_IN = "http://in.m.wikipedia.org/wiki/";
    private static final String WIKI_IT = "http://it.m.wikipedia.org/wiki/";
    private static final String WIKI_JA = "http://ja.m.wikipedia.org/wiki/";
    private static final String WIKI_KO = "http://ko.m.wikipedia.org/wiki/";
    private static final String WIKI_NL = "http://nl.m.wikipedia.org/wiki/";
    private static final String WIKI_PL = "http://pl.m.wikipedia.org/wiki/";
    private static final String WIKI_PT = "http://pt.m.wikipedia.org/wiki/";
    private static final String WIKI_RO = "http://ro.m.wikipedia.org/wiki/";
    private static final String WIKI_RU = "http://ru.m.wikipedia.org/wiki/";
    private static final String WIKI_SV = "http://sv.m.wikipedia.org/wiki/";
    private static final String WIKI_TR = "http://tr.m.wikipedia.org/wiki/";
    private static final String WIKI_UK = "http://uk.m.wikipedia.org/wiki/";
    private static final String WIKI_ZH = "http://zh.m.wikipedia.org/wiki/";

    private String wikiHref = WIKI_EN;
    public static final Integer adShowInt = 15;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public boolean grid = false;
    public boolean ads_disable_button = false;
    private boolean dontShowRatingDialog = true;
    private boolean review_enabled = true;
    private int numRatingDialog = 0;
    private final int firstTab = 4;
    private int ratingCounter = 0;

    private ViewPager2 mViewPager;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int adCount = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    private TextToSpeech tts;

    private ArrayList<Animal> wild, home, aqua, birds, insects, fairy, animals;
    private int screenWidth = 800, screenHeight = 1280;
    private String language = "en";
    private int unlockCounter = 0;

    private Animation mScaleAnimation0, mScaleAnimation1, mScaleAnimation2, mScaleAnimation3, mScaleAnimation4;
    public boolean ads_disabled = false;
    private final String mSkuId = "disable_ads";
    private final List<String> skuList = new ArrayList<>();
    private SharedPreferences getPrefs;
    private TabLayout.Tab tab;
    TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        GlideApp.get(this).setMemoryCategory(MemoryCategory.LOW);

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        ads_disabled = getPrefs.getBoolean("ads_disabled_key", ads_default);
        ads_disable_button = getPrefs.getBoolean("ads_disable_button_key", false);
        grid = getPrefs.getBoolean(GRID_MINIMIZATION_KEY, false);
        backPressedToExitOnce = getPrefs.getBoolean(PRESSED_BACK_ONCE_KEY, false);
        ratingDialogWasShown = getPrefs.getBoolean(RATING_DIALOG_WAS_SHOWN_KEY, false);
        dontShowRatingDialog = getPrefs.getBoolean(DONT_SHOW_RATING_DIALOF_KEY, false);
        numRatingDialog = getPrefs.getInt(NUMBER_OF_RATING_START_KEY, 0);
        unlockCounter = getPrefs.getInt(KEY_TO_UNLOCK_FAIRY, 0);

        // First start check
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isFirstStart = prefs.getBoolean("firstStart", true);
        if (isFirstStart) {
            Intent i = new Intent(MainActivity.this, MyIntro.class);
            startActivity(i);
            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean("firstStart", false);
            e.apply();
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main_down_tabs_webview);

        // Fullscreen mode
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        initData();
        mSectionsPagerAdapter = new SectionsPagerAdapter(this);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, mViewPager, (tab, position) -> {
        }).attach();
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mScaleAnimation0 = AnimationUtils.loadAnimation(this, R.anim.myscale0);
        mScaleAnimation1 = AnimationUtils.loadAnimation(this, R.anim.myscale1);
        mScaleAnimation2 = AnimationUtils.loadAnimation(this, R.anim.myscale2);
        mScaleAnimation3 = AnimationUtils.loadAnimation(this, R.anim.myscale3);
        mScaleAnimation4 = AnimationUtils.loadAnimation(this, R.anim.myscale4);

        setupTabIcons();
        tab = tabLayout.getTabAt(firstTab);
        tab.select();

        makeLanguageList(Locale.getDefault().getLanguage());

                // Initialize TTS in main thread to avoid memory leaks
        if (!isFinishing()) {
            new android.os.Handler(getMainLooper()).post(() -> {
                if (!isFinishing() && tts == null) {
                    try {
                        tts = new TextToSpeech(getApplicationContext(), status -> {
                            if (status == TextToSpeech.SUCCESS && !isFinishing()) {
                                tts.setLanguage(new Locale(language, ""));
                            }
                        });
                    } catch (Exception e) {
                        if (!isFinishing() && tts == null) {
                            try {
                                tts = new TextToSpeech(getApplicationContext(), status -> {
                                    if (status == TextToSpeech.SUCCESS && !isFinishing()) {
                                        tts.setLanguage(new Locale("en", ""));
                                    }
                                });
                            } catch (Exception ex) {
                                // ignore
                            }
                        }
                    }
                }
            });
        }

        MobileAds.initialize(this, initializationStatus -> {});
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("01481448E8EC40257290F6C3754DA1E2", "84E317C211C4719630024A009A35FDCA"))
                .build();
        MobileAds.setRequestConfiguration(configuration);

        if (!ads_disabled) {
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        GlideApp.with(this)
                .load(R.drawable.background)
                .priority(Priority.LOW)
                .override(screenWidth / 3, screenHeight / 3)
                .fitCenter()
                .placeholder(new ColorDrawable(getResources().getColor(R.color.colorBackground)))
                .transition(withCrossFade(1000))
                .into((ImageView) findViewById(R.id.imageViewBackground));

        try {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            fetch();
        } catch (Exception e) {
            // ignore
        }
    }

    private void setupTabIcons() {
        int[] resID = {
                R.drawable.tab_home, R.drawable.tab_wild, R.drawable.tab_birds,
                R.drawable.tab_aqua, R.drawable.tab_insects, R.drawable.tab_fairy};

        // Tab 0 - Ads
        tabLayout.getTabAt(0).setText("Ads&Privacy");

        // Tab 1 - Game3
        View view10 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab10 = view10.findViewById(R.id.icon);
        imageViewTab10.setImageResource(R.drawable.tab_game3);
        imageViewTab10.setContentDescription("Game 3");
        imageViewTab10.startAnimation(mScaleAnimation3);
        tabLayout.getTabAt(1).setCustomView(view10);

        // Tab 2 - Game2
        View view0 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab0 = view0.findViewById(R.id.icon);
        imageViewTab0.setImageResource(R.drawable.tab_game2);
        imageViewTab0.setContentDescription("Game 2");
        imageViewTab0.startAnimation(mScaleAnimation0);
        tabLayout.getTabAt(2).setCustomView(view0);

        // Tab 3 - Game1
        View view1 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab1 = view1.findViewById(R.id.icon);
        imageViewTab1.setImageResource(R.drawable.tab_game);
        imageViewTab1.setContentDescription("Game 1");
        imageViewTab1.startAnimation(mScaleAnimation2);
        tabLayout.getTabAt(3).setCustomView(view1);



        // Tab 4 - Home
        View view3 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab3 = view3.findViewById(R.id.icon);
        imageViewTab3.setImageResource(R.drawable.tab_home);
        imageViewTab3.setContentDescription("Home");
        imageViewTab3.startAnimation(mScaleAnimation4);
        tabLayout.getTabAt(4).setCustomView(view3);

        // Tab 5 - Wild
        View view4 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab4 = view4.findViewById(R.id.icon);
        imageViewTab4.setImageResource(R.drawable.tab_wild);
        imageViewTab4.setContentDescription("Wild animals");
        imageViewTab4.startAnimation(mScaleAnimation1);
        tabLayout.getTabAt(5).setCustomView(view4);

        // Tab 6 - Birds
        View view5 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab5 = view5.findViewById(R.id.icon);
        imageViewTab5.setImageResource(R.drawable.tab_birds);
        imageViewTab5.setContentDescription("Birds");
        imageViewTab5.startAnimation(mScaleAnimation3);
        tabLayout.getTabAt(6).setCustomView(view5);

        // Tab 7 - Aqua
        View view6 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab6 = view6.findViewById(R.id.icon);
        imageViewTab6.setImageResource(R.drawable.tab_aqua);
        imageViewTab6.setContentDescription("Aquatic animals");
        imageViewTab6.startAnimation(mScaleAnimation0);
        tabLayout.getTabAt(7).setCustomView(view6);

        // Tab 8 - Insects
        View view7 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab7 = view7.findViewById(R.id.icon);
        imageViewTab7.setImageResource(R.drawable.tab_insects);
        imageViewTab7.setContentDescription("Insects");
        imageViewTab7.startAnimation(mScaleAnimation2);
        tabLayout.getTabAt(8).setCustomView(view7);

        // Tab 9 - Fairy
        View view8 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab8 = view8.findViewById(R.id.icon);
        imageViewTab8.setImageResource(R.drawable.tab_fairy);
        imageViewTab8.setContentDescription("Fairy tales");
        imageViewTab8.startAnimation(mScaleAnimation4);
        tabLayout.getTabAt(9).setCustomView(view8);
    }

    public void writeBoolean(boolean enabled) {
        SharedPreferences.Editor e = getPrefs.edit();
        e.putBoolean("ads_disabled_key", enabled);
        e.apply();
        ads_disabled = enabled;
    }

    public void loadInterstitial() {
        AdRequest adRequestInterstitial = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-2888343178529026/6970013790", adRequestInterstitial,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        if (!isFinishing()) {
                            mInterstitialAd = interstitialAd;
                            mFirebaseAnalytics.logEvent("interstitial_onAdLoaded", null);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    public void showInterstitial() {
        if (ratingCounter > 3 && !ratingDialogWasShown && review_enabled) {
            showRatingDialog();
            ratingDialogWasShown = true;
            mFirebaseAnalytics.logEvent("showRatingDialog", null);
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean(RATING_DIALOG_WAS_SHOWN_KEY, true);
            e.apply();
            ratingCounter = 0;
        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
                adCount = 0;
                ratingCounter++;
                mFirebaseAnalytics.logEvent("interstitial_show", null);
            } else {
                Bundle params = new Bundle();
                params.putInt("adCount", adCount);
                mFirebaseAnalytics.logEvent("interstitial_is_null_or_not_loaded", params);
            }
        }
    }

    public void showRatingDialog() {
        if (!dontShowRatingDialog && !ads_disabled) {
            RatingDialog ratingDialog = new RatingDialog.Builder(this)
                    .threshold(4)
                    .title(getString(R.string.rd_title))
                    .positiveButtonText(getString(R.string.rd_positiveButtonText))
                    .negativeButtonText(getString(R.string.rd_negativeButtonText))
                    .formTitle(getString(R.string.rd_formTitle))
                    .formHint(getString(R.string.rd_formHint))
                    .formSubmitText(getString(R.string.rd_formSubmitText))
                    .formCancelText(getString(R.string.rd_formCancelText))
                    .onThresholdCleared((dialog, rating, thresholdCleared) -> {
                        openPlaystore(MainActivity.this);
                        mFirebaseAnalytics.logEvent("rating_dialog_5star", null);
                        SharedPreferences.Editor e = getPrefs.edit();
                        dontShowRatingDialog = true;
                        e.putBoolean(DONT_SHOW_RATING_DIALOF_KEY, true);
                        e.apply();
                        dialog.dismiss();
                    })
                    .onRatingBarFormSumbit(feedback -> {
                        SharedPreferences.Editor e = getPrefs.edit();
                        dontShowRatingDialog = true;
                        e.putBoolean(DONT_SHOW_RATING_DIALOF_KEY, true);
                        e.apply();
                        mFirebaseAnalytics.logEvent("rating_dialog_4_and_less", null);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "contact@yapapa.xyz", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, feedback);
                        try {
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        } catch (Exception ex) {
                            // ignore
                        }
                    })
                    .build();
            ratingDialog.show();
            mFirebaseAnalytics.logEvent("rating_dialog", null);
        }
    }

    @Override
    public void speak(String text, int sound) {
        try {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, "id1");
        } catch (Exception e) {
            // ignore
        }
    }

    public void playSilence(int mseconds) {
        try {
            tts.playSilentUtterance(mseconds, TextToSpeech.QUEUE_FLUSH, "id2");
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void onResume() {
        if (!ads_disabled) {
            try {
                mAdView.resume();
            } catch (Exception e) {
                // ignore
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (!ads_disabled && mAdView != null) {
            try {
                mAdView.pause();
            } catch (Exception e) {
                // ignore
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (!ads_disabled && mAdView != null) {
            try {
                mAdView.destroy();
            } catch (Exception e) {
                // ignore
            }
            mAdView = null;
        }
        // Clear interstitial ad reference to prevent memory leak
        mInterstitialAd = null;
        // Stop animations to prevent memory leak
        if (mScaleAnimation0 != null) mScaleAnimation0.cancel();
        if (mScaleAnimation1 != null) mScaleAnimation1.cancel();
        if (mScaleAnimation2 != null) mScaleAnimation2.cancel();
        if (mScaleAnimation3 != null) mScaleAnimation3.cancel();
        if (mScaleAnimation4 != null) mScaleAnimation4.cancel();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        GlideApp.get(this).clearMemory();
        SoundPlay.clearSP(this);
        super.onDestroy();
    }

    private void makeLanguageList(String locale) {
        if (locale.equals("ar")) { wikiHref = WIKI_AR; language = "ar"; }
        else if (locale.equals("bg")) { wikiHref = WIKI_BG; language = "bg"; }
        else if (locale.equals("cs")) { wikiHref = WIKI_CS; language = "cs"; }
        else if (locale.equals("de")) { wikiHref = WIKI_DE; language = "de"; }
        else if (locale.equals("el")) { wikiHref = WIKI_EL; language = "el"; }
        else if (locale.equals("es")) { wikiHref = WIKI_ES; language = "es"; }
        else if (locale.equals("fi")) { wikiHref = WIKI_FI; language = "fi"; }
        else if (locale.equals("fr")) { wikiHref = WIKI_FR; language = "fr"; }
        else if (locale.equals("hi")) { wikiHref = WIKI_HI; language = "hi"; }
        else if (locale.equals("hu")) { wikiHref = WIKI_HU; language = "hu"; }
        else if (locale.equals("in")) { wikiHref = WIKI_IN; language = "in"; }
        else if (locale.equals("it")) { wikiHref = WIKI_IT; language = "it"; }
        else if (locale.equals("ja")) { wikiHref = WIKI_JA; language = "ja"; }
        else if (locale.equals("ko")) { wikiHref = WIKI_KO; language = "ko"; }
        else if (locale.equals("nl")) { wikiHref = WIKI_NL; language = "nl"; }
        else if (locale.equals("pl")) { wikiHref = WIKI_PL; language = "pl"; }
        else if (locale.equals("pt")) { wikiHref = WIKI_PT; language = "pt"; }
        else if (locale.equals("ro")) { wikiHref = WIKI_RO; language = "ro"; }
        else if (locale.equals("ru")) { wikiHref = WIKI_RU; language = "ru"; }
        else if (locale.equals("sv")) { wikiHref = WIKI_SV; language = "sv"; }
        else if (locale.equals("tr")) { wikiHref = WIKI_TR; language = "tr"; }
        else if (locale.equals("uk")) { wikiHref = WIKI_UK; language = "uk"; }
        else if (locale.equals("zh")) { wikiHref = WIKI_ZH; language = "zh"; }
    }

    private void initData() {
        wild = new ArrayList<>();
        wild.add(new Animal(getString(R.string.bear), R.drawable.w0hd, R.raw.w0, true, "w0.gif"));
        wild.add(new Animal(getString(R.string.elephant), R.drawable.w5hd, R.raw.w5));
        wild.add(new Animal(getString(R.string.wolf), R.drawable.w1hd, R.raw.w1, true, "w1.gif"));
        wild.add(new Animal(getString(R.string.camel), R.drawable.w6hd, R.raw.w06));
        wild.add(new Animal(getString(R.string.monkey), R.drawable.w4hd, R.raw.w4, true, "w4.gif"));
        wild.add(new Animal(getString(R.string.jackal), R.drawable.w8hd, R.raw.w08));
        wild.add(new Animal(getString(R.string.zebra), R.drawable.w7hd, R.raw.w07));
        wild.add(new Animal(getString(R.string.leo), R.drawable.w2hd, R.raw.w2, true, "w2.gif"));
        wild.add(new Animal(getString(R.string.rhino), R.drawable.w12hd, R.raw.w12));
        wild.add(new Animal(getString(R.string.tiger), R.drawable.w3hd, R.raw.w3, true, "w3.gif"));
        wild.add(new Animal(getString(R.string.snake), R.drawable.w9hd, R.raw.w09));
        wild.add(new Animal(getString(R.string.fox), R.drawable.w10hd, R.raw.w10, true, "w10.gif"));
        wild.add(new Animal(getString(R.string.hare), R.drawable.w11hd, R.raw.w11));
        wild.add(new Animal(getString(R.string.crocodile), R.drawable.w13hd, R.raw.w13));
        wild.add(new Animal(getString(R.string.koala), R.drawable.w14hd, R.raw.w14));
        wild.add(new Animal(getString(R.string.panda), R.drawable.w15hd, R.raw.w15, true, "w15.gif"));
        wild.add(new Animal(getString(R.string.kangoroo), R.drawable.w16hd, R.raw.w16));
        wild.add(new Animal(getString(R.string.lemur), R.drawable.w17hd, R.raw.w17, true, "w17.gif"));
        wild.add(new Animal(getString(R.string.lynx), R.drawable.w18hd, R.raw.w18));
        wild.add(new Animal(getString(R.string.elk), R.drawable.w19hd, R.raw.w19));
        wild.add(new Animal(getString(R.string.racoon), R.drawable.w20hd, R.raw.w20));
        wild.add(new Animal(getString(R.string.squirrel), R.drawable.w21hd, R.raw.w21));
        wild.add(new Animal(getString(R.string.rat), R.drawable.w22hd, R.raw.w22, true, "w22.gif"));
        wild.add(new Animal(getString(R.string.jaguar), R.drawable.w24hd, R.raw.w24));
        wild.add(new Animal(getString(R.string.mouse), R.drawable.w23hd, R.raw.w23, true, "w23.gif"));
        wild.add(new Animal(getString(R.string.hippopotamus), R.drawable.w25hd, R.raw.w25));
        wild.add(new Animal(getString(R.string.badger), R.drawable.w26barsuk, R.raw.w26));
        wild.add(new Animal(getString(R.string.beaver), R.drawable.w27beaver, R.raw.w27));
        wild.add(new Animal(getString(R.string.deer), R.drawable.w28deer, R.raw.w28));
        wild.add(new Animal(getString(R.string.hedgehog), R.drawable.w29hedgehog, R.raw.w29, true, "w29.gif"));
        wild.add(new Animal(getString(R.string.giraffe), R.drawable.w30giraffe, R.raw.w30));
        wild.add(new Animal(getString(R.string.mole), R.drawable.w31mole, R.raw.w31));
        wild.add(new Animal(getString(R.string.skunk), R.drawable.w32skunk, R.raw.w32));
        wild.add(new Animal(getString(R.string.boar), R.drawable.w33boar, R.raw.w33));
        wild.add(new Animal(getString(R.string.bison), R.drawable.w34bison, R.raw.w34));
        wild.add(new Animal(getString(R.string.chipmunk), R.drawable.w35chipmunk, R.raw.w35));
        wild.add(new Animal(getString(R.string.alpaca), R.drawable.w36alpaca, R.raw.w36, true, "w36.gif"));
        wild.add(new Animal(getString(R.string.hyena), R.drawable.w37hyena, R.raw.w37));
        wild.add(new Animal(getString(R.string.bat), R.drawable.b28bat, R.raw.b28));
        wild.add(new Animal(getString(R.string.armadillo), R.drawable.w38, R.raw.w38));
        wild.add(new Animal(getString(R.string.wombat), R.drawable.w39, R.raw.w39));
        wild.add(new Animal(getString(R.string.capybara), R.drawable.w40, R.raw.w40));
        wild.add(new Animal(getString(R.string.meerkat), R.drawable.w41, R.raw.w41));
        wild.add(new Animal(getString(R.string.quokka), R.drawable.w42, R.raw.w42));
        wild.add(new Animal(getString(R.string.sloth), R.drawable.w43, R.raw.w43));
        wild.add(new Animal(getString(R.string.monitorlizard), R.drawable.w44, R.raw.w44));
        wild.add(new Animal(getString(R.string.anteater), R.drawable.w45, R.raw.w45));
        wild.add(new Animal(getString(R.string.ferret), R.drawable.w46, R.raw.w46));
        wild.add(new Animal(getString(R.string.lizard), R.drawable.w47, R.raw.w47));
        wild.add(new Animal(getString(R.string.wolverine), R.drawable.w48, R.raw.w48));

        home = new ArrayList<>();
        home.add(new Animal(getString(R.string.dog), R.drawable.h0hd, R.raw.h0, true, "h0.gif"));
        home.add(new Animal(getString(R.string.cat), R.drawable.h1hd, R.raw.h1, true, "h1hd.gif"));
        home.add(new Animal(getString(R.string.pig), R.drawable.h2hd, R.raw.h2));
        home.add(new Animal(getString(R.string.cock), R.drawable.h3hd, R.raw.h3));
        home.add(new Animal(getString(R.string.chiken), R.drawable.h4hd, R.raw.h4));
        home.add(new Animal(getString(R.string.cow), R.drawable.h5hd, R.raw.h5, true, "h5.gif"));
        home.add(new Animal(getString(R.string.sheep), R.drawable.h7hd, R.raw.h7));
        home.add(new Animal(getString(R.string.horse), R.drawable.h6hd, R.raw.h6, true, "h6.gif"));
        home.add(new Animal(getString(R.string.goat), R.drawable.h8hd, R.raw.h8));
        home.add(new Animal(getString(R.string.donkey), R.drawable.h9hd, R.raw.h9));
        home.add(new Animal(getString(R.string.cavy), R.drawable.h11hd, R.raw.h11, true, "h11.gif"));
        home.add(new Animal(getString(R.string.turkey), R.drawable.h10hd, R.raw.h10));
        home.add(new Animal(getString(R.string.rabbit), R.drawable.h12rabbit, R.raw.h12, true, "h12.gif"));
        home.add(new Animal(getString(R.string.pony), R.drawable.h13, R.raw.h13));

        aqua = new ArrayList<>();
        aqua.add(new Animal(getString(R.string.dolphin), R.drawable.a0hd, R.raw.a0, true, "a0.gif"));
        aqua.add(new Animal(getString(R.string.shark), R.drawable.a11hd, R.raw.a11));
        aqua.add(new Animal(getString(R.string.sealbark), R.drawable.a1hd, R.raw.a1));
        aqua.add(new Animal(getString(R.string.frog), R.drawable.a2hd, R.raw.a2));
        aqua.add(new Animal(getString(R.string.penguin), R.drawable.a3hd, R.raw.a3, true, "a3.gif"));
        aqua.add(new Animal(getString(R.string.walrus), R.drawable.a4hd, R.raw.a4));
        aqua.add(new Animal(getString(R.string.sealion), R.drawable.a5hd, R.raw.a5));
        aqua.add(new Animal(getString(R.string.whale), R.drawable.a6hd, R.raw.a6));
        aqua.add(new Animal(getString(R.string.turtle), R.drawable.a8turtle, R.raw.a8, true, "a8.gif"));
        aqua.add(new Animal(getString(R.string.fish), R.drawable.a7hd, R.raw.a7));
        aqua.add(new Animal(getString(R.string.otter), R.drawable.a9otter, R.raw.a9, true, "a9.gif"));
        aqua.add(new Animal(getString(R.string.lobster), R.drawable.a10lobster, R.raw.a10));

        birds = new ArrayList<>();
        birds.add(new Animal(getString(R.string.goose), R.drawable.b0hd, R.raw.b0, true, "b0.gif"));
        birds.add(new Animal(getString(R.string.duck), R.drawable.b1hd, R.raw.b1));
        birds.add(new Animal(getString(R.string.crow), R.drawable.b2hd, R.raw.b2));
        birds.add(new Animal(getString(R.string.seagull), R.drawable.b3hd, R.raw.b3));
        birds.add(new Animal(getString(R.string.dove), R.drawable.b4hd, R.raw.b4));
        birds.add(new Animal(getString(R.string.nightingale), R.drawable.b5hd, R.raw.b5));
        birds.add(new Animal(getString(R.string.eagle), R.drawable.b6hd, R.raw.b6, true, "b6.gif"));
        birds.add(new Animal(getString(R.string.hawk), R.drawable.b7hd, R.raw.b7));
        birds.add(new Animal(getString(R.string.woodpecker), R.drawable.b8hd, R.raw.b8, true, "b8.gif"));
        birds.add(new Animal(getString(R.string.pelican), R.drawable.b12hd, R.raw.b12));
        birds.add(new Animal(getString(R.string.parrot), R.drawable.b9hd, R.raw.b9, true, "b9.gif"));
        birds.add(new Animal(getString(R.string.catbird), R.drawable.b16catbird, R.raw.b16));
        birds.add(new Animal(getString(R.string.owl), R.drawable.b10hd, R.raw.b10, true, "b10.gif"));
        birds.add(new Animal(getString(R.string.cuckoo), R.drawable.b11hd, R.raw.b11));
        birds.add(new Animal(getString(R.string.ostrich), R.drawable.b13hd, R.raw.b13));
        birds.add(new Animal(getString(R.string.flamingo), R.drawable.b14hd, R.raw.b14, true, "b14.gif"));
        birds.add(new Animal(getString(R.string.peacock), R.drawable.b15hd, R.raw.b15));
        birds.add(new Animal(getString(R.string.tit), R.drawable.b17tit, R.raw.b17));
        birds.add(new Animal(getString(R.string.toucan), R.drawable.b18toucan, R.raw.b18));
        birds.add(new Animal(getString(R.string.robin), R.drawable.b19robin, R.raw.b19));
        birds.add(new Animal(getString(R.string.blackgrouse), R.drawable.b20blackgrouse, R.raw.b20));
        birds.add(new Animal(getString(R.string.hummingbird), R.drawable.b21hummingbird, R.raw.b21));
        birds.add(new Animal(getString(R.string.bullfinch), R.drawable.b23bullfinch, R.raw.b23));
        birds.add(new Animal(getString(R.string.stork), R.drawable.b24stork, R.raw.b24));
        birds.add(new Animal(getString(R.string.heron), R.drawable.b25heron, R.raw.b25));
        birds.add(new Animal(getString(R.string.canary), R.drawable.b26canary, R.raw.b26));
        birds.add(new Animal(getString(R.string.magpie), R.drawable.b27magpie, R.raw.b27));
        birds.add(new Animal(getString(R.string.jay), R.drawable.b29jay, R.raw.b29));
        birds.add(new Animal(getString(R.string.starling), R.drawable.b30starling, R.raw.b30));
        birds.add(new Animal(getString(R.string.sparrow), R.drawable.b31, R.raw.b31));

        insects = new ArrayList<>();
        insects.add(new Animal(getString(R.string.bees), R.drawable.i0hd, R.raw.i00));
        insects.add(new Animal(getString(R.string.flies), R.drawable.i1hd, R.raw.i01));
        insects.add(new Animal(getString(R.string.mosquito), R.drawable.i2hd, R.raw.i02, true, "i2.gif"));
        insects.add(new Animal(getString(R.string.grasshopper), R.drawable.i3hd, R.raw.i3));
        insects.add(new Animal(getString(R.string.bumblebee), R.drawable.i4hd, R.raw.i4));
        insects.add(new Animal(getString(R.string.cricket), R.drawable.i5hd, R.raw.i5));
        insects.add(new Animal(getString(R.string.butterfly), R.drawable.i6hd, R.raw.i6, true, "i6.gif"));
        insects.add(new Animal(getString(R.string.dragonfly), R.drawable.i7hd, R.raw.i7));
        insects.add(new Animal(getString(R.string.ants), R.drawable.i8hd, R.raw.i8));
        insects.add(new Animal(getString(R.string.mantis), R.drawable.i9hd, R.raw.i9));
        insects.add(new Animal(getString(R.string.cicada), R.drawable.i10hd, R.raw.i10));
        insects.add(new Animal(getString(R.string.spider), R.drawable.i11, R.raw.i11));
        insects.add(new Animal(getString(R.string.scorpion), R.drawable.i12, R.raw.i12));

        fairy = new ArrayList<>();
        fairy.add(new Animal(getString(R.string.dragon), R.drawable.f0hd, R.raw.f0));
        fairy.add(new Animal(getString(R.string.unicorn), R.drawable.f1hd, R.raw.f1));
        fairy.add(new Animal(getString(R.string.pokemon), R.drawable.f2hd, R.raw.f2, true, "f2.gif"));
        fairy.add(new Animal(getString(R.string.buckbeak), R.drawable.f3hd, R.raw.f3));
        fairy.add(new Animal(getString(R.string.dinosaur), R.drawable.f4hd, R.raw.f4));
        fairy.add(new Animal(getString(R.string.pegasus), R.drawable.f5hd, R.raw.f5));
        fairy.add(new Animal(getString(R.string.centaur), R.drawable.f6hd, R.raw.f6));
        fairy.add(new Animal(getString(R.string.phoenix), R.drawable.f7hd, R.raw.f7, true, "f7.gif"));
        fairy.add(new Animal(getString(R.string.waternymph), R.drawable.f8hd, R.raw.f8));
        fairy.add(new Animal(getString(R.string.griffon), R.drawable.f9hd, R.raw.f9));
        fairy.add(new Animal(getString(R.string.yeti), R.drawable.f10hd, R.raw.f10));

        animals = new ArrayList<>();
        animals.addAll(home);
        animals.addAll(wild);
        animals.addAll(birds);
        animals.addAll(aqua);
        animals.addAll(insects);
        if (unlockCounter > 9) {
            animals.addAll(fairy);
        }
    }

    private void fetch() {
        final java.lang.ref.WeakReference<MainActivity> weakRef = new java.lang.ref.WeakReference<>(this);
        mFirebaseRemoteConfig.fetch(3600)
                .addOnCompleteListener(task -> {
                    MainActivity activity = weakRef.get();
                    if (activity != null && !activity.isFinishing() && task.isSuccessful()) {
                        mFirebaseRemoteConfig.activate();
                        SharedPreferences.Editor e = activity.getPrefs.edit();
                        activity.ads_disable_button = mFirebaseRemoteConfig.getBoolean(ADS_DISABLE_KEY);
                        activity.grid = mFirebaseRemoteConfig.getBoolean(GRID_MINIMIZATION_KEY);
                        if (!mFirebaseRemoteConfig.getBoolean(REVIEW_ENABLED)) {
                            activity.review_enabled = false;
                        }
                        e.putBoolean(GRID_MINIMIZATION_KEY, activity.grid);
                        e.putBoolean("ads_disable_button_key", activity.ads_disable_button);
                        e.apply();
                    }
                });
    }

    public void incAdCounter() {
        adCount++;
        if (adCount == 3 || adCount > 20) {
            loadInterstitial();
        }
    }

    public int getAdCounter() {
        return adCount;
    }

    public boolean getGrid() {
        return grid;
    }

    @Override
    public void onBackPressed() {
        incrementRating();
        if (backPressedToExitOnce) {
            super.onBackPressed();
            return;
        } else {
            if (review_enabled) {
                showRatingDialog();
            }
            backPressedToExitOnce = true;
        }
    }

    public void incrementRating() {
        numRatingDialog++;
        saveInt(NUMBER_OF_RATING_START_KEY, numRatingDialog);
        if (numRatingDialog > 3) {
            backPressedToExitOnce = true;
            saveBoolean(PRESSED_BACK_ONCE_KEY, true);
        }
        if (numRatingDialog == 10 || numRatingDialog == 20 || numRatingDialog == 30 ||
                numRatingDialog == 40 || numRatingDialog == 50) {
            backPressedToExitOnce = false;
            saveBoolean(PRESSED_BACK_ONCE_KEY, false);
        }
    }

    public void incrementUnlockCounter() {
        unlockCounter++;
        saveInt(KEY_TO_UNLOCK_FAIRY, unlockCounter);
    }

    private void openPlaystore(Context context) {
        final Uri marketUri = Uri.parse("market://details?id=com.yamilab.animalsounds");
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Couldn't find PlayStore on this device", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setGameTab() {
        tab = tabLayout.getTabAt(new Random().nextInt(3));
        tab.select();
    }

    private static final String TAG = "MainActivity";

    public class SectionsPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

        public SectionsPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public SectionsPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            incAdCounter();
            if (adCount > adShowInt) {
                showInterstitial();
            }
            switch (position) {
                case 0:
                    mFirebaseAnalytics.logEvent("tab_ads", null);
                    return new ImageGridFragmentAds();
                case 1:
                    mFirebaseAnalytics.logEvent("tab_game3", null);
                    return ImageGridFragmentGame3.newInstance(animals, screenWidth);
                case 2:
                    mFirebaseAnalytics.logEvent("tab_game2", null);
                    return ImageGridFragmentGame2.newInstance(animals, screenWidth);
                case 3:
                    mFirebaseAnalytics.logEvent("tab_game1", null);
                    return ImageGridFragmentGame.newInstance(animals, screenWidth);
                case 4:
                    mFirebaseAnalytics.logEvent("tab_home", null);
                    return ImageGridFragment.newInstance(home, screenWidth);
                case 5:
                    mFirebaseAnalytics.logEvent("tab_wild", null);
                    return ImageGridFragment.newInstance(wild, screenWidth);
                case 6:
                    mFirebaseAnalytics.logEvent("tab_birds", null);
                    return ImageGridFragment.newInstance(birds, screenWidth);
                case 7:
                    mFirebaseAnalytics.logEvent("tab_aqua", null);
                    return ImageGridFragment.newInstance(aqua, screenWidth);
                case 8:
                    mFirebaseAnalytics.logEvent("tab_insects", null);
                    return ImageGridFragment.newInstance(insects, screenWidth);
                case 9:
                    mFirebaseAnalytics.logEvent("tab_fairy", null);
                    if (unlockCounter < 29) {
                        return FragmentUnlockFairy.newInstance(unlockCounter);
                    } else {
                        return ImageGridFragment.newInstance(fairy, screenWidth);
                    }
                default:
                    return ImageGridFragment.newInstance(home, screenWidth);
            }
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }
}