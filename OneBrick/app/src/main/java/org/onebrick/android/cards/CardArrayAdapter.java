package org.onebrick.android.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CardArrayAdapter extends ArrayAdapter<Card> {

    public CardArrayAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Card card = getItem(position);

        View view = card.getView(parent);
        if (view == null) {
            view = card.initView(parent);
        }
        return view;
    }
}
