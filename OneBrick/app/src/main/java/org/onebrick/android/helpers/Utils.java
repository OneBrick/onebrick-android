package org.onebrick.android.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Utils {
    private static final String TAG = "Utils";

    /**
     * remove img tags from html inside event description
     * Please, don't expect this regular expression always works.
     *
     * @param input
     * @return
     */
    public static String removeImgTagsFromHTML(@NonNull String input){
        if (!input.isEmpty()){
            input = input.replaceAll("(<img\\b[^>]*\\bsrc\\s*=\\s*)([\"\'])((?:(?!\\2)[^>])*)\\2(\\s*[^>]*>)", "");
        }
        return input;
    }

    public static String removeHTagsFromHTML(@NonNull String input){
        if (!input.isEmpty()){
            input = input.replaceAll("<h3>|</h3>", "");
        }
        return input;
    }

    public static boolean isValidEmail(@Nullable String email){
        return !(email == null || "null".equals(email.trim().toLowerCase()));
    }

    public static String replaceWhiteSpace(@NonNull String input){
        if (!input.isEmpty()){
            input = input.replaceAll("\\s+", "+");
        }
        return input;
    }

}
