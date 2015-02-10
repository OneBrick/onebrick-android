package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.onebrick.android.R;
import org.onebrick.android.helpers.SocialShareEmail;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import butterknife.InjectView;


public class ContactsCard extends EventCard {
    @InjectView(R.id.btn_email_manager) Button btnEmailManager;
    @InjectView(R.id.btn_email_coordinator) Button btnEmailCoordinator;

    public ContactsCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_contacts);

        // email to manager
        btnEmailManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEvent.getManagerEmail();
                if (Utils.isValidEmail(email)) {
                    SocialShareEmail.sendEmails(v, mEvent.getTitle(), mEvent.getEventId(), email);
                }
            }
        });

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

        return mView;
    }
}
