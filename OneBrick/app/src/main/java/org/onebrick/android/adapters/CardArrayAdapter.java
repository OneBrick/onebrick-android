package org.onebrick.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.onebrick.android.cards.Card;

public class CardArrayAdapter extends ArrayAdapter<Card> {

    public CardArrayAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Card card = getItem(position);

        View view = card.getView();
        if (view == null) {
            view = card.initView(parent);
        }
        return view;
    }
}
