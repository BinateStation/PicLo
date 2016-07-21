package rkr.binatestation.piclo.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.activities.SplashScreen;

/**
 * Created by RKR on 18-01-2016.
 * PicloGcmListenerService.
 */
public class PicloGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = PicloGcmListenerService.class.getSimpleName();

    /**
     * Called when message is received.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Log.d(TAG, "From: " + from);
        sendNotification(message.getTtl(), message.getNotification().getBody());
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM messageId.
     *
     * @param messageId GCM messageId received.
     * @param body      the body of msg.
     */
    private void sendNotification(int messageId, String body) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_piclo_24dp)
                .setContentTitle("New Piclo added")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(messageId, notificationBuilder.build());
    }
}
