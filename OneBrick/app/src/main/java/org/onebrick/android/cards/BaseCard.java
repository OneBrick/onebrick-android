package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseCard implements Card {
    protected Context mContext;
    protected View mView;

    protected BaseCard(Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public View getView() {
        return mView;
    }

    protected View initView(ViewGroup parent, int resId) {
        final LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(resId, parent, false);
        ButterKnife.inject(this, mView);
        return mView;
    }
}
