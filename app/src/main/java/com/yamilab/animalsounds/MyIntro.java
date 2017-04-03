package com.yamilab.animalsounds;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MyIntro extends AppIntro2 {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(first_fragment);
        //addSlide(second_fragment);
        //addSlide(third_fragment);
        //addSlide(fourth_fragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance(getString(R.string.click),
                getString(R.string.clickdesc),
                R.mipmap.intro1, Color.parseColor("#009688")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.longclick),
                getString(R.string.longclickdesc),
                R.mipmap.intro2, Color.parseColor("#009688")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.bigimage),
                getString(R.string.bigimagedesc),
                R.mipmap.intro3, Color.parseColor("#009688")));
        // OPTIONAL METHODS

        // SHOW or HIDE the statusbar
        showStatusBar(true);

        // Edit the color of the nav bar on Lollipop+ devices
        setNavBarColor(R.color.colorPrimary);//Color.parseColor("#3F51B5"));

        // Turn vibration on and set intensity
        // NOTE: you will need to ask VIBRATE permission in Manifest if you haven't already
        //setVibrate(true);
        //setVibrateIntensity(30);

        // Animations -- use only one of the below. Using both could cause errors.
        setFadeAnimation(); // OR
        //setZoomAnimation(); // OR
        //setFlowAnimation(); // OR
       // setSlideOverAnimation(); // OR
       // setDepthAnimation(); // OR
        //setCustomTransformer(yourCustomTransformer);


    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }
}