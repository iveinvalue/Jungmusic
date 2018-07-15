package com.jungcode.jm2.jm2;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import es.dmoral.toasty.Toasty;

import static com.jungcode.jm2.jm2.Backmusic.mediaPlayer;
import static com.jungcode.jm2.jm2.Backmusic.notification;
import static com.jungcode.jm2.jm2.Backmusic.notificationManager;
import static com.jungcode.jm2.jm2.Backmusic.remoteView;
import static com.jungcode.jm2.jm2.intro.IntroAct;
import static com.jungcode.jm2.jm2.localplayer_frag.count;
import static com.jungcode.jm2.jm2.localplayer_frag.suffcheck;

public class NotificationReturnSlot extends BroadcastReceiver {
    SharedPreferences.Editor mPrefEdit;
    SharedPreferences mPref;
    String fileName,cate;
    int do_;
    Context context_;
    @Override
    public void onReceive(Context context, Intent intent) {
        context_ = context;
        String action = intent.getAction();
        if(action.equalsIgnoreCase("com.jungcode.jm2.jm2.ACTION_STOP_PLAY")){
            Log.i("NotificationReturnSlot", "ACTION_STOP_PLAY");
            try{
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    remoteView.setTextViewText(R.id.play, "재생");
                }else{
                    mediaPlayer.start();
                    remoteView.setTextViewText(R.id.play, "정지");
                }
                notification.contentView = remoteView;
                notificationManager.notify(1, notification);
            }catch (Exception e){
            }
        }else if(action.equalsIgnoreCase("com.jungcode.jm2.jm2.ACTION_EXIT")){
            Log.i("NotificationReturnSlot", "ACTION_STOP_PLAY");
            NotificationManager notifManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancelAll();
            mediaPlayer.pause();
            IntroAct.finish();
            MainActivity close = (MainActivity)MainActivity.main;
            close.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }else if(action.equalsIgnoreCase("com.jungcode.jm2.jm2.ACTION_GOTO")){
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent2);
        }else if(action.equalsIgnoreCase("com.jungcode.jm2.jm2.ACTION_NEXT") || action.equalsIgnoreCase("com.jungcode.jm2.jm2.ACTION_PRE")){
            try{
                if(! MainActivity.is_downloading){
                    do_ = count;
                    if(action.equalsIgnoreCase("com.jungcode.jm2.jm2.ACTION_NEXT")){
                        if(!(do_ >= 100)) {
                            do_++;
                            if(suffcheck == true){
                                Random rand = new Random();
                                int n = rand.nextInt(100);
                                do_ = n;
                            }
                        }else{
                            do_ = 1;
                        }
                    }else{
                        if(!(do_ <= 1)) {
                            do_--;
                            if(suffcheck == true){
                                Random rand = new Random();
                                int n = rand.nextInt(100);
                                do_ = n;
                            }
                        }
                        else{
                        }
                    }


                    mPref = context.getSharedPreferences("mPref", 0);
                    mPrefEdit = mPref.edit();

                    cate = mPref.getString("cate", "");
                    String  fileURL= "" , resultt="";
                    String parse2 = "",xgnm="",nameee="",artisttt="";
                    try{
                        if(cate.contains("genie")){
                            parse2 = mPref.getString("chartdata", "");
                            String[] data = parse2.split("SONG_NAME\":\"");
                            String[] data2 = data[do_].split("\",");
                            String[] data33 = parse2.split("SONG_ID\":\"");
                            String[] data44 = data33[do_].split("\"");
                            String[] data5 = parse2.split("ARTIST_NAME\":\"");
                            String[] data6 = data5[do_].split("\",");
                            xgnm = data44[0];
                            nameee = data2[0];
                            artisttt = data6[0];
                        }else if(cate.contains("bill")){
                            parse2 = mPref.getString("chartdata2", "");

                            String[] data = parse2.split("<div class=\"song\">");
                            String[] data2 = data[do_].split("<td class=\"action\">");
                            String[] title2 = data2[0].split("a target=\"_blank\" href=\"/song/");
                            String[] title3 = title2[1].split("\">");
                            String[] title = title3[1].split("</a>");

                            String[] data5 = data2[0].split("<a class=\"artist\" ");
                            String[] artist = data5[1].split("</div>");
                            String[] real_a1 = artist[0].split("\">");
                            String[] real_a2 = real_a1[1].split("</a>");
                            try{
                                real_a2 = real_a2[0].split("\\(");
                            }catch(Exception e){

                            }

                            nameee = title[0];
                            artisttt = real_a2[0];
                        }else{
                            parse2 = mPref.getString("chartdata3", "");
                            String[] data = parse2.split("SONG_NAME\":\"");
                            String[] data2 = data[do_].split("\",");
                            String[] data33 = parse2.split("SONG_ID\":\"");
                            String[] data44 = data33[do_].split("\"");
                            String[] data5 = parse2.split("ARTIST_NAME\":\"");
                            String[] data6 = data5[do_].split("\",");
                            xgnm = data44[0];
                            nameee = data2[0];
                            artisttt = data6[0];
                        }

                        fileName = nameee + " - " + artisttt + ".mp3";
                        File saveDir2 = new File(Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);;
                        if(!saveDir2.exists()){
                            String uxtk = mPref.getString("uxtk", "");
                            String unm = mPref.getString("unm", "");
                            String parse;

                            if(cate.contains("bill")){
                                String search = nameee + "%20" + artisttt;
                                parse = gethttp1("https://app.genie.co.kr/Iv3/Search/f_Search_Song.asp?query=" + search.replace(" ","%20") + "&pagesize=1");

                                parse = parse.replace("<span class=\\\"t_point\\\">","");//태그제거
                                parse = parse.replace("<\\/span>","");
                                parse = parse.replace("%28", "");
                                parse = parse.replace("%29" ,"");
                                parse = parse.replace("%2C" ,"");
                                parse = parse.replace("%26" ,"");
                                parse = parse.replace("%29" ,"");
                                parse = parse.replace("\\" ,"");

                                String[] data3a = parse.split("SONG_ID\":\"");
                                String[] data4a = data3a[1].split("\"");
                                xgnm = data4a[0];
                            }

                            String str77 = "https://app.genie.co.kr/Iv3/Player/j_AppStmInfo_V2.asp?xgnm=" + xgnm + "&uxtk=" + uxtk + "&unm=" + unm + "&bitrate=" + "192&svc=DI";

                            parse = gethttp1(str77);
                            String[] data3 = parse.split("STREAMING_MP3_URL\":\"");
                            try {
                                String[] data4 = data3[1].split("\"");
                                try {
                                    resultt = java.net.URLDecoder.decode(data4[0], "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                fileURL = resultt;
                            } catch (Exception e) {
                            }

                            mPrefEdit.putString("fileURL", fileURL);
                            mPrefEdit.putString("fileName", fileName);
                            mPrefEdit.putString("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);
                            mPrefEdit.putInt("ischart", 1);
                            mPrefEdit.putInt("count", count);
                            mPrefEdit.putString("cate", cate);
                            mPrefEdit.commit();
                            MainActivity.is_downloading = true;

                            remoteView.setTextViewText(R.id.play, "로딩");
                            notification.contentView = remoteView;
                            notificationManager.notify(1, notification);
                            new DownloadFile().execute(fileURL);
                        }else{
                            count = do_;

                            mPrefEdit.putString("fileURL", fileURL);
                            mPrefEdit.putString("fileName", fileName);
                            mPrefEdit.putString("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);
                            mPrefEdit.putInt("ischart", 1);
                            mPrefEdit.putInt("count", do_);
                            mPrefEdit.putString("cate", cate);

                            mPrefEdit.putInt("notiplay", 1);
                            mPrefEdit.commit();


                    /*
                    Intent intentt = new Intent(context,Backmusic.class);
                    intentt.putExtra(Backmusic.MESSAGE_KEY,true);
                    context.startService(intentt);*/
                        }

                    }catch(Exception e){

                    }
                }
            }catch (Exception e){


            }




        }
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();// Create progress dialog
        }
        @Override
        protected String doInBackground(String... Url) {
            try {
                URL url = new URL(Url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();// Detect the file lenghth
                String filepath = Environment.getExternalStorageDirectory()// Locate storage location
                        .getPath();
                InputStream input = new BufferedInputStream(url.openStream());// Download the file
                OutputStream output;
                output = new FileOutputStream(filepath + "/.JM/"// Save the downloaded file
                        + fileName);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileLength));// Publish the progress
                    output.write(data, 0, count);
                }
                output.flush();// Close connection
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            if(progress[0] == 100){
                MainActivity.is_downloading = false;
                count = do_;

                mPrefEdit.putString("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);
                mPrefEdit.putInt("ischart", 1);
                mPrefEdit.putInt("count", count);
                mPrefEdit.putString("cate", cate);

                mPrefEdit.putInt("notiplay", 1);
                mPrefEdit.commit();
            }
        }
    }

    public String gethttp1(String sstr1) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet();
            post.setURI(new URI(sstr1));
            HttpResponse resp = client.execute(post);
            BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(),"utf-8"));
            String str = null;
            StringBuilder sb = new StringBuilder();
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



}