package uiutils;

import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.medapptest.R;
import com.example.medapptest.common.Constants;
import com.example.medapptest.model.NotificationActionData;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private String title;
    private String content;
    private int icon = R.drawable.ic_launcher_foreground;
    private List<NotificationActionData> actionDataList =
            new ArrayList<NotificationActionData>();
    private Context context;
    private boolean autoCancel = true;

    public NotificationManager(String title, String content, Context context) {
        this.content = content;
        this.title = title;
        this.context = context;
    }

    public void addAction (NotificationActionData actionData) {
        this.actionDataList.add(actionData);
    }
    public void sendNotification(){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setAutoCancel(autoCancel)
                .setContentText(content)
                .setWhen(System.currentTimeMillis());
        if(actionDataList.size() > 0) {
            for(NotificationActionData actionData:actionDataList) {
                notificationBuilder.addAction(actionData.getIcon(),
                        actionData.getName(),actionData.getPendingIntent());
            }
        }
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());

    }
}
