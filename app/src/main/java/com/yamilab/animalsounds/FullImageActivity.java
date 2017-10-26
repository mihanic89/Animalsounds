package com.yamilab.animalsounds;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Created by Misha on 01.04.2017.
 */

public  class FullImageActivity extends AppCompatActivity {
    public ImageView imgFullImage;
    ImageButton btnBack;
    TextView name;
    private AdView mAdView;
    Context context;
    Integer sound;
    Integer image;

    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2888343178529026~2046736590");
        //Bundle params = new Bundle(); params.putString(FirebaseAnalytics.PARAM.ITEM_CATEGORY, "screen");
        //params.putString(FirebaseAnalytics.PARAM.ITEM_NAME, "screen name");
        //mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);

        ///findViewBYID
        imgFullImage = findViewById(R.id.fullImage);
        btnBack = findViewById(R.id.action_back);
        name = findViewById(R.id.textName);
        Bundle bundle = getIntent().getExtras();
        image = bundle.getInt("image");
        sound = bundle.getInt("sound");

        String screenName = image + "-" + sound;

        // [START set_current_screen]
        mFirebaseAnalytics.setCurrentScreen(this, screenName, null /* class override */);
        // [END set_current_screen]


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.myscale);
        animation.setRepeatCount(Animation.INFINITE);
        imgFullImage.startAnimation(animation);

        //imgFullImage.setImageResource(image);

        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .override(imgFullImage.getWidth(), imgFullImage.getHeight())
                ;

        Glide.with(this)
                .load(image)
                .apply(myOptions)
                .into(imgFullImage);

        context= imgFullImage.getContext();
        imgFullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SoundPlay.playSP(context, sound);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SoundPlay.clearSP(context);
                finish();
            }
        });

        //Bundle extras = new Bundle();
       // extras.putBoolean("is_designed_for_families", true);

        name.setText(bundle.getString("name"));

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addNetworkExtrasBundle(AdMobAdapter.class, extras)
                //.tagForChildDirectedTreatment(true)
                .build();
        mAdView.loadAd(adRequest);
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
        mAdView.destroy();
        imgFullImage.clearAnimation();
        SoundPlay.clearSP(context);
        super.onPause();

    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        SoundPlay.clearSP(context);
        mAdView.destroy();
        imgFullImage.clearAnimation();
        super.onDestroy();
    }
}
