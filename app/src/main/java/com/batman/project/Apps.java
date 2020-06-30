package com.batman.project;

import android.app.Application;
import android.graphics.Typeface;

public class Apps extends Application {
    public static Typeface font;


    @Override
    public void onCreate() {
        super.onCreate();
        font = Typeface.createFromAsset(getAssets(), "impact.ttf");

    }

}
