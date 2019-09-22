package com.kinderlicht.ui;

import android.app.Activity;
import android.content.Intent;

public class Util {

    private static int sTheme;

    public final static int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;

    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            default:
            case THEME_LIGHT:
                activity.setTheme(R.style.AppTheme_NoActionBar);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.AppThemeDark);
                break;
        }
    }
}
