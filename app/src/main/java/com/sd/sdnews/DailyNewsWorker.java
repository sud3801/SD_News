package com.sd.sdnews;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DailyNewsWorker extends Worker {

    private static final String CHANNEL_ID   = "sd_news_daily";
    private static final String CHANNEL_NAME = "Daily News";
    private static final int    NOTIF_ID     = 1001;

    // Duolingo-style rotating messages
    private static final String[][] MESSAGES = {
            {"🗞️ Good Morning!",
                    "Your daily news is ready. Stay informed in 5 minutes!"},
            {"📰 Don't Miss Out!",
                    "Top stories from SD News are waiting for you."},
            {"☕ Morning Briefing",
                    "Start your day right — catch up on today's headlines."},
            {"🌍 World is Moving!",
                    "Are you keeping up? Open SD News for today's top stories."},
            {"📢 News Alert",
                    "Your personalised news feed has been updated. Tap to read!"},
            {"🔔 Daily Reminder",
                    "You haven't read today's news yet. Just 5 minutes!"},
            {"💡 Stay Sharp!",
                    "Informed people make better decisions. Read today's news."},
            {"🌅 Rise & Read",
                    "A new day, new stories. See what's happening in the world."},
            {"📖 Knowledge is Power",
                    "Catch up on the latest headlines before your day gets busy."},
            {"⚡ Quick Catch-Up",
                    "5 minutes of news keeps you ahead of the curve. Let's go!"}
    };

    public DailyNewsWorker(@NonNull Context context,
                           @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        showDailyNotification();
        return Result.success();
    }

    private void showDailyNotification() {
        Context context = getApplicationContext();

        // Pick message based on day of year so it rotates
        int dayOfYear = java.util.Calendar.getInstance()
                .get(java.util.Calendar.DAY_OF_YEAR);
        String[] msg = MESSAGES[dayOfYear % MESSAGES.length];
        String title = msg[0];
        String body  = msg[1];

        // Tapping notification opens MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create notification channel (required Android 8+)
        NotificationManager manager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Daily news reminders from SD News");
            manager.createNotificationChannel(channel);
        }

        // Build and show the notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_newspaper)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);

        manager.notify(NOTIF_ID, builder.build());
    }
}