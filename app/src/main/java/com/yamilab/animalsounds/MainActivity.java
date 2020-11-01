package com.yamilab.animalsounds;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/*
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
*/


public class MainActivity extends AppCompatActivity implements TTSListener  {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */

    //если тру то отключены, фолс включены
    private final boolean ads_default = false;
    private boolean backPressedToExitOnce;
    private boolean ratingDialogWasShown;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static final String ADS_DISABLE_KEY = "ads_disable_enabled";
    private static final String GRID_MINIMIZATION_KEY = "grid_minimization";
    private static final String PRESSED_BACK_ONCE_KEY ="show_rating_dialog";
    private static final String RATING_DIALOG_WAS_SHOWN_KEY="rating_dialog_was_shown";
    private static final String NUMBER_OF_RATING_START_KEY="number_of_rating_start";
    private static final  String DONT_SHOW_RATING_DIALOF_KEY="dont_show_rating_dialog";
    private static final String KEY_TO_UNLOCK_FAIRY = "unlockFairy";
    private static final String REVIEW_ENABLED="ReviewEnabled";
    private static final String WIKI_EN="http://en.m.wikipedia.org/wiki/";
    private static final String WIKI_AR="http://ar.m.wikipedia.org/wiki/";
    private static final String WIKI_BG="http://bg.m.wikipedia.org/wiki/";
    private static final String WIKI_CS="http://cs.m.wikipedia.org/wiki/";
    private static final String WIKI_DE="http://de.m.wikipedia.org/wiki/";
    private static final String WIKI_EL="http://el.m.wikipedia.org/wiki/";
    private static final String WIKI_ES="http://es.m.wikipedia.org/wiki/";
    private static final String WIKI_FI="http://fi.m.wikipedia.org/wiki/";
    private static final String WIKI_FR="http://fr.m.wikipedia.org/wiki/";
    private static final String WIKI_HI="http://hi.m.wikipedia.org/wiki/";
    private static final String WIKI_HU="http://hu.m.wikipedia.org/wiki/";
    private static final String WIKI_IN="http://in.m.wikipedia.org/wiki/";
    private static final String WIKI_IT="http://it.m.wikipedia.org/wiki/";
    private static final String WIKI_JA="http://ja.m.wikipedia.org/wiki/";
    private static final String WIKI_KO="http://ko.m.wikipedia.org/wiki/";
    private static final String WIKI_NL="http://nl.m.wikipedia.org/wiki/";
    private static final String WIKI_PL="http://pl.m.wikipedia.org/wiki/";
    private static final String WIKI_PT="http://pt.m.wikipedia.org/wiki/";
    private static final String WIKI_RO="http://ro.m.wikipedia.org/wiki/";
    private static final String WIKI_RU="http://ru.m.wikipedia.org/wiki/";
    private static final String WIKI_SV="http://sv.m.wikipedia.org/wiki/";
    private static final String WIKI_TR="http://tr.m.wikipedia.org/wiki/";
    private static final String WIKI_UK="http://uk.m.wikipedia.org/wiki/";
    private static final String WIKI_ZH="http://zh.m.wikipedia.org/wiki/";

    private boolean showWiki = false;

    private String wikiHref=WIKI_EN;

    public static final Integer adShowInt = 15;





    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public boolean grid = false;

    public boolean ads_disable_button=false;
    private boolean dontShowRatingDialog=true;
    private boolean review_enabled = true;
    private int numRatingDialog=0;

    private int firstTab = 4;
    private int ratingCounter=0;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int adCount=0;
    boolean showInterstitialAd = true;
    boolean notFirstStart = true;
    private FirebaseAnalytics mFirebaseAnalytics;
    //private ImageView imageViewBackground;

    private TextToSpeech tts;

    private ArrayList<Animal> wild, home, aqua, birds, insects, fairy,animals;
    private int screenWidth=800,screenHeight=1280;
    private String language="en";

    private int unlockCounter=0;




    private Animation mScaleAnimation0,
            mScaleAnimation1,
            mScaleAnimation2,
            mScaleAnimation3,
            mScaleAnimation4;

    public boolean ads_disabled=false;


    //по покупкам
   // private BillingClient mBillingClient;
  //  private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private String mSkuId = "disable_ads";
    private List<String> skuList = new ArrayList<>();
    private SharedPreferences getPrefs;
    private TabLayout.Tab  tab;
    TabLayout tabLayout;

    private WebView wiki;
    private ImageButton stopWiki;
    private ProgressBar progressWiki;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Debug.startMethodTracing("sample");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        GlideApp.get(this).setMemoryCategory(MemoryCategory.LOW);



        /*
        FiveStarsDialog fiveStarsDialog = new FiveStarsDialog(this,"contact@yapapa.xyz");
        fiveStarsDialog
                //.setRateText("Your custom text")
                //.setTitle("Your custom title")
                .setForceMode(true)
                .setUpperBound(4) // Market opened if a rating >= 2 is selected
                //.setNegativeReviewListener((NegativeReviewListener) this) // OVERRIDE mail intent for negative review
                //.setReviewListener(this) // Used to listen for reviews (if you want to track them )
                .showAfter(1);
        */

       getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        ads_disabled = getPrefs.getBoolean("ads_disabled_key", ads_default);
        ads_disable_button = getPrefs.getBoolean("ads_disable_button_key",false);
        grid=getPrefs.getBoolean(GRID_MINIMIZATION_KEY,false);
        backPressedToExitOnce = getPrefs.getBoolean(PRESSED_BACK_ONCE_KEY,false);
        ratingDialogWasShown =  getPrefs.getBoolean(RATING_DIALOG_WAS_SHOWN_KEY,false);
        dontShowRatingDialog = getPrefs.getBoolean(DONT_SHOW_RATING_DIALOF_KEY,false);

        numRatingDialog = getPrefs.getInt(NUMBER_OF_RATING_START_KEY,0);

        unlockCounter = getPrefs.getInt(KEY_TO_UNLOCK_FAIRY,0);

