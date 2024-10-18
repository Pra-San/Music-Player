package com.example.musicplayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MusicWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);

        setUpButtonClickListener(context, views, R.id.button_play_pause, "PLAY_PAUSE");
        setUpButtonClickListener(context, views, R.id.button_next, "NEXT");
        setUpButtonClickListener(context, views, R.id.button_previous, "PREVIOUS");
        setUpButtonClickListener(context, views, R.id.button_forward, "FORWARD");
        setUpButtonClickListener(context, views, R.id.button_rewind, "REWIND");

        views.setTextViewText(R.id.text_track_title, getCurrentTrackTitle());
        views.setTextViewText(R.id.text_artist, getCurrentArtist());

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setUpButtonClickListener(Context context, RemoteViews views, int buttonId, String action) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(buttonId, pendingIntent);
    }

    private static String getCurrentTrackTitle() {

        return "Current Track Title";
    }

    private static String getCurrentArtist() {

        return "Current Artist";
    }
}