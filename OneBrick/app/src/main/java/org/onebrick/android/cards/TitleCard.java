package org.onebrick.android.cards;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.models.Event;

import java.util.Calendar;

import butterknife.InjectView;

public class TitleCard extends EventCard {

    @InjectView(R.id.tv_event_title) TextView mTitleTextView;
    @InjectView(R.id.tv_event_time) TextView mDateTimeTextView;
    @InjectView(R.id.tv_event_address) TextView mLocationTextView;
    @InjectView(R.id.iv_calendar) ImageView mCalendarImageView;

    public TitleCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_title);

        mTitleTextView.setText(mEvent.getTitle());
        mDateTimeTextView.setText(DateTimeFormatter.getInstance().getFormattedEventDate(mEvent.getEventStartDate())
                + " - "
                + DateTimeFormatter.getInstance().getFormattedTimeEndOnly(mEvent.getEventStartDate(),
                mEvent.getEventEndDate()));

        mLocationTextView.setText(mEvent.getEventAddress());

        mCalendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DateTimeFormatter dtf = DateTimeFormatter.getInstance();
                final Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(dtf.getDateFromString(mEvent.getEventStartDate()));
                final Calendar endTime = Calendar.getInstance();
                beginTime.setTime(dtf.getDateFromString(mEvent.getEventEndDate()));
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, mEvent.getTitle())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, mEvent.getEventAddress())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                mContext.startActivity(intent);
                mCalendarImageView.setImageResource(R.drawable.ic_in_calendar);
            }
        });

        return mView;
    }
}
