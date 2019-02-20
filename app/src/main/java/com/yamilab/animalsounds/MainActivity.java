package com.yamilab.animalsounds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity implements TTSListener  {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

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
    private ImageView imageViewBackground;

    private TextToSpeech tts;

    private ArrayList<Animal> wild, home, aqua, birds, insects, fairy,animals;
    private int screenWidth=800,screenHeight=1280;
    private String language="en";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Debug.startMethodTracing("sample");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

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

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, MyIntro.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);
                    showInterstitialAd = false;
                    notFirstStart = false;
                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main);



        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initData();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        int[] resID={
                R.drawable.tab_home,
                R.drawable.tab_wild,
                R.drawable.tab_birds,
                R.drawable.tab_aqua,
                R.drawable.tab_insects,
                R.drawable.tab_fairy};

        tabLayout.getTabAt(0).setIcon( R.drawable.tab_game);
        tabLayout.getTabAt(1).setText("Ads");

        for (int i = 2; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(resID[i-1]);
        }

        TabLayout.Tab tab = tabLayout.getTabAt(2);
        tab.select();









        // Create the InterstitialAd and set the adUnitId.

        makeLanguageList(Locale.getDefault().getLanguage());

        try {
            new TtsInit().execute();
        }
        catch (Exception e){

        };




        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2888343178529026~2046736590");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                // .addNetworkExtrasBundle(AdMobAdapter.class, extrasAdview)
                // .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                //.tagForChildDirectedTreatment(true)
                .addTestDevice("A4203BC89A24BEEC45D1111F16D2F0A3")
                //.addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
                .build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2888343178529026/6970013790");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLoaded(){
                // showInterstitial();
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
                .override((int)screenWidth/3, (int) screenHeight/3)
                .fitCenter()
                // .thumbnail()
                //.error(R.mipmap.ic_launcher)
                .placeholder(new ColorDrawable(getResources().getColor(R.color.colorBackground)))
                //.placeholder(R.mipmap.placeholder)
                .transition(withCrossFade(1000))
                .into((ImageView) findViewById(R.id.imageViewBackground));

        loadInterstitial();

       // Debug.stopMethodTracing();
    }




    private void loadInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder()
                    //.tagForChildDirectedTreatment(true)
                    .addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
                    .build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }





    @Override
    public void speak(String text,int sound) {
        final String textapi=text;




            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                try {
                    tts.speak(textapi, TextToSpeech.QUEUE_FLUSH, null);
                }

                catch (Exception e){
                   // SoundPlay.playSP(this,sound);
                }


            } else {
                try {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
                }
                catch (Exception e){
                    //SoundPlay.playSP(this,sound);
                }

            }

    }

    /**
     * A placeholder fragment containing a simple view.
     */







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
    public class SectionsPagerAdapter extends SmartFragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            adCount++;
            if (adCount>6 && showInterstitialAd){
                showInterstitial();
                //loadInterstitial();
                //adCount=0;
            }
            switch (position) {

                case 0:

                    return ImageGridFragmentGame.newInstance(animals,screenWidth);


                case 1:

                    return new ImageGridFragmentAds();


                case 2:

                    return ImageGridFragment.newInstance( home,screenWidth);

                case 3:

                    return ImageGridFragment.newInstance( wild,screenWidth);
                case 4:

                    return ImageGridFragment.newInstance( birds,screenWidth);
                case 5:

                    return ImageGridFragment.newInstance( aqua,screenWidth);
                case 6:

                    return ImageGridFragment.newInstance( insects,screenWidth);
                case 7:

                    return ImageGridFragment.newInstance(fairy,screenWidth);

            }
            return ImageGridFragment.newInstance(home,screenWidth);//PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 7;
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume the AdView.
        mAdView.resume();
    }

    @Override
    public void onPause() {
        // Pause the AdView.
        mAdView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        mAdView.destroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public class TtsInit extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status==TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(new Locale(language, ""));
                    }
                    else
                    {

                    }
                }
            });
            return null;
        }
    }

    private void makeLanguageList(String locale){
        if (locale.equals("ru")){
            language="ru";
        }
        if (locale.equals("uk")){
            language="uk";
        }

        if (locale.equals("it")){
            language="it";
        }
        if (locale.equals("es")){
            language="es";
        }

        if (locale.equals("fr")){
            language="fr";
        }
        if (locale.equals("de")){
            language="de";
        }

        if (locale.equals("pt")){
            language="pt";
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
        wild.add(new Animal(getString(R.string.wolf),R.drawable.w1hd,R.raw.w1,true,"w1.gif"));
        wild.add(new Animal(getString(R.string.leo),R.drawable.w2hd,R.raw.w2,true,"w2.gif"));
        wild.add(new Animal(getString(R.string.tiger),R.drawable.w3hd,R.raw.w3,true,"w3.gif"));
        wild.add(new Animal(getString(R.string.monkey),R.drawable.w4hd,R.raw.w4,true,"w4.gif"));
        wild.add(new Animal(getString(R.string.elephant),R.drawable.w5hd,R.raw.w5));
        wild.add(new Animal(getString(R.string.camel),R.drawable.w6hd,R.raw.w06));
        wild.add(new Animal(getString(R.string.zebra),R.drawable.w7hd,R.raw.w07));
        wild.add(new Animal(getString(R.string.jackal),R.drawable.w8hd,R.raw.w08));
        wild.add(new Animal(getString(R.string.snake),R.drawable.w9hd,R.raw.w09));
        wild.add(new Animal(getString(R.string.fox),R.drawable.w10hd,R.raw.w10,true,"w10.gif"));
        wild.add(new Animal(getString(R.string.hare),R.drawable.w11hd,R.raw.w11));
        wild.add(new Animal(getString(R.string.rhino),R.drawable.w12hd,R.raw.w12));
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
        wild.add(new Animal(getString(R.string.mouse),R.drawable.w23hd,R.raw.w23,true,"w23.gif"));
        wild.add(new Animal(getString(R.string.jaguar),R.drawable.w24hd,R.raw.w24));
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

        home = new ArrayList<>();
        home.add(new Animal(getString(R.string.dog),R.drawable.h0hd,R.raw.h0,true,"h0.gif"));
        home.add(new Animal(getString(R.string.cat),R.drawable.h1hd,R.raw.h1,true,"h1hd.gif"));
        home.add(new Animal(getString(R.string.pig),R.drawable.h2hd,R.raw.h2));
        home.add(new Animal(getString(R.string.cock),R.drawable.h3hd,R.raw.h3));
        home.add(new Animal(getString(R.string.chiken),R.drawable.h4hd,R.raw.h4));
        home.add(new Animal(getString(R.string.cow),R.drawable.h5hd,R.raw.h5,true,"h5.gif"));
        home.add(new Animal(getString(R.string.horse),R.drawable.h6hd,R.raw.h6,true,"h6.gif"));
        home.add(new Animal(getString(R.string.sheep),R.drawable.h7hd,R.raw.h7));
        home.add(new Animal(getString(R.string.goat),R.drawable.h8hd,R.raw.h8));
        home.add(new Animal(getString(R.string.donkey),R.drawable.h9hd,R.raw.h9));
        home.add(new Animal(getString(R.string.turkey),R.drawable.h10hd,R.raw.h10));
        home.add(new Animal(getString(R.string.cavy),R.drawable.h11hd,R.raw.h11,true,"h11.gif"));
        home.add(new Animal(getString(R.string.rabbit),R.drawable.h12rabbit,R.raw.h12,true,"h12.gif"));

        aqua = new ArrayList<>();
        aqua.add(new Animal(getString(R.string.dolphin),R.drawable.a0hd,R.raw.a0,true,"a0.gif"));
        aqua.add(new Animal(getString(R.string.sealbark),R.drawable.a1hd,R.raw.a1));
        aqua.add(new Animal(getString(R.string.frog),R.drawable.a2hd,R.raw.a2));
        aqua.add(new Animal(getString(R.string.penguin),R.drawable.a3hd,R.raw.a3,true,"a3.gif"));
        aqua.add(new Animal(getString(R.string.walrus),R.drawable.a4hd,R.raw.a4));
        aqua.add(new Animal(getString(R.string.sealion),R.drawable.a5hd,R.raw.a5));
        aqua.add(new Animal(getString(R.string.whale),R.drawable.a6hd,R.raw.a6));
        aqua.add(new Animal(getString(R.string.fish),R.drawable.a7hd,R.raw.a7));
        aqua.add(new Animal(getString(R.string.turtle),R.drawable.a8turtle,R.raw.a8,true,"a8.gif"));
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
        birds.add(new Animal(getString(R.string.parrot),R.drawable.b9hd,R.raw.b9,true,"b9.gif"));
        birds.add(new Animal(getString(R.string.owl),R.drawable.b10hd,R.raw.b10,true,"b10.gif"));
        birds.add(new Animal(getString(R.string.cuckoo),R.drawable.b11hd,R.raw.b11));
        birds.add(new Animal(getString(R.string.pelican),R.drawable.b12hd,R.raw.b12));
        birds.add(new Animal(getString(R.string.ostrich),R.drawable.b13hd,R.raw.b13));
        birds.add(new Animal(getString(R.string.flamingo),R.drawable.b14hd,R.raw.b14,true,"b14.gif"));
        birds.add(new Animal(getString(R.string.peacock),R.drawable.b15hd,R.raw.b15));
        birds.add(new Animal(getString(R.string.catbird),R.drawable.b16catbird,R.raw.b16));
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
        birds.add(new Animal(getString(R.string.bat),R.drawable.b28bat,R.raw.b28));
        birds.add(new Animal(getString(R.string.jay),R.drawable.b29jay,R.raw.b29));
        birds.add(new Animal(getString(R.string.starling),R.drawable.b30starling,R.raw.b30));

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


        animals =new ArrayList<Animal>();
        animals.addAll(home);
        animals.addAll(wild);
        animals.addAll(birds);
        animals.addAll(aqua);
        animals.addAll(insects);
        animals.addAll(fairy);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // if (data == null) {return;}
       // String name = data.getStringExtra("name");
       // tvName.setText("Your name is " + name);
        mViewPager.setCurrentItem(3);
    }



}
