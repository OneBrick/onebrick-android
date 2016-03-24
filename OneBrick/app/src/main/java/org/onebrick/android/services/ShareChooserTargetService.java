package org.onebrick.android.services;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Build;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.M)
public class ShareChooserTargetService extends ChooserTargetService {

    @Override
    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName,
                                                   IntentFilter matchedFilter) {

//        ComponentName componentName = new ComponentName(getPackageName(),
//                EventDetailActivity.class.getCanonicalName());
        // The list of Direct Share items. The system will show the items the way they are sorted
        // in this list.
        ArrayList<ChooserTarget> targets = new ArrayList<>();
        return targets;
    }

}
