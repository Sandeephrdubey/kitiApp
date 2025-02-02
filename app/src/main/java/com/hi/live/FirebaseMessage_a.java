package com.hi.live;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hi.live.activity.ChatListActivityGOriginal_a;
import com.hi.live.activity.MainActivityG_a;
import com.hi.live.activity.WatchLiveActivity_a;
import com.hi.live.models.CoinHistoryRoot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class FirebaseMessage_a extends FirebaseMessagingService {
    private static final String TAG = "mmmmmm";
    Intent intent;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: "+token);
    }

    public FirebaseMessage_a() {
        super();
    }

    @Override
    public void handleIntent(Intent intent) {
//        super.handleIntent(intent);
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        try {
            if (intent.getExtras() != null) {
                RemoteMessage.Builder builder = new RemoteMessage.Builder("MessagingService");
                for (String key : intent.getExtras().keySet()) {
                    builder.addData(key, intent.getExtras().get(key).toString());
                }

                onMessageReceived(builder.build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_logo);

            Map<String, String> messageData = remoteMessage.getData();
            Log.d(TAG, "onMessageReceived: " + messageData);

            String type = messageData.get("type");
            String data = messageData.get("data");
            Log.d(TAG, "onMessageReceived: message type " + type);
            Log.d(TAG, "onMessageReceived: message data " + data);

            switch (type) {
                case "Live":
                    intent = new Intent(this, WatchLiveActivity_a.class);
                    intent.putExtra("model", data);
                    break;
                case "Chat":
                    intent = new Intent(this, ChatListActivityGOriginal_a.class);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(data);
                        intent.putExtra("name", jsonObject.getString("name"));
                        intent.putExtra("image", jsonObject.getString("image"));
                        intent.putExtra("isFake", false);
                        intent.putExtra("hostid", jsonObject.getString("hostId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case "CoinPurchased":
                    intent = new Intent(this, CoinHistoryRoot.class);
                    break;
                default:
                    intent = new Intent(this, MainActivityG_a.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }



            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            String channelId = "01";
            if (remoteMessage.getNotification().getImageUrl() != null) {
                String imageUri = remoteMessage.getNotification().getImageUrl().toString();

                Bitmap bitmap = getBitmapfromUrl(imageUri);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(icon)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap))
                        .setColor(getResources().getColor(R.color.purplepink))
                        .setLights(Color.RED, 1000, 300)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setSmallIcon(R.drawable.a_notilogo);


                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    String name = "Channel_001";
                    String description = "Channel Description";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                    channel.setDescription(description);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());


            } else {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(icon)
                        .setColor(getResources().getColor(R.color.purplepink))
                        .setLights(Color.RED, 1000, 300)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setSmallIcon(R.drawable.a_notilogo);


                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    String name = "Channel_001";
                    String description = "Channel Description";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                    channel.setDescription(description);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());
            }


        }

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Log.d(TAG, "getBitmapfromUrl: " + imageUrl);
            return bitmap;

        } catch (Exception e) {
            Log.d(TAG, "getBitmapfromUrl: " + e);
            e.printStackTrace();
            return null;

        }
    }
}
