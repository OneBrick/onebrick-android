package org.onebrick.android.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by AshwinGV on 10/17/14.
 */

@Table(name="RsvpInfo")
public class RsvpInfo extends Model {

    @Column(name="Event")
    Event event;

    private RsvpInfo() {
        super();
    }


    /*
    This method is called to save rsvp info into the table for a user.
     */
    public RsvpInfo Rsvp2Event(Event e) {
        RsvpInfo rsvpInfo = new RsvpInfo();
        rsvpInfo.event = e;
        rsvpInfo.save();
        return rsvpInfo;
    }

    public static List<RsvpInfo> getRsvpInfo() {
        From sql = new Select()
                .from(RsvpInfo.class);
        Log.d("RSVP SQL", sql.toSql());
        return sql.execute();
    }


}