        /*
        mBillingClient = BillingClient.newBuilder(this).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                    //сюда мы попадем когда будет осуществлена покупка
                    payComplete();
                }
            }
        }).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    //здесь мы можем запросить информацию о товарах и покупках

                    querySkuDetails();

                    List<Purchase> purchasesList = queryPurchases(); //запрос о покупках


                    if (purchasesList!=null) {
                        try {
                            boolean pay = false;
                            for (int i = 0; i < purchasesList.size(); i++) {
                                String purchaseId = purchasesList.get(i).getSku();
                                if (TextUtils.equals(mSkuId, purchaseId)) {
                                    payComplete();
                                    pay = true;
                                }
                            }

                            if (!pay) {
                                payUnComplete();
                            }
                        }
                        catch (Exception ex){

                        }
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                //сюда мы попадем если что-то пойдет не так
            }
        });
        */


        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {
                    //firstTab=0;
                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, MyIntro.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);
                   // showInterstitialAd = false;
                    notFirstStart = false;
                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        //if (!ads_disabled) {



            setContentView(R.layout.activity_main_down_tabs_webview);


       /* {
            setContentView(R.layout.activity_main_down_tabs_noads);
        }
        */



           wiki = (WebView) findViewById(R.id.wiki);
           stopWiki = (ImageButton) findViewById(R.id.buttonWikiClose);
           progressWiki = (ProgressBar) findViewById(R.id.progressWiki);

           wiki.setVisibility(View.INVISIBLE);
           stopWiki.setVisibility(View.INVISIBLE);
           progressWiki.setVisibility(View.INVISIBLE);


        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;


       // Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        initData();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        mViewPager.setOffscreenPageLimit(3);




        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        int[] resID={
                R.drawable.tab_home,
                R.drawable.tab_wild,
                R.drawable.tab_birds,
                R.drawable.tab_aqua,
                R.drawable.tab_insects,
                R.drawable.tab_fairy};

        mScaleAnimation0 = AnimationUtils.loadAnimation(this,R.anim.myscale0);
        mScaleAnimation1 = AnimationUtils.loadAnimation(this,R.anim.myscale1);
        mScaleAnimation2 = AnimationUtils.loadAnimation(this,R.anim.myscale2);
        mScaleAnimation3 = AnimationUtils.loadAnimation(this,R.anim.myscale3);
        mScaleAnimation4 = AnimationUtils.loadAnimation(this,R.anim.myscale4);

        View view10 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab10 = view10.findViewById(R.id.icon);
        imageViewTab10.setImageResource(R.drawable.tab_game3);
        imageViewTab10.startAnimation(mScaleAnimation3);
        tabLayout.getTabAt(0).setCustomView(view10);


        View view0 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab0 = view0.findViewById(R.id.icon);
        imageViewTab0.setImageResource(R.drawable.tab_game2);
        imageViewTab0.startAnimation(mScaleAnimation0);
        tabLayout.getTabAt(1).setCustomView(view0);

        View view1 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab1 = view1.findViewById(R.id.icon);
        imageViewTab1.setImageResource(R.drawable.tab_game);
        imageViewTab1.startAnimation(mScaleAnimation2);
        tabLayout.getTabAt(2).setCustomView(view1);




        /*
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_game);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));
        */
       // tabLayout.getTabAt(0).setIcon( R.drawable.tab_game2);
       //tabLayout.getTabAt(1).setIcon( R.drawable.tab_game);
        tabLayout.getTabAt(3).setText("Ads");

        View view3 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab3 = view3.findViewById(R.id.icon);
        imageViewTab3.setImageResource(R.drawable.tab_home);
        imageViewTab3.startAnimation(mScaleAnimation4);
        tabLayout.getTabAt(4).setCustomView(view3);

        View view4 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab4 = view4.findViewById(R.id.icon);
        imageViewTab4.setImageResource(R.drawable.tab_wild);
        imageViewTab4.startAnimation(mScaleAnimation1);
        tabLayout.getTabAt(5).setCustomView(view4);

        View view5 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab5 = view5.findViewById(R.id.icon);
        imageViewTab5.setImageResource(R.drawable.tab_birds);
        imageViewTab5.startAnimation(mScaleAnimation3);
        tabLayout.getTabAt(6).setCustomView(view5);

        View view6 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab6 = view6.findViewById(R.id.icon);
        imageViewTab6.setImageResource(R.drawable.tab_aqua);
        imageViewTab6.startAnimation(mScaleAnimation0);
        tabLayout.getTabAt(7).setCustomView(view6);

        View view7 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab7 = view7.findViewById(R.id.icon);
        imageViewTab7.setImageResource(R.drawable.tab_insects);
        imageViewTab7.startAnimation(mScaleAnimation2);
        tabLayout.getTabAt(8).setCustomView(view7);

        View view8 = getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView imageViewTab8 = view8.findViewById(R.id.icon);
        imageViewTab8.setImageResource(R.drawable.tab_fairy);
        imageViewTab8.startAnimation(mScaleAnimation4);
        tabLayout.getTabAt(9).setCustomView(view8);

        /*
        for (int i = 3; i < tabLayout.getTabCount(); i++) {
            try {
                //tabLayout.getTabAt(i).setIcon(resID[i - 3]);


            }
            catch (Exception e)
            {

            }
        }
        */
        tab = tabLayout.getTabAt(firstTab);
        tab.select();









        // Create the InterstitialAd and set the adUnitId.

        makeLanguageList(Locale.getDefault().getLanguage());


