package com.tech.micasa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Ravindra Birla on 17,March,2021
 */
public class MicasaPushNotifications extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String notificationType, title, meetupId, messageId;
    private String message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "payload:" + remoteMessage.getData());

            Map<String,String> map = remoteMessage.getData();

            try {
                sendNotification("","",map);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "onMessageReceived for FCM");

        if (remoteMessage.getNotification() != null) {
          Log.d(TAG, "c: " + remoteMessage.getData());
            try {
                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param message
     * @param title
     */

    private void sendNotification(String message, String title,Map<String,String> map) throws JSONException {

            JSONObject jsonObject = null;
            jsonObject = new JSONObject(map);
            String key1= jsonObject.getString("key1");
            String key = jsonObject.getString("key");
            Intent intent = new Intent();

        if(key1.equalsIgnoreCase("Chat request"))
          {
              String result = jsonObject.getString("result");
              intent = new Intent(this, HomeActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.putExtra("notification","notification");
              intent.putExtra("key","notification");
              intent.putExtra("chat","test");
              intent.putExtra("GoToTab3","1");
          } else  if(key1.equalsIgnoreCase("Offer request"))
            {
            String result = jsonObject.getString("result");
            intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("notification","notification");
            intent.putExtra("key","notification");
            intent.putExtra("chat","test");
            intent.putExtra("GoToTab3","1");
        } else if(key1.equalsIgnoreCase("Accepted"))
           {
              intent = new Intent(this, HomeActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.putExtra("chat","test");
              intent.putExtra("notification","notification");
               intent.putExtra("key","notification");
               intent.putExtra("GoToTab3","3");
          }else if(key1.equalsIgnoreCase("new chat"))
          {
              String result =    jsonObject.getString("result");
              String senderId =   jsonObject.getString("sender_id");
              String itemId =   jsonObject.getString("item_id");
              intent = new Intent(this, HomeActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.putExtra("chat","chat");
              intent.putExtra("key","chat");
              intent.putExtra("notification","test");
              intent.putExtra("seller_id",senderId);
              intent.putExtra("item_id",itemId);
          }else if(key1.equalsIgnoreCase("Offer"))
          {
              intent = new Intent(this, HomeActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.putExtra("chat","test");
              intent.putExtra("key","notification");
              intent.putExtra("notification","notification");
              intent.putExtra("GoToTab3","1");
          }else if(key1.equalsIgnoreCase("Rejected"))
          {
              String result = jsonObject.getString("result");
          }

       /* if (jsonObject.has("data")) {
            JSONObject object = new JSONObject(map);
            Log.i("onMessageReceived: ", object.toString());
            title = object.optString("title");
            notificationType = object.optString("type");
            message = object.optString("message");
        }*/

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.mm)
                        .setContentTitle(title)
                        .setContentText(key)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
