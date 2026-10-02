package com.example.pusheralert;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String PUSHER_KEY = "6360d9584aa0c6e5d240";
    private static final String PUSHER_CLUSTER = "ap2";
    private static final String CHANNEL_NAME = "chat-channel";
    private static final String EVENT_NAME = "new-text";
    private static final String NOTIFICATION_CHANNEL_ID = "pusher_alerts";
    private static final int REQUEST_NOTIFICATIONS = 1001;
    private Pusher pusher;
    private TextView statusText;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusText = findViewById(R.id.statusText);
        createNotificationChannel();
        requestNotificationPermission();
        startPusher();
    }

    private void startPusher() {
        PusherOptions options = new PusherOptions().setCluster(PUSHER_CLUSTER);
        pusher = new Pusher(PUSHER_KEY, options);
        Channel channel = pusher.subscribe(CHANNEL_NAME);
        channel.bind(EVENT_NAME, new SubscriptionEventListener() {
            @Override public void onEvent(String channelName, String eventName, String data) {
                runOnUiThread(() -> handleMessage(data));
            }
        });
        pusher.connect();
        statusText.setText("Pusher connected\nListening: " + CHANNEL_NAME + " / " + EVENT_NAME);
    }

    private void handleMessage(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String text1 = json.optString("text1", "");
            String text2 = json.optString("text2", "");
            String title = text2.isEmpty() ? "🔥 Pusher Alert" : "🔥 " + text2;
            String body = text1.isEmpty() ? "New alert received" : text1;
            statusText.setText("Last alert:\n" + title + "\n" + body);
            showNotification(title, body);
        } catch (Exception e) {
            statusText.setText("Invalid message: " + e.getMessage());
        }
    }

    private void showNotification(String title, String body) {
        if (Build.VERSION.SDK_INT >= 33 && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Trading Alerts", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Pusher alerts");
            channel.enableVibration(true);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS);
        }
    }

    @Override protected void onDestroy() {
        if (pusher != null) pusher.disconnect();
        super.onDestroy();
    }
}
