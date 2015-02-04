package org.onebrick.android.helpers;

import android.graphics.Typeface;

import org.onebrick.android.core.OneBrickApplication;

public class FontsHelper {
    private static final Typeface robotoThinItalic =
            Typeface.createFromAsset(
                    OneBrickApplication.getInstance().getAssets(),
                    "fonts/Roboto-ThinItalic.ttf"
            );
    private static final Typeface robotoRegular =
            Typeface.createFromAsset(
                    OneBrickApplication.getInstance().getAssets(),
                    "fonts/Roboto-Regular.ttf");

    public static Typeface getRobotoRegular() {
        return robotoRegular;
    }

}
