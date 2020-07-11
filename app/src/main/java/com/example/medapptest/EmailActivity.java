package com.example.medapptest;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.medapptest.common.Constants;
import com.example.medapptest.common.MedAppUserPreference;

public class EmailActivity extends Activity {
    // region Android life cycle
    private Button mailNowBtn;
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        mailNowBtn = (Button) findViewById(R.id.mailNowBtn);
        mailNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMailNowBtnClicked();
            }
        });
        if(getIntent().hasExtra(Constants.KEY_EMAILINTENT_PICTURE)) {
            imgPath = getIntent().getStringExtra(Constants.KEY_EMAILINTENT_PICTURE);
        }
    }
    //endregion


    //region button clickevents
    private void onMailNowBtnClicked() {
        String[] TO = {"nivedita.neethirajan@gmail.com"};
        //Fetch name from user preference
        String name = MedAppUserPreference.getUserName();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("application/image");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello World");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "\n Greetings\n" + name);
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgPath));
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail.."));
            finish();
            // Build notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                    getApplicationContext(),Constants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Email Notification")
                    .setAutoCancel(true)
                    .setContentText("Sending Picture")
                    .setWhen(System.currentTimeMillis());
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notificationBuilder.build());
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion
}
