package com.yamilab.animalsounds;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.github.appintro.model.SliderPage;

public class MyIntro extends AppIntro {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(getString(R.string.tabs));
        sliderPage.setDescription(getString(R.string.category));
        sliderPage.setImageDrawable(R.mipmap.intro0);
        sliderPage.setBackgroundColor(Color.parseColor("#009688"));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        sliderPage.setTitle(getString(R.string.click));
        sliderPage.setDescription(getString(R.string.clickdesc));
        sliderPage.setImageDrawable(R.mipmap.intro1);
        sliderPage.setBackgroundColor(Color.parseColor("#009688"));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        sliderPage.setTitle(getString(R.string.clicktext));
        sliderPage.setDescription(getString(R.string.clicktextdesc));
        sliderPage.setImageDrawable(R.mipmap.intro2);
        sliderPage.setBackgroundColor(Color.parseColor("#009688"));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        sliderPage.setTitle(getString(R.string.game));
        sliderPage.setDescription(getString(R.string.gametext));
        sliderPage.setImageDrawable(R.mipmap.intro3);
        sliderPage.setBackgroundColor(Color.parseColor("#009688"));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        setSkipButtonEnabled(true);
        setIndicatorColor(Color.parseColor("#FFFFFF"), Color.parseColor("#80FFFFFF"));
        setNavBarColor(R.color.colorPrimary);

        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
