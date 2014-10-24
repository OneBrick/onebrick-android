package org.onebrick.android.helpers;

import android.graphics.Typeface;

import org.onebrick.android.OneBrickApplication;

/**
 * Created by AshwinGV on 10/24/14.
 */
public class FontsHelper {
    private static final Typeface robotoThinItalic =
            Typeface.createFromAsset(
                    OneBrickApplication.getContext().getAssets(),
                    "fonts/Roboto-ThinItalic.ttf"
            );
    private static final Typeface robotoRegular =
            Typeface.createFromAsset(
                    OneBrickApplication.getContext().getApplicationContext().getAssets(),
                    "fonts/Roboto-Regular.ttf");

    public static Typeface getRobotoRegular() {
        return robotoRegular;
    }

    public static Typeface getRobotoThinItalic() {
        return robotoThinItalic;
    }
}
