package com.jungcode.jm2.jm2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.IBinder;

import android.widget.RemoteViews;

import java.io.IOException;

public class Backmusic extends Service {
    public static String MESSAGE_KEY;
    static MediaPlayer mediaPlayer;
    public static RemoteViews remoteView;
    static NotificationManager notificationManager;
    static Notification notification;
    PendingIntent mPendingIntent2;
    int is_ear = 0;

    public Backmusic() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        boolean message = intent.getExtras().getBoolean(Backmusic.MESSAGE_KEY);
        SharedPreferences test = this.getSharedPreferences("mPref", MODE_PRIVATE);
        if(message){
            try {
                if(mediaPlayer!=null) {
                    try {
                        mediaPlayer.release();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                mediaPlayer = new MediaPlayer();

                mediaPlayer.setDataSource(test.getString("song", ""));
                mediaPlayer.prepare();
                mediaPlayer.start();
                if(test.getInt("is_started", 0) == 1)
                    mediaPlayer.pause();

                SharedPreferences.Editor mPrefEdit = test.edit();
                mPrefEdit.putInt("is_started", 0);
                mPrefEdit.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                if(mediaPlayer!=null) {
                    try {
                        mediaPlayer.release();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                mediaPlayer = new MediaPlayer();

                mediaPlayer.setDataSource(test.getString("song", ""));
                mediaPlayer.prepare();
                //mediaPlayer.start();
                //mediaPlayer.pause();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Intent mMainIntent = new Intent(this,MainActivity.class);
        //mMainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //PendingIntent mPendingIntent = PendingIntent.getActivity(this,1,mMainIntent,PendingIntent.FLAG_ONE_SHOT);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent2 = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //notification = new Notification(R.drawable.ic_stat_headset, null, System.currentTimeMillis());

        notification = new Notification.Builder(this)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_stat_headset)
                .build();

        remoteView = new RemoteViews(getPackageName(), R.layout.notificationview);

        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        if(!test.getString("song", "none").contains("none")){
            try{
                metaRetriver.setDataSource(test.getString("song", ""));//태그경로
                byte[] art = metaRetriver.getEmbeddedPicture();//앨범아트
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);

                remoteView.setImageViewBitmap(R.id.art_small,songImage);
                remoteView.setTextViewText(R.id.tittle_small, metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                remoteView.setTextViewText(R.id.artist_small, metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) + " - " + metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            }catch (Exception e){
                remoteView.setTextViewText(R.id.tittle_small, "타이틀");
                remoteView.setTextViewText(R.id.artist_small, "아티스트");
            }
        }else{
            remoteView.setTextViewText(R.id.tittle_small, "타이틀");
            remoteView.setTextViewText(R.id.artist_small, "아티스트");
        }

        remoteView.setTextViewText(R.id.next, ">|");
        remoteView.setTextViewText(R.id.previous, "|<");
        if(mediaPlayer.isPlaying()){
            remoteView.setTextViewText(R.id.play, "정지");
        }else{
            remoteView.setTextViewText(R.id.play, "재생");
        }

        notification.contentView = remoteView;
        notification.contentIntent = mPendingIntent2;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        setListeners(remoteView);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        EarphoneMic headsetReceiver = new EarphoneMic();
        registerReceiver(headsetReceiver, intentFilter);

        return START_NOT_STICKY;
    }

    private class EarphoneMic extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (0 == intent.getIntExtra("state", 0)) {
                    if(is_ear == 1){
                        mediaPlayer.pause();
                        remoteView.setTextViewText(R.id.play, "재생");
                        is_ear = 0;
                    }

                } else if (1 == intent.getIntExtra("state", 0)) {
                    is_ear =1;
                    /*
                    mediaPlayer.start();
                    remoteView.setTextViewText(R.id.play, "정지");*/

                }
                notification.contentView = remoteView;
                notificationManager.notify(1, notification);
            }
        }

    }

    public void setListeners(RemoteViews view){
        Intent switchIntent = new Intent("com.jungcode.jm2.jm2.ACTION_STOP_PLAY");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 1, switchIntent, 0);
        remoteView.setOnClickPendingIntent(R.id.play, pendingSwitchIntent);
        notificationManager.notify(1, notification);

        Intent goto_ = new Intent("com.jungcode.jm2.jm2.ACTION_GOTO");
        PendingIntent pendinggoto_ = PendingIntent.getBroadcast(this, 1, goto_, 0);
        remoteView.setOnClickPendingIntent(R.id.gotoo, pendinggoto_);
        notificationManager.notify(1, notification);

        Intent next = new Intent("com.jungcode.jm2.jm2.ACTION_NEXT");
        PendingIntent pendingnext = PendingIntent.getBroadcast(this, 1, next, 0);
        remoteView.setOnClickPendingIntent(R.id.next, pendingnext);
        notificationManager.notify(1, notification);

        Intent pre = new Intent("com.jungcode.jm2.jm2.ACTION_PRE");
        PendingIntent pendingpre = PendingIntent.getBroadcast(this, 1, pre, 0);
        remoteView.setOnClickPendingIntent(R.id.previous, pendingpre);
        notificationManager.notify(1, notification);

        Intent exit = new Intent("com.jungcode.jm2.jm2.ACTION_EXIT");
        PendingIntent pendingexit = PendingIntent.getBroadcast(this, 1, exit, 0);
        remoteView.setOnClickPendingIntent(R.id.exit, pendingexit);
        notificationManager.notify(1, notification);
    }

    //private int getNotificationIcon() { boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP); return useWhiteIcon ? R.drawable.icon_silhouette : R.drawable.ic_launcher; }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //mediaPlayer.stop();
    }

}
