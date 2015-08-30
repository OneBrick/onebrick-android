package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.helpers.SocialShareEmail;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import butterknife.InjectView;


public class ContactsCard extends EventCard {
    @InjectView(R.id.btn_email_manager) Button btnEmailManager;
    @InjectView(R.id.btn_email_coordinator) Button btnEmailCoordinator;
    @InjectView(R.id.tv_contact_organizer) TextView tvContactOrganizer;

    public ContactsCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_contacts);

        if (TextUtils.isEmpty(mEvent.getManagerEmail()) && TextUtils.isEmpty(mEvent.getCoordinatorEmail())) {
            btnEmailManager.setVisibility(View.INVISIBLE);
            btnEmailCoordinator.setVisibility(View.INVISIBLE);
            tvContactOrganizer.setVisibility(View.INVISIBLE);
            return mView;
        }
        if (TextUtils.isEmpty(mEvent.getManagerEmail())){
            btnEmailManager.setVisibility(View.INVISIBLE);
        }else{
            btnEmailManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = mEvent.getManagerEmail();
                    if (Utils.isValidEmail(email)) {
                        SocialShareEmail.sendEmails(v, mEvent.getTitle(), mEvent.getEventId(), email);
                    }
                }
            });
        }
        if (TextUtils.isEmpty(mEvent.getCoordinatorEmail())){
            btnEmailCoordinator.setVisibility(View.INVISIBLE);
        }else{
            // email to coordinator
            btnEmailCoordinator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = mEvent.getCoordinatorEmail();
                    if (Utils.isValidEmail(email)) {
                        SocialShareEmail.sendEmails(v, mEvent.getTitle(), mEvent.getEventId(), email);
                    }
                }
            });
        }
        return mView;
    }
}