        try {
            new TtsInit().execute();
        }
        catch (Exception e){

        }



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        List<String> testDeviceIds = Arrays.asList("202EFAFD70CDAE930A3318D4508534C0");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);



                if (!ads_disabled) {

                    mAdView = findViewById(R.id.adView);
                    AdRequest adRequest = new AdRequest.Builder()
                            // .addNetworkExtrasBundle(AdMobAdapter.class, extrasAdview)
                            // .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            //.tagForChildDirectedTreatment(true)

                            .build();
                    mAdView.loadAd(adRequest);
                }



        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2888343178529026/6970013790");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
               // loadInterstitial();
                adCount=0;
                ratingCounter++;
            }

            @Override
            public void onAdLoaded(){
                // showInterstitial();
                mFirebaseAnalytics.logEvent("interstitial_onAdLoaded", null);
            }
        });


        GlideApp.with(this)
                // .asDrawable()
                // .load(mStorageRef.child(mDataSet.get(position).getImage()))
                .load(R.drawable.background)
                // .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.LOW)
                //.load(internetUrl)
                //.skipMemoryCache(true)
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(screenWidth /3, screenHeight /3)
                .fitCenter()
                // .thumbnail()
                //.error(R.mipmap.ic_launcher)
                .placeholder(new ColorDrawable(getResources().getColor(R.color.colorBackground)))
                //.placeholder(R.mipmap.placeholder)
                .transition(withCrossFade(1000))
                .into((ImageView) findViewById(R.id.imageViewBackground));

        //loadInterstitial();



        //удаленная конфигурация
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                //.setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);


        fetch();

       // Debug.stopMethodTracing();

            stopWiki.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopWiki();
                }
            });


    }





    /*
    private void querySkuDetails() {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        skuList.add("disable_ads");
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

        mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                if (responseCode == 0) {

                   // Toast toast = Toast.makeText(getApplication(),
                   //         "ответ получен " + skuDetailsList.size(), Toast.LENGTH_SHORT);
                   // toast.show();

                    for (SkuDetails skuDetails : skuDetailsList) {
                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                    }
                }
            }
        });

    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult=null;
        if (areSubscriptionsSupported()) {
             purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
        }



        if (purchasesResult!=null && purchasesResult.getPurchasesList()!=null){
        return purchasesResult.getPurchasesList();}
        else return null;
    }

    public boolean areSubscriptionsSupported() {
        int responseCode = mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        return responseCode == BillingClient.BillingResponse.OK;
    }
    public void launchBilling(String skuId) {



        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(mSkuId))
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    public void payComplete(){

     //   Toast toast = Toast.makeText(this,
      //          "payComplete()", Toast.LENGTH_SHORT);
     //   toast.show();

        removeAd();
        writeBoolean(true);
    }

    public void payUnComplete(){

       // Toast toast = Toast.makeText(this,
      //          "payUnComplete()", Toast.LENGTH_SHORT);
      //  toast.show();

        //removeAd();
        writeBoolean(false);
        //loadInterstitial();
      //  ProcessPhoenix.triggerRebirth(this);

    }
    */


    public void writeBoolean (boolean enabled){
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putBoolean("ads_disabled_key", enabled);
        e.apply();
        ads_disabled=enabled;

    }

    public void loadInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        //если не грузится, не загружено и не запрещено, то загрузить
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded() && !ads_disabled) {
            AdRequest adRequest = new AdRequest.Builder()
                    //.tagForChildDirectedTreatment(true)

                    .addTestDevice("634EE6DF579E0E01020981609CDA857D")
                    .addTestDevice("A4203BC89A24BEEC45D1111F16D2F0A3")
                    .addTestDevice("4174C23AC2A2DAFD78A7C0F0DFB39F3E") //Samsung A50
                    .addTestDevice("7A4531178089C7C58205C7AA937079B6") //Nexus
                  //  .addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
                    .build();
            mInterstitialAd.loadAd(adRequest);
            mFirebaseAnalytics.logEvent("interstitial_load", null);
        }
    }



    public void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        //если баннер создан
        //ratingCounter++;

        /*
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded() && !ads_disabled) {
        loadInterstitial();
            /*
            Toast toast = Toast.makeText(this,
                    "showInterstitial() loadInterstitial()", Toast.LENGTH_SHORT);
            toast.show();

        }
        */


        if (ratingCounter>3 && !ratingDialogWasShown && review_enabled){
            showRatingDialog();
            ratingDialogWasShown=true;
            SharedPreferences getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean(RATING_DIALOG_WAS_SHOWN_KEY,true);
            e.apply();
            ratingCounter=0;
           // adCount=0;
        }

        else {

            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                mFirebaseAnalytics.logEvent("interstitial_show", null);
            } else {
                Bundle params = new Bundle();
                params.putInt("adCount", adCount);
                mFirebaseAnalytics.logEvent("interstitial_is_null_or_not_loaded", params);
            }
        }
    }




    public void showRatingDialog (){

        if (!dontShowRatingDialog && !ads_disabled) {

            final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                    .threshold(4)

                    .title(getString(R.string.rd_title))
                    .positiveButtonText(getString(R.string.rd_positiveButtonText))
                    .negativeButtonText(getString(R.string.rd_negativeButtonText))

                    .formTitle(getString(R.string.rd_formTitle))
                    .formHint(getString(R.string.rd_formHint))
                    .formSubmitText(getString(R.string.rd_formSubmitText))
                    .formCancelText(getString(R.string.rd_formCancelText))
                    .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                        @Override
                        public void onThresholdCleared(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                            //do something
                            openPlaystore(MainActivity.this);
                            mFirebaseAnalytics.logEvent("rating_dialog_5star", null);
                            SharedPreferences getPrefs = PreferenceManager
                                    .getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor e = getPrefs.edit();
                            dontShowRatingDialog=true;
                            e.putBoolean(DONT_SHOW_RATING_DIALOF_KEY, true);
                            e.apply();
                            ratingDialog.dismiss();
                        }
                    })
                    //.session(7)
                    .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                        @Override
                        public void onFormSubmitted(String feedback) {
                            SharedPreferences getPrefs = PreferenceManager
                                    .getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor e = getPrefs.edit();
                            dontShowRatingDialog=true;
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

                            }
                        }
                    }).build();

            ratingDialog.show();
            // incrementRating();
            mFirebaseAnalytics.logEvent("rating_dialog", null);
        }


    }


    public void playSilence (int mseconds){




        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {

                tts.playSilence(mseconds,TextToSpeech.QUEUE_FLUSH,null);
            }
            catch (Exception e){
                // SoundPlay.playSP(this,sound);

                //tts.playSilentUtterance(mseconds, TextToSpeech.QUEUE_FLUSH, null);
            }

        } else {
            try {
                tts.playSilentUtterance(mseconds, TextToSpeech.QUEUE_FLUSH, "id2");
            }
            catch (Exception e){
                //SoundPlay.playSP(this,sound);
            }

        }
     }



    @Override
    public void speak(String text,int sound) {


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                try {
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null);
                }
                catch (Exception e){
                   // SoundPlay.playSP(this,sound);
                }

            } else {
                try {
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null, "id1");
                }
                catch (Exception e){
                    //SoundPlay.playSP(this,sound);
                }

            }

    }


   // public void launchBilling() {
        /*
        mAdView.setEnabled(false);
        mAdView.destroy();
        mAdView.setVisibility(View.INVISIBLE);
        */
        //View view = getWindow().getDecorView();
       // removeAd();
    //    launchBilling(mSkuId);

   // }

    private void removeAd() {
        if (mAdView != null && !ads_disabled) {
            ViewGroup parent = (ViewGroup) mAdView.getParent();
            try {
                parent.removeView(mAdView);
            }
            catch (Exception e){

            }
            parent.invalidate();
        }
    }








        /*
        @Override
        public void onStart() {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "onStart", Toast.LENGTH_SHORT);
            toast.show();
            super.onStart();
        }

        @Override
        public void onPause() {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "onStart", Toast.LENGTH_SHORT);
            toast.show();
            super.onPause();
        }

        @Override
        public void onStop() {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "onStart", Toast.LENGTH_SHORT);
            toast.show();
            super.onStop();
        }

        @Override
        public void onDestroy() {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "onStart", Toast.LENGTH_SHORT);
            toast.show();
            super.onDestroy();
        }
        */


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */




    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            stopWiki();

            //adCount++;
            incAdCounter();
            if (adCount>adShowInt){
                showInterstitial();

            }



            switch (position) {

                case 0:
                    mFirebaseAnalytics.logEvent("tab_game3", null);
                    return ImageGridFragmentGame3.newInstance(animals,screenWidth);

                case 1:
                    mFirebaseAnalytics.logEvent("tab_game2", null);
                    return ImageGridFragmentGame2.newInstance(animals,screenWidth);

                case 2:
                    mFirebaseAnalytics.logEvent("tab_game1", null);
                    return ImageGridFragmentGame.newInstance(animals,screenWidth);

                case 3:
                    mFirebaseAnalytics.logEvent("tab_ads", null);
                    return new ImageGridFragmentAds();

                case 4:
                    mFirebaseAnalytics.logEvent("tab_home", null);
                    return ImageGridFragment.newInstance( home,screenWidth);

                case 5:
                    mFirebaseAnalytics.logEvent("tab_wild", null);
                    return ImageGridFragment.newInstance( wild,screenWidth);
                case 6:
                    mFirebaseAnalytics.logEvent("tab_birds", null);
                    return ImageGridFragment.newInstance( birds,screenWidth);
                case 7:
                    mFirebaseAnalytics.logEvent("tab_aqua", null);
                    return ImageGridFragment.newInstance( aqua,screenWidth);
                case 8:
                    mFirebaseAnalytics.logEvent("tab_insects", null);
                    return ImageGridFragment.newInstance( insects,screenWidth);
                case 9:
                    mFirebaseAnalytics.logEvent("tab_fairy", null);

                    if (unlockCounter<29) {
                        return FragmentUnlockFairy.newInstance(unlockCounter);
                    }
                    else {
                        return ImageGridFragment.newInstance(fairy, screenWidth);
                    }

            }
            return ImageGridFragment.newInstance(home,screenWidth);//PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 10;
        }


    }

    @Override
    public void onResume() {

        // Resume the AdView.
        if (!ads_disabled) {
            try {
                mAdView.resume();
            }
            catch (Exception e){

            }
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        // Pause the AdView.
        if (!ads_disabled) {
            try {
            mAdView.pause();
            }
            catch (Exception e){

            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (!ads_disabled) {
            try {
            mAdView.destroy();
            }
            catch (Exception e){

            }
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        GlideApp.get(this).clearMemory();
        SoundPlay.clearSP(this);
        wiki.loadUrl("about:blank");
        wiki.setVisibility(View.INVISIBLE);
        stopWiki.setVisibility(View.INVISIBLE);
        super.onDestroy();

    }

    public class TtsInit extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = tts.setLanguage(new Locale(language, ""));
                        }
                    }
                });

            }
            catch (Exception e){
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = tts.setLanguage(new Locale("en", ""));
                        }
                    }
                });

            }


            return null;
        }
    }

    private void makeLanguageList(String locale){
        if (locale.equals("en")){
           showWiki = true;
        }



        if (locale.equals("ar")){
            wikiHref=WIKI_AR;
            language="ar";
        }

        if (locale.equals("bg")){
            wikiHref=WIKI_BG;
            language="bg";
        }
        if (locale.equals("cs")){
            wikiHref=WIKI_CS;
            language="cs";
        }
        if (locale.equals("de")){
            wikiHref=WIKI_DE;
            language="de";
         //   showWiki = true;
        }
        if (locale.equals("el")){
            wikiHref=WIKI_EL;
            language="el";
        }
        if (locale.equals("es")){
            wikiHref=WIKI_ES;
            language="es";
            showWiki = true;
        }
        if (locale.equals("fi")){
            wikiHref=WIKI_FI;
            language="fi";
        }
        /*
        if (locale.equals("fil")){
            language="fil";
        }
        */
        if (locale.equals("fr")){
            wikiHref=WIKI_FR;
            language="fr";
            showWiki = true;
        }
        if (locale.equals("hi")){
            wikiHref=WIKI_HI;
            language="hi";
        }
        if (locale.equals("hu")){
            wikiHref=WIKI_HU;
            language="hu";
        }
        if (locale.equals("in")){
            wikiHref=WIKI_IN;
            language="in";
        }
        if (locale.equals("it")){
            wikiHref=WIKI_IT;
            language="it";
            showWiki = true;
        }
        if (locale.equals("ja")){
            wikiHref=WIKI_JA;
            language="ja";
        }
        if (locale.equals("ko")){
            wikiHref=WIKI_KO;
            language="ko";
        }
        if (locale.equals("nl")){
            wikiHref=WIKI_NL;
            language="nl";
        }
        if (locale.equals("pl")){
            wikiHref=WIKI_PL;
            language="pl";
        }
        if (locale.equals("pt")){
            wikiHref=WIKI_PT;
            language="pt";
            showWiki = true;
        }
        if (locale.equals("ro")){
            wikiHref=WIKI_RO;
            language="ro";
        }
        if (locale.equals("ru")){
            wikiHref=WIKI_RU;
            language="ru";
            showWiki = true;
        }
        if (locale.equals("sv")){
            wikiHref=WIKI_SV;
            language="sv";
        }
        if (locale.equals("tr")){
            wikiHref=WIKI_TR;
            language="tr";
        }
        if (locale.equals("uk")){
            wikiHref=WIKI_UK;
            language="uk";
            showWiki = true;
        }
        if (locale.equals("zh")){
            wikiHref=WIKI_ZH;
            language="zh";
        }








        /*
        if (locale.equals("be")){
            language="be";
        }
        if (locale.equals("hi")){
            language="hi";
        }
        if (locale.equals("pl")){
            language="pl";
        }


        if (locale.equals("zh")){
            language="zh";
        }
        if (locale.equals("fi")){
            language="fi";
        }
        if (locale.equals("ko")){
            language="ko";
        }


        if (locale.equals("tr")){
            language="tr";
        }
        if (locale.equals("ja")){
            language="ja";
        }
        */

    }

    private void initData() {
        wild = new ArrayList<>();

        wild.add(new Animal(getString(R.string.bear),R.drawable.w0hd,R.raw.w0,true,"w0.gif"));
        wild.add(new Animal(getString(R.string.elephant),R.drawable.w5hd,R.raw.w5));
        wild.add(new Animal(getString(R.string.wolf),R.drawable.w1hd,R.raw.w1,true,"w1.gif"));

        wild.add(new Animal(getString(R.string.camel),R.drawable.w6hd,R.raw.w06));
        wild.add(new Animal(getString(R.string.monkey),R.drawable.w4hd,R.raw.w4,true,"w4.gif"));
        wild.add(new Animal(getString(R.string.jackal),R.drawable.w8hd,R.raw.w08));
        wild.add(new Animal(getString(R.string.zebra),R.drawable.w7hd,R.raw.w07));
        wild.add(new Animal(getString(R.string.leo),R.drawable.w2hd,R.raw.w2,true,"w2.gif"));
        wild.add(new Animal(getString(R.string.rhino),R.drawable.w12hd,R.raw.w12));
        wild.add(new Animal(getString(R.string.tiger),R.drawable.w3hd,R.raw.w3,true,"w3.gif"));

        wild.add(new Animal(getString(R.string.snake),R.drawable.w9hd,R.raw.w09));
        wild.add(new Animal(getString(R.string.fox),R.drawable.w10hd,R.raw.w10,true,"w10.gif"));
        wild.add(new Animal(getString(R.string.hare),R.drawable.w11hd,R.raw.w11));

        wild.add(new Animal(getString(R.string.crocodile),R.drawable.w13hd,R.raw.w13));
        wild.add(new Animal(getString(R.string.koala),R.drawable.w14hd,R.raw.w14));
        wild.add(new Animal(getString(R.string.panda),R.drawable.w15hd,R.raw.w15,true,"w15.gif"));
        wild.add(new Animal(getString(R.string.kangoroo),R.drawable.w16hd,R.raw.w16));
        wild.add(new Animal(getString(R.string.lemur),R.drawable.w17hd,R.raw.w17,true,"w17.gif"));
        wild.add(new Animal(getString(R.string.lynx),R.drawable.w18hd,R.raw.w18));
        wild.add(new Animal(getString(R.string.elk),R.drawable.w19hd,R.raw.w19));
        wild.add(new Animal(getString(R.string.racoon),R.drawable.w20hd,R.raw.w20));
        wild.add(new Animal(getString(R.string.squirrel),R.drawable.w21hd,R.raw.w21));
        wild.add(new Animal(getString(R.string.rat),R.drawable.w22hd,R.raw.w22,true,"w22.gif"));
        wild.add(new Animal(getString(R.string.jaguar),R.drawable.w24hd,R.raw.w24));
        wild.add(new Animal(getString(R.string.mouse),R.drawable.w23hd,R.raw.w23,true,"w23.gif"));

        wild.add(new Animal(getString(R.string.hippopotamus),R.drawable.w25hd,R.raw.w25));
        wild.add(new Animal(getString(R.string.badger),R.drawable.w26barsuk, R.raw.w26));
        wild.add(new Animal(getString(R.string.beaver),R.drawable.w27beaver, R.raw.w27));
        wild.add(new Animal(getString(R.string.deer),R.drawable.w28deer, R.raw.w28));
        wild.add(new Animal(getString(R.string.hedgehog),R.drawable.w29hedgehog, R.raw.w29,true,"w29.gif"));
        wild.add(new Animal(getString(R.string.giraffe),R.drawable.w30giraffe, R.raw.w30));
        wild.add(new Animal(getString(R.string.mole),R.drawable.w31mole, R.raw.w31));
        wild.add(new Animal(getString(R.string.skunk),R.drawable.w32skunk, R.raw.w32));
        wild.add(new Animal(getString(R.string.boar),R.drawable.w33boar, R.raw.w33));
        wild.add(new Animal(getString(R.string.bison),R.drawable.w34bison, R.raw.w34));
        wild.add(new Animal(getString(R.string.chipmunk),R.drawable.w35chipmunk,R.raw.w35));
        wild.add(new Animal(getString(R.string.alpaca),R.drawable.w36alpaca,R.raw.w36,true,"w36.gif"));
        wild.add(new Animal(getString(R.string.hyena),R.drawable.w37hyena,R.raw.w37));
        wild.add(new Animal(getString(R.string.bat),R.drawable.b28bat,R.raw.b28));
        wild.add(new Animal(getString(R.string.armadillo),R.drawable.w38,R.raw.w38));
        wild.add(new Animal(getString(R.string.wombat),R.drawable.w39,R.raw.w39));
        wild.add(new Animal(getString(R.string.capybara),R.drawable.w40,R.raw.w40));
        wild.add(new Animal(getString(R.string.meerkat),R.drawable.w41,R.raw.w41));
        wild.add(new Animal(getString(R.string.quokka),R.drawable.w42,R.raw.w42));
        wild.add(new Animal(getString(R.string.sloth),R.drawable.w43,R.raw.w43));
        wild.add(new Animal(getString(R.string.monitorlizard),R.drawable.w44,R.raw.w44));
        wild.add(new Animal(getString(R.string.anteater),R.drawable.w45,R.raw.w45));
        wild.add(new Animal(getString(R.string.ferret),R.drawable.w46,R.raw.w46));
        wild.add(new Animal(getString(R.string.lizard),R.drawable.w47,R.raw.w47));
        wild.add(new Animal(getString(R.string.wolverine),R.drawable.w48,R.raw.w48));

        home = new ArrayList<>();
        home.add(new Animal(getString(R.string.dog),R.drawable.h0hd,R.raw.h0,true,"h0.gif"));
        home.add(new Animal(getString(R.string.cat),R.drawable.h1hd,R.raw.h1,true,"h1hd.gif"));
        home.add(new Animal(getString(R.string.pig),R.drawable.h2hd,R.raw.h2));
        home.add(new Animal(getString(R.string.cock),R.drawable.h3hd,R.raw.h3));
        home.add(new Animal(getString(R.string.chiken),R.drawable.h4hd,R.raw.h4));
        home.add(new Animal(getString(R.string.cow),R.drawable.h5hd,R.raw.h5,true,"h5.gif"));
        home.add(new Animal(getString(R.string.sheep),R.drawable.h7hd,R.raw.h7));
        home.add(new Animal(getString(R.string.horse),R.drawable.h6hd,R.raw.h6,true,"h6.gif"));

        home.add(new Animal(getString(R.string.goat),R.drawable.h8hd,R.raw.h8));
        home.add(new Animal(getString(R.string.donkey),R.drawable.h9hd,R.raw.h9));

        home.add(new Animal(getString(R.string.cavy),R.drawable.h11hd,R.raw.h11,true,"h11.gif"));
        home.add(new Animal(getString(R.string.turkey),R.drawable.h10hd,R.raw.h10));
        home.add(new Animal(getString(R.string.rabbit),R.drawable.h12rabbit,R.raw.h12,true,"h12.gif"));
        home.add(new Animal(getString(R.string.pony),R.drawable.h13,R.raw.h13));

        aqua = new ArrayList<>();
        aqua.add(new Animal(getString(R.string.dolphin),R.drawable.a0hd,R.raw.a0,true,"a0.gif"));
        aqua.add(new Animal(getString(R.string.shark),R.drawable.a11hd,R.raw.a11));
        aqua.add(new Animal(getString(R.string.sealbark),R.drawable.a1hd,R.raw.a1));
        aqua.add(new Animal(getString(R.string.frog),R.drawable.a2hd,R.raw.a2));
        aqua.add(new Animal(getString(R.string.penguin),R.drawable.a3hd,R.raw.a3,true,"a3.gif"));
        aqua.add(new Animal(getString(R.string.walrus),R.drawable.a4hd,R.raw.a4));
        aqua.add(new Animal(getString(R.string.sealion),R.drawable.a5hd,R.raw.a5));
        aqua.add(new Animal(getString(R.string.whale),R.drawable.a6hd,R.raw.a6));

        aqua.add(new Animal(getString(R.string.turtle),R.drawable.a8turtle,R.raw.a8,true,"a8.gif"));
        aqua.add(new Animal(getString(R.string.fish),R.drawable.a7hd,R.raw.a7));
        aqua.add(new Animal(getString(R.string.otter),R.drawable.a9otter,R.raw.a9,true,"a9.gif"));
        aqua.add(new Animal(getString(R.string.lobster),R.drawable.a10lobster,R.raw.a10));

        birds = new ArrayList<>();
        birds.add(new Animal(getString(R.string.goose),R.drawable.b0hd,R.raw.b0,true,"b0.gif"));
        birds.add(new Animal(getString(R.string.duck),R.drawable.b1hd,R.raw.b1));
        birds.add(new Animal(getString(R.string.crow),R.drawable.b2hd,R.raw.b2));
        birds.add(new Animal(getString(R.string.seagull),R.drawable.b3hd,R.raw.b3));
        birds.add(new Animal(getString(R.string.dove),R.drawable.b4hd,R.raw.b4));
        birds.add(new Animal(getString(R.string.nightingale),R.drawable.b5hd,R.raw.b5));
        birds.add(new Animal(getString(R.string.eagle),R.drawable.b6hd,R.raw.b6,true,"b6.gif"));
        birds.add(new Animal(getString(R.string.hawk),R.drawable.b7hd,R.raw.b7));
        birds.add(new Animal(getString(R.string.woodpecker),R.drawable.b8hd,R.raw.b8,true,"b8.gif"));
        birds.add(new Animal(getString(R.string.pelican),R.drawable.b12hd,R.raw.b12));

        birds.add(new Animal(getString(R.string.parrot),R.drawable.b9hd,R.raw.b9,true,"b9.gif"));
        birds.add(new Animal(getString(R.string.catbird),R.drawable.b16catbird,R.raw.b16));
        birds.add(new Animal(getString(R.string.owl),R.drawable.b10hd,R.raw.b10,true,"b10.gif"));
        birds.add(new Animal(getString(R.string.cuckoo),R.drawable.b11hd,R.raw.b11));

        birds.add(new Animal(getString(R.string.ostrich),R.drawable.b13hd,R.raw.b13));
        birds.add(new Animal(getString(R.string.flamingo),R.drawable.b14hd,R.raw.b14,true,"b14.gif"));
        birds.add(new Animal(getString(R.string.peacock),R.drawable.b15hd,R.raw.b15));

        birds.add(new Animal(getString(R.string.tit),R.drawable.b17tit,R.raw.b17));
        birds.add(new Animal(getString(R.string.toucan),R.drawable.b18toucan,R.raw.b18));
        birds.add(new Animal(getString(R.string.robin),R.drawable.b19robin,R.raw.b19));
        birds.add(new Animal(getString(R.string.blackgrouse),R.drawable.b20blackgrouse,R.raw.b20));
        birds.add(new Animal(getString(R.string.hummingbird),R.drawable.b21hummingbird,R.raw.b21));
        birds.add(new Animal(getString(R.string.bullfinch),R.drawable.b23bullfinch,R.raw.b23));
        birds.add(new Animal(getString(R.string.stork),R.drawable.b24stork,R.raw.b24));
        birds.add(new Animal(getString(R.string.heron),R.drawable.b25heron,R.raw.b25));
        birds.add(new Animal(getString(R.string.canary),R.drawable.b26canary,R.raw.b26));
        birds.add(new Animal(getString(R.string.magpie),R.drawable.b27magpie,R.raw.b27));

        birds.add(new Animal(getString(R.string.jay),R.drawable.b29jay,R.raw.b29));
        birds.add(new Animal(getString(R.string.starling),R.drawable.b30starling,R.raw.b30));
        birds.add(new Animal(getString(R.string.sparrow),R.drawable.b31,R.raw.b31));

        insects = new ArrayList<>();
        insects.add(new Animal(getString(R.string.bees),R.drawable.i0hd,R.raw.i00));
        insects.add(new Animal(getString(R.string.flies),R.drawable.i1hd,R.raw.i01));
        insects.add(new Animal(getString(R.string.mosquito),R.drawable.i2hd,R.raw.i02,true,"i2.gif"));
        insects.add(new Animal(getString(R.string.grasshopper),R.drawable.i3hd,R.raw.i3));
        insects.add(new Animal(getString(R.string.bumblebee),R.drawable.i4hd,R.raw.i4));
        insects.add(new Animal(getString(R.string.cricket),R.drawable.i5hd,R.raw.i5));
        insects.add(new Animal(getString(R.string.butterfly),R.drawable.i6hd,R.raw.i6,true,"i6.gif"));
        insects.add(new Animal(getString(R.string.dragonfly),R.drawable.i7hd,R.raw.i7));
        insects.add(new Animal(getString(R.string.ants),R.drawable.i8hd,R.raw.i8));
        insects.add(new Animal(getString(R.string.mantis),R.drawable.i9hd,R.raw.i9));
        insects.add(new Animal(getString(R.string.cicada), R.drawable.i10hd,R.raw.i10));
        insects.add(new Animal(getString(R.string.spider), R.drawable.i11,R.raw.i11));
        insects.add(new Animal(getString(R.string.scorpion),R.drawable.i12,R.raw.i12));


        fairy = new ArrayList<>();
        fairy.add(new Animal(getString(R.string.dragon), R.drawable.f0hd,R.raw.f0));
        fairy.add(new Animal(getString(R.string.unicorn), R.drawable.f1hd,R.raw.f1));
        fairy.add(new Animal(getString(R.string.pokemon), R.drawable.f2hd,R.raw.f2,true, "f2.gif"));
        fairy.add(new Animal(getString(R.string.buckbeak), R.drawable.f3hd,R.raw.f3));
        fairy.add(new Animal(getString(R.string.dinosaur), R.drawable.f4hd,R.raw.f4));
        fairy.add(new Animal(getString(R.string.pegasus), R.drawable.f5hd,R.raw.f5));
        fairy.add(new Animal(getString(R.string.centaur), R.drawable.f6hd,R.raw.f6));
        fairy.add(new Animal(getString(R.string.phoenix), R.drawable.f7hd,R.raw.f7,true, "f7.gif"));
        fairy.add(new Animal(getString(R.string.waternymph), R.drawable.f8hd,R.raw.f8));
        fairy.add(new Animal(getString(R.string.griffon), R.drawable.f9hd,R.raw.f9));
        fairy.add(new Animal(getString(R.string.yeti), R.drawable.f10hd,R.raw.f10));


        animals =new ArrayList<Animal>();
        animals.addAll(home);
        animals.addAll(wild);
        animals.addAll(birds);
        animals.addAll(aqua);
        animals.addAll(insects);
        if (unlockCounter>9) {
            animals.addAll(fairy);
        }

    }



    private void fetch() {

        long cacheExpiration = 01;
        if (!notFirstStart) {
            cacheExpiration = 1; // 1 hour in seconds.
        }

        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
       // if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
       //     cacheExpiration = 0;
       // }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activate();

                            SharedPreferences getPrefs = PreferenceManager
                                    .getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor e = getPrefs.edit();

                            if (mFirebaseRemoteConfig.getBoolean(ADS_DISABLE_KEY)) {
                                ads_disable_button=true;
                            }
                            else {
                                ads_disable_button=false;
                            }


                            if (mFirebaseRemoteConfig.getBoolean(GRID_MINIMIZATION_KEY)){
                                grid=true;

                            }
                            else {
                                grid=false;

                            }

                            if (!mFirebaseRemoteConfig.getBoolean(REVIEW_ENABLED)) {
                                review_enabled = false;
                            }

                            e.putBoolean(GRID_MINIMIZATION_KEY,grid);
                            e.putBoolean("ads_disable_button_key", ads_disable_button);
                            e.apply();



                        }
                        //else {
                           // Toast.makeText(MainActivity.this, "Fetch Failed",
                           //         Toast.LENGTH_SHORT).show();
                        //}

                    }
                });
        // [END fetch_config_with_callback]
    }

    public void incAdCounter(){
        adCount++;

        /*
        Toast toast = Toast.makeText(this,
                "adCount" + adCount, Toast.LENGTH_SHORT);

        toast.show();
        */


        if (adCount==3||adCount>20) {
            loadInterstitial();
            /*
            Toast toast = Toast.makeText(this,
                    "adCount==11 loadInterstitial()", Toast.LENGTH_SHORT);

            toast.show();
            */
        }
    }

    public int getAdCounter(){
        return adCount;
    }

    public void zeroAdCounter(){
       // adCount=0;
    }

    public boolean getGrid (){
        return grid;
    }

    @Override
    public void onBackPressed() {

        if (wiki.isShown()){
            stopWiki();
        }

        else {

            incrementRating();
            if (backPressedToExitOnce) {
                //if (false){
                super.onBackPressed();
                return;
            } else {
                if (review_enabled) {
                    showRatingDialog();
                }
                backPressedToExitOnce = true;

            }
        }

    }

    public void incrementRating(){
        numRatingDialog++;
        saveInt(NUMBER_OF_RATING_START_KEY,numRatingDialog);
        if (numRatingDialog>3){
            backPressedToExitOnce=true;
            saveBoolean(PRESSED_BACK_ONCE_KEY,true);
            //Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
        }
        if (numRatingDialog==10||numRatingDialog==20||numRatingDialog==30||numRatingDialog==40||numRatingDialog==50){
            backPressedToExitOnce=false;
            saveBoolean(PRESSED_BACK_ONCE_KEY,false);
            //Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
        }

    }

    public void incrementUnlockCounter (){
        unlockCounter++;
        saveInt(KEY_TO_UNLOCK_FAIRY,unlockCounter);
    }

    private void openPlaystore(Context context) {
        final Uri marketUri = Uri.parse("market://details?id=com.yamilab.animalsounds");
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Couldn't find PlayStore on this device", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveBoolean(String key, boolean value){
        SharedPreferences getPrefs1 = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = getPrefs1.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void saveInt(String key, int value){
        SharedPreferences getPrefs1 = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = getPrefs1.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void setGameTab (){
        tab = tabLayout.getTabAt(new Random().nextInt(3));
        tab.select();
    }

    public void startWiki (String url){
       // WebSettings webSettings = wiki.getSettings();
        //webSettings.setBuiltInZoomControls(false);
        mFirebaseAnalytics.logEvent("wiki_show", null);
        incAdCounter();

        if (adCount>adShowInt)
                showInterstitial();

        wiki.getSettings().setJavaScriptEnabled(true);
        wiki.setWebViewClient(new WebViewClient(){

            String jsFunction=new StringBuilder()
                    .append("var heading = document.getElementsByClassName(\"collapsible-heading\");\n")
                    .append("for (var i = 0; i < heading.length; i++) {\n")
                    .append("if(heading[i].classList.contains(\"open-block\")) {\n")
                    .append("heading[i].className = heading[i].className.replace(\"open-block\", \"close-block\");\n}\n")
                    .append("else {\n")
                    .append("heading[i].className+= \" close-block\";\n}\n}\n")
                    .append("var section = document.getElementsByClassName(\"collapsible-block\");\n")
                    .append("for (var i = 0; i < section.length; i++) {\n")
                    .append("if(section[i].classList.contains(\"open-block\")) {\n")
                    .append("section[i].className = section[i].className.replace(\"open-block\", \"close-block\");\n}\n")
                    .append("else {\n")
                    .append("section[i].className+= \" close-block\";\n}\n}\n")
                    .toString();

            boolean ok=true;
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Handle the error
                ok=false;
                Toast toast = Toast.makeText(MainActivity.this,
                                 "Check connection " + errorCode + " " + description, Toast.LENGTH_SHORT);
                           toast.show();
                stopWiki();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //wiki.loadUrl("javascript:(function(){"+jsFunction+"})()");

                if (ok) {
                   // wiki.loadUrl("javascript:(function(){"+jsFunction+"})()");
                    wiki.setVisibility(View.VISIBLE);
                    stopWiki.setVisibility(View.VISIBLE);
                    progressWiki.setVisibility(View.INVISIBLE);
                }
                 wiki.loadUrl("javascript:(function() { " +
                         "document.getElementsByClassName ('header-container header-chrome')[0].style.display='none';"+
                      //   "document.getElementsByClassName ('pre-content heading-holder')[0].style.display='none';"+
                         "document.getElementsByClassName ('page-actions-menu')[0].style.display='none';"+
                         "document.getElementsByClassName ('minerva-footer')[0].style.display='none';"+
                      //   "document.getElementsByClassName ('plainlinks metadata ambox ambox-discussion').style.display='none';"+
                         "document.getElementsByClassName ('hatnote rellink')[0].style.display='none';"+
                         "document.getElementsByClassName ('dablink hatnote')[0].style.display='none';"+
                         "document.getElementsByClassName ('dablink hatnote')[1].style.display='none';"+
                         "document.getElementsByClassName ('dablink hatnote')[2].style.display='none';"+
                         "document.getElementsByClassName ('dablink hatnote')[3].style.display='none';"+
                         "document.getElementsByClassName ('dablink hatnote')[4].style.display='none';"+
                         "document.getElementsByClassName ('dablink hatnote')[5].style.display='none';"+
                        // "document.getElementsByClassName ('othermeaning-box').style.display='none';"+
                         "document.getElementsByClassName ('dablink hatnote noprint')[0].style.display='none';"+

                          "})()");


                //wiki.loadUrl("javascript:(function(){"+jsFunction+"})()");
               // wiki.loadUrl("javascript:(function() { " +
               //         "document.getElementsByClassName ('header-container header-chrome')[0].style.display='none';"
               //         +"})()");
               // wiki.loadUrl("javascript:(function() { " +
               //         "document.getElementsByClassName ('page-actions-menu')[0].style.display='none';"
               //         +"})()");
               // wiki.loadUrl("javascript:(function() { " +
               //         "document.getElementsByClassName ('dablink hatnote noprint')[0].style.display='none';"
               //         +"})()");
               // wiki.loadUrl("javascript:(function() { " +
                //        "document.getElementsByClassName ('dablink hatnote')[0].style.display='none';"
                //        +"})()");
            }


            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon){
                progressWiki.setVisibility(View.VISIBLE);
            }

        });
        wiki.loadUrl(wikiHref+url);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressWiki.setVisibility(View.INVISIBLE);
            }
        }, 7000);

    }

    public void stopWiki(){
        //wiki.loadUrl("about:blank");
        progressWiki.setVisibility(View.INVISIBLE);
        wiki.setVisibility(View.INVISIBLE);
        stopWiki.setVisibility(View.INVISIBLE);
    }

    public boolean showWiki (){
       return showWiki;
    }

}
