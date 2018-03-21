package com.yamilab.animalsounds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity implements TTSListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    int[] resID={R.drawable.tab_home,R.drawable.tab_wild,R.drawable.tab_birds,R.drawable.tab_aqua,R.drawable.tab_insects};
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

    private ArrayList<Animal> wild, home, aqua, birds, insects;
    private int screenWidth=800,screenHeight=1280;
    private String language="en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);





        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        makeLanguageList(Locale.getDefault().getLanguage());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2888343178529026~2046736590");
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



        setContentView(R.layout.activity_main);

        GlideApp.with(this)
                // .asDrawable()
                // .load(mStorageRef.child(mDataSet.get(position).getImage()))
                .load(R.drawable.background)
               // .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.LOW)
                //.load(internetUrl)
                //.skipMemoryCache(true)
                .override((int)screenWidth/3, (int) screenHeight/3)
                .fitCenter()
                // .thumbnail()
                //.error(R.mipmap.ic_launcher)
                .placeholder(new ColorDrawable(getResources().getColor(R.color.colorBackground)))
                //.placeholder(R.mipmap.placeholder)
                .transition(withCrossFade(1000))
                .into((ImageView) findViewById(R.id.imageViewBackground));


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initData();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText("Ads");
        for (int i = 1; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(resID[i-1]);
        }

        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();








        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2888343178529026/6970013790");
        loadInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLoaded(){
               // showInterstitial();
            }
        });


        try {
            new TtsInit().execute();
        }
        catch (Exception e){

        };

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                // .addNetworkExtrasBundle(AdMobAdapter.class, extrasAdview)
                // .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                //.tagForChildDirectedTreatment(true)
                .addTestDevice("A4203BC89A24BEEC45D1111F16D2F0A3")
                //.addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
                .build();
        mAdView.loadAd(adRequest);
    }




    private void loadInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder()
                    //.tagForChildDirectedTreatment(true)
                   // .addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
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





    public static class ImageGridFragment extends Fragment{
        RecyclerView recyclerView;
        StaggeredGridLayoutManager staggeredGridLayoutManager;
        AnimalAdapter animalAdapter;
        GlideRequests glideRequests;

        public ImageGridFragment(){

        }

        public static ImageGridFragment newInstance (ArrayList array, int screenWidth) {
            ImageGridFragment fragment = new ImageGridFragment();
            Bundle args = new Bundle();
            args.putSerializable("key", array);
            args.putInt("width", screenWidth);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            glideRequests=GlideApp.with(rootView.getContext());
            int spanCount = 2;

            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;

            if (screenSize>=Configuration.SCREENLAYOUT_SIZE_LARGE) spanCount=3;

            recyclerView = rootView.findViewById(R.id.recyclerView);

            staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);

            animalAdapter = new AnimalAdapter((ArrayList<Animal>)getArguments().getSerializable("key"),
                    (int) getArguments().getInt("width")/(spanCount+1),
                    glideRequests);

            recyclerView.setAdapter(animalAdapter);


            recyclerView.getRecycledViewPool().setMaxRecycledViews(0,spanCount*3);
            recyclerView.setItemViewCacheSize(0);

            return rootView;
        }
    }

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
            adCount++;
            if (adCount>5 && showInterstitialAd){
                showInterstitial();
                //loadInterstitial();
                //adCount=0;
            }
            switch (position) {

                case 0:

                    return new ImageGridFragmentAds();

                case 1:

                    return ImageGridFragment.newInstance(home,screenWidth);

                case 2:
                    return ImageGridFragment.newInstance(wild,screenWidth);
                case 3:
                    return ImageGridFragment.newInstance(birds,screenWidth);
                case 4:
                    return ImageGridFragment.newInstance(aqua,screenWidth);
                case 5:
                    return ImageGridFragment.newInstance(insects,screenWidth);
            }
            return ImageGridFragment.newInstance(home,screenWidth);//PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 6;
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
        if (locale.equals("be")){
            language="be";
        }
        if (locale.equals("pt")){
            language="pt";
        }
        if (locale.equals("hi")){
            language="hi";
        }
        if (locale.equals("pl")){
            language="pl";
        }
        if (locale.equals("it")){
            language="it";
        }
        if (locale.equals("es")){
            language="es";
        }
        if (locale.equals("pt")){
            language="pt";
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
        if (locale.equals("fr")){
            language="de";
        }
        if (locale.equals("tr")){
            language="tr";
        }
        if (locale.equals("ja")){
            language="ja";
        }


    }

    private void initData() {
        wild = new ArrayList<>();

        wild.add(new Animal(getString(R.string.bear),R.mipmap.w0hd,R.raw.w0,true,"w0.gif"));
        wild.add(new Animal(getString(R.string.wolf),R.mipmap.w1hd,R.raw.w1,true,"w1.gif"));
        wild.add(new Animal(getString(R.string.leo),R.mipmap.w2hd,R.raw.w2,true,"w2.gif"));
        wild.add(new Animal(getString(R.string.tiger),R.mipmap.w3hd,R.raw.w3,true,"w3.gif"));
        wild.add(new Animal(getString(R.string.monkey),R.mipmap.w4hd,R.raw.w4,true,"w4.gif"));
        wild.add(new Animal(getString(R.string.elephant),R.mipmap.w5hd,R.raw.w5));
        wild.add(new Animal(getString(R.string.camel),R.mipmap.w6hd,R.raw.w6));
        wild.add(new Animal(getString(R.string.zebra),R.mipmap.w7hd,R.raw.w7));
        wild.add(new Animal(getString(R.string.jackal),R.mipmap.w8hd,R.raw.w8));
        wild.add(new Animal(getString(R.string.snake),R.mipmap.w9hd,R.raw.w9));
        wild.add(new Animal(getString(R.string.fox),R.mipmap.w10hd,R.raw.w10,true,"w10.gif"));
        wild.add(new Animal(getString(R.string.hare),R.mipmap.w11hd,R.raw.w11));
        wild.add(new Animal(getString(R.string.rhino),R.mipmap.w12hd,R.raw.w12));
        wild.add(new Animal(getString(R.string.crocodile),R.mipmap.w13hd,R.raw.w13));
        wild.add(new Animal(getString(R.string.koala),R.mipmap.w14hd,R.raw.w14));
        wild.add(new Animal(getString(R.string.panda),R.mipmap.w15hd,R.raw.w15,true,"w15.gif"));
        wild.add(new Animal(getString(R.string.kangoroo),R.mipmap.w16hd,R.raw.w16));
        wild.add(new Animal(getString(R.string.lemur),R.mipmap.w17hd,R.raw.w17,true,"w17.gif"));
        wild.add(new Animal(getString(R.string.lynx),R.mipmap.w18hd,R.raw.w18));
        wild.add(new Animal(getString(R.string.elk),R.mipmap.w19hd,R.raw.w19));
        wild.add(new Animal(getString(R.string.racoon),R.mipmap.w20hd,R.raw.w20));
        wild.add(new Animal(getString(R.string.squirrel),R.mipmap.w21hd,R.raw.w21));
        wild.add(new Animal(getString(R.string.rat),R.mipmap.w22hd,R.raw.w22,true,"w22.gif"));
        wild.add(new Animal(getString(R.string.mouse),R.mipmap.w23hd,R.raw.w23,true,"w23.gif"));
        wild.add(new Animal(getString(R.string.jaguar),R.mipmap.w24hd,R.raw.w24));
        wild.add(new Animal(getString(R.string.hippopotamus),R.mipmap.w25hd,R.raw.w25));
        wild.add(new Animal(getString(R.string.badger),R.mipmap.w26barsuk, R.raw.w26));
        wild.add(new Animal(getString(R.string.beaver),R.mipmap.w27beaver, R.raw.w27));
        wild.add(new Animal(getString(R.string.deer),R.mipmap.w28deer, R.raw.w28));
        wild.add(new Animal(getString(R.string.hedgehog),R.mipmap.w29hedgehog, R.raw.w29,true,"w29.gif"));
        wild.add(new Animal(getString(R.string.giraffe),R.mipmap.w30giraffe, R.raw.w30));
        wild.add(new Animal(getString(R.string.mole),R.mipmap.w31mole, R.raw.w31));
        wild.add(new Animal(getString(R.string.skunk),R.mipmap.w32skunk, R.raw.w32));
        wild.add(new Animal(getString(R.string.boar),R.mipmap.w33boar, R.raw.w33));
        wild.add(new Animal(getString(R.string.bison),R.mipmap.w34bison, R.raw.w34));
        wild.add(new Animal(getString(R.string.chipmunk),R.mipmap.w35chipmunk,R.raw.w35));
        wild.add(new Animal(getString(R.string.alpaca),R.mipmap.w36alpaca,R.raw.w36,true,"w36.gif"));
        wild.add(new Animal(getString(R.string.hyena),R.mipmap.w37hyena,R.raw.w37));

        home = new ArrayList<>();
        home.add(new Animal(getString(R.string.dog),R.mipmap.h0hd,R.raw.h0,true,"h0.gif"));
        home.add(new Animal(getString(R.string.cat),R.mipmap.h1hd,R.raw.h1,true,"h1hd.gif"));
        home.add(new Animal(getString(R.string.pig),R.mipmap.h2hd,R.raw.h2));
        home.add(new Animal(getString(R.string.cock),R.mipmap.h3hd,R.raw.h3));
        home.add(new Animal(getString(R.string.chiken),R.mipmap.h4hd,R.raw.h4));
        home.add(new Animal(getString(R.string.cow),R.mipmap.h5hd,R.raw.h5,true,"h5.gif"));
        home.add(new Animal(getString(R.string.horse),R.mipmap.h6hd,R.raw.h6,true,"h6.gif"));
        home.add(new Animal(getString(R.string.sheep),R.mipmap.h7hd,R.raw.h7));
        home.add(new Animal(getString(R.string.goat),R.mipmap.h8hd,R.raw.h8));
        home.add(new Animal(getString(R.string.donkey),R.mipmap.h9hd,R.raw.h9));
        home.add(new Animal(getString(R.string.turkey),R.mipmap.h10hd,R.raw.h10));
        home.add(new Animal(getString(R.string.cavy),R.mipmap.h11hd,R.raw.h11,true,"h11.gif"));
        home.add(new Animal(getString(R.string.rabbit),R.mipmap.h12rabbit,R.raw.h12,true,"h12.gif"));

        aqua = new ArrayList<>();
        aqua.add(new Animal(getString(R.string.dolphin),R.mipmap.a0hd,R.raw.a0,true,"a0.gif"));
        aqua.add(new Animal(getString(R.string.sealbark),R.mipmap.a1hd,R.raw.a1));
        aqua.add(new Animal(getString(R.string.frog),R.mipmap.a2hd,R.raw.a2));
        aqua.add(new Animal(getString(R.string.penguin),R.mipmap.a3hd,R.raw.a3,true,"a3.gif"));
        aqua.add(new Animal(getString(R.string.walrus),R.mipmap.a4hd,R.raw.a4));
        aqua.add(new Animal(getString(R.string.sealion),R.mipmap.a5hd,R.raw.a5));
        aqua.add(new Animal(getString(R.string.whale),R.mipmap.a6hd,R.raw.a6));
        aqua.add(new Animal(getString(R.string.fish),R.mipmap.a7hd,R.raw.a7));
        aqua.add(new Animal(getString(R.string.turtle),R.mipmap.a8turtle,R.raw.a8,true,"a8.gif"));
        aqua.add(new Animal(getString(R.string.otter),R.mipmap.a9otter,R.raw.a9,true,"a9.gif"));
        aqua.add(new Animal(getString(R.string.lobster),R.mipmap.a10lobster,R.raw.a10));

        birds = new ArrayList<>();
        birds.add(new Animal(getString(R.string.goose),R.mipmap.b0hd,R.raw.b0,true,"b0.gif"));
        birds.add(new Animal(getString(R.string.duck),R.mipmap.b1hd,R.raw.b1));
        birds.add(new Animal(getString(R.string.crow),R.mipmap.b2hd,R.raw.b2));
        birds.add(new Animal(getString(R.string.seagull),R.mipmap.b3hd,R.raw.b3));
        birds.add(new Animal(getString(R.string.dove),R.mipmap.b4hd,R.raw.b4));
        birds.add(new Animal(getString(R.string.nightingale),R.mipmap.b5hd,R.raw.b5));
        birds.add(new Animal(getString(R.string.eagle),R.mipmap.b6hd,R.raw.b6,true,"b6.gif"));
        birds.add(new Animal(getString(R.string.hawk),R.mipmap.b7hd,R.raw.b7));
        birds.add(new Animal(getString(R.string.woodpecker),R.mipmap.b8hd,R.raw.b8,true,"b8.gif"));
        birds.add(new Animal(getString(R.string.parrot),R.mipmap.b9hd,R.raw.b9,true,"b9.gif"));
        birds.add(new Animal(getString(R.string.owl),R.mipmap.b10hd,R.raw.b10,true,"b10.gif"));
        birds.add(new Animal(getString(R.string.cuckoo),R.mipmap.b11hd,R.raw.b11));
        birds.add(new Animal(getString(R.string.pelican),R.mipmap.b12hd,R.raw.b12));
        birds.add(new Animal(getString(R.string.ostrich),R.mipmap.b13hd,R.raw.b13));
        birds.add(new Animal(getString(R.string.flamingo),R.mipmap.b14hd,R.raw.b14,true,"b14.gif"));
        birds.add(new Animal(getString(R.string.peacock),R.mipmap.b15hd,R.raw.b15));
        birds.add(new Animal(getString(R.string.catbird),R.mipmap.b16catbird,R.raw.b16));
        birds.add(new Animal(getString(R.string.tit),R.mipmap.b17tit,R.raw.b17));
        birds.add(new Animal(getString(R.string.toucan),R.mipmap.b18toucan,R.raw.b18));
        birds.add(new Animal(getString(R.string.robin),R.mipmap.b19robin,R.raw.b19));
        birds.add(new Animal(getString(R.string.blackgrouse),R.mipmap.b20blackgrouse,R.raw.b20));
        birds.add(new Animal(getString(R.string.hummingbird),R.mipmap.b21hummingbird,R.raw.b21));
        birds.add(new Animal(getString(R.string.bullfinch),R.mipmap.b23bullfinch,R.raw.b23));
        birds.add(new Animal(getString(R.string.stork),R.mipmap.b24stork,R.raw.b24));
        birds.add(new Animal(getString(R.string.heron),R.mipmap.b25heron,R.raw.b25));
        birds.add(new Animal(getString(R.string.canary),R.mipmap.b26canary,R.raw.b26));
        birds.add(new Animal(getString(R.string.magpie),R.mipmap.b27magpie,R.raw.b27));
        birds.add(new Animal(getString(R.string.bat),R.mipmap.b28bat,R.raw.b28));
        birds.add(new Animal(getString(R.string.jay),R.mipmap.b29jay,R.raw.b29));
        birds.add(new Animal(getString(R.string.starling),R.mipmap.b30starling,R.raw.b30));

        insects = new ArrayList<>();
        insects.add(new Animal(getString(R.string.bees),R.mipmap.i0hd,R.raw.i0));
        insects.add(new Animal(getString(R.string.flies),R.mipmap.i1hd,R.raw.i1));
        insects.add(new Animal(getString(R.string.mosquito),R.mipmap.i2hd,R.raw.i2,true,"i2.gif"));
        insects.add(new Animal(getString(R.string.grasshopper),R.mipmap.i3hd,R.raw.i3));
        insects.add(new Animal(getString(R.string.bumblebee),R.mipmap.i4hd,R.raw.i4));
        insects.add(new Animal(getString(R.string.cricket),R.mipmap.i5hd,R.raw.i5));
        insects.add(new Animal(getString(R.string.butterfly),R.mipmap.i6hd,R.raw.i6,true,"i6.gif"));
        insects.add(new Animal(getString(R.string.dragonfly),R.mipmap.i7hd,R.raw.i7));
        insects.add(new Animal(getString(R.string.ants),R.mipmap.i8hd,R.raw.i8));
        insects.add(new Animal(getString(R.string.mantis),R.mipmap.i9hd,R.raw.i9));
        insects.add(new Animal(getString(R.string.cicada), R.mipmap.i10hd,R.raw.i10));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // if (data == null) {return;}
       // String name = data.getStringExtra("name");
       // tvName.setText("Your name is " + name);
        mViewPager.setCurrentItem(3);
    }
}
