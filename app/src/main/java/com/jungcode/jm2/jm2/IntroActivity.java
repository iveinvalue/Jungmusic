package com.jungcode.jm2.jm2;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by User on 2017-07-17.
 */

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        //addSlide(firstFragment);
        //addSlide(secondFragment);
        //addSlide(thirdFragment);
        //addSlide(fourthFragment);

        //addSlide(AppIntro2Fragment.newInstance("안녕", "친구들", R.mipmap.screen1, Color.parseColor("#333535")));
        //addSlide(AppIntro2Fragment.newInstance("안녕", "친구들", R.mipmap.screen2, Color.parseColor("#333535")));
        //addSlide(AppIntro2Fragment.newInstance("안녕", "친구들", R.mipmap.screen3, Color.parseColor("#333535")));

        setBarColor(Color.parseColor("#00000000"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        showSkipButton(false);
        setProgressButtonEnabled(true);

        setVibrate(false);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}