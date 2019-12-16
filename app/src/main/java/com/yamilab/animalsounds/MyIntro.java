package com.yamilab.animalsounds;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

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
        // Just set a title, description, background and image. AppIntro will do the rest addSlide(AppIntroFragment.newInstance(getString(R.string.click),
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(getString(R.string.tabs));
        sliderPage.setDescription(getString(R.string.category));
        sliderPage.setImageDrawable(R.mipmap.intro0);
        sliderPage.setBgColor(Color.parseColor("#009688"));

        addSlide(AppIntroFragment.newInstance(sliderPage));
        /*
        addSlide(AppIntroFragment.newInstance(getString(R.string.tabs),
                getString(R.string.category),
                R.mipmap.intro0, Color.parseColor("#009688")));
        */


        sliderPage.setTitle(getString(R.string.click));
        sliderPage.setDescription(getString(R.string.clickdesc));
        sliderPage.setImageDrawable(R.mipmap.intro1);
        sliderPage.setBgColor(Color.parseColor("#009688"));
        addSlide(AppIntroFragment.newInstance(sliderPage));


        sliderPage.setTitle(getString(R.string.clicktext));
        sliderPage.setDescription(getString(R.string.clicktextdesc));
        sliderPage.setImageDrawable(R.mipmap.intro2);
        sliderPage.setBgColor(Color.parseColor("#009688"));
        addSlide(AppIntroFragment.newInstance(sliderPage));


        sliderPage.setTitle(getString(R.string.game));
        sliderPage.setDescription(getString(R.string.gametext));
        sliderPage.setImageDrawable(R.mipmap.intro3);
        sliderPage.setBgColor(Color.parseColor("#009688"));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        sliderPage = null;

        /*

        addSlide(AppIntroFragment.newInstance(getString(R.string.click),
                getString(R.string.clickdesc),
                R.mipmap.intro1, Color.parseColor("#009688")));

        /*
        addSlide(AppIntroFragment.newInstance(getString(R.string.longclick),
                getString(R.string.longclickdesc),
                R.mipmap.intro2, Color.parseColor("#009688")));
         */
        /*
        addSlide(AppIntroFragment.newInstance(getString(R.string.clicktext),
                getString(R.string.clicktextdesc),
                R.mipmap.intro2, Color.parseColor("#009688")));

        addSlide(AppIntroFragment.newInstance(getString(R.string.game),
                getString(R.string.gametext),
                R.mipmap.intro3, Color.parseColor("#009688")));
            */

        // OPTIONAL METHODS

        // SHOW or HIDE the statusbar
        showSkipButton(true);
        setProgressButtonEnabled(true);

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
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }


    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }



}