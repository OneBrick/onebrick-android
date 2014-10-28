package org.onebrick.android.helpers;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ChapterBannerMapper {

    private Map<Integer, String> bannerMap = new HashMap<Integer, String>();
    private int chapterId;
    private String banner;

    public ChapterBannerMapper(){
        bannerMap.put(101, "onebrick_sf_banner"); // san francisco
        bannerMap.put(102, "ny_banner"); // new york
        bannerMap.put(103, "chicago_banner");
        bannerMap.put(104, "dc_banner"); // dc
        bannerMap.put(105, "minneapolis_st_paul_banner"); // minneapolis/st. paul
        bannerMap.put(107, "seattle_banner"); // seattle
        bannerMap.put(109, ""); // orlando
        bannerMap.put(111, "silicon_valley_banner"); // silicon valley
        bannerMap.put(112, "la_banner"); // los angeles
        bannerMap.put(114, "detroit_banner"); // detroit
        bannerMap.put(115, "boston_banner"); // boston
        bannerMap.put(116, "philly_banner"); // philadelphia
    }

    public String getBanner(int chapterId){
        if (bannerMap.containsKey(chapterId)){
            Log.i("image banner: ", bannerMap.get(chapterId));
            return bannerMap.get(chapterId);
        }
        // return default san francisco banner
        return bannerMap.get(101);
    }
}
