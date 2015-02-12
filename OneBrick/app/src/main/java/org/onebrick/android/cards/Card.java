package org.onebrick.android.cards;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

public interface Card {
    @NonNull
    View initView(@NonNull ViewGroup parent);

    @Nullable
    View getView();
}
