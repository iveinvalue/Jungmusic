package com.jungcode.jm2.jm2;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jungcode.jm2.jm2.function.Mp3Singleton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import static android.content.Context.MODE_PRIVATE;


import static android.os.SystemClock.sleep;
import static com.jungcode.jm2.jm2.Backmusic.mediaPlayer;
import static com.jungcode.jm2.jm2.Backmusic.notification;
import static com.jungcode.jm2.jm2.Backmusic.notificationManager;
import static com.jungcode.jm2.jm2.Backmusic.remoteView;
import static com.jungcode.jm2.jm2.MainActivity.fragmentManager2;
import static com.jungcode.jm2.jm2.MainActivity.fragmentTransaction2;
import static com.jungcode.jm2.jm2.MainActivity.panel;
import static com.jungcode.jm2.jm2.MainActivity.refresh_fra;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;


public class localplayer_frag extends Fragment implements View.OnClickListener ,AudioManager.OnAudioFocusChangeListener {
    ProgressDialog mProgressDialog;
    int position,endposition,ischart,is_started;
    boolean leave = false,loopcheck = false, runningThread = true;
    String  parse, fileName,main_lyrics ="";
    public static String song;
    byte[] art;
    String[] nowplaylist;
    static String cate;
    static int count;
    static boolean suffcheck=false;

    TextView album, artist, playingname, lyric ,start, end;
    Button loop,Shuffle;
    ImageView album_art, album_art2 ,blur, next;
    ImageButton btplay,nextsong,previoussong;
    ProgressBar sb2;
    SeekBarCompat sb;

    MediaMetadataRetriever metaRetriver;
    private static Thread updateSeekBar;
    Bitmap blurredBitmap, songImage;
    Handler handler = new Handler(),handler2;
    SharedPreferences.Editor mPrefEdit;
    SharedPreferences mPref;
    static View view;


    public localplayer_frag() {

    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_localplayer, null);

        mPref = getActivity().getSharedPreferences("mPref", 0);
        mPrefEdit = mPref.edit();

        playingname = (TextView)view.findViewById(R.id.tittle);
        start = (TextView)view.findViewById(R.id.start);
        end = (TextView)view.findViewById(R.id.end);
        artist = (TextView)view.findViewById(R.id.artist);
        album = (TextView)view.findViewById(R.id.album);
        lyric = (TextView)view.findViewById(R.id.lyric);
        btplay = (ImageButton) view.findViewById(R.id.imageButton);
        next = (ImageButton) view.findViewById(R.id.next);
        album_art = (ImageView) view.findViewById(R.id.mImage);
        album_art2 = (ImageView) view.findViewById(R.id.imageView4);
        blur = (ImageView) view.findViewById(R.id.blur);
        loop = (Button) view.findViewById(R.id.Repeat);
        nextsong = (ImageButton) view.findViewById(R.id.next);
        previoussong = (ImageButton) view.findViewById(R.id.previous);
        Shuffle = (Button) view.findViewById(R.id.Shuffle);

        playingname.setPaintFlags(playingname.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        btplay.setOnClickListener(this);
        next.setOnClickListener(this);
        nextsong.setOnClickListener(this);
        previoussong.setOnClickListener(this);
        lyric.setMovementMethod(new ScrollingMovementMethod());
        lyric.setVisibility(View.INVISIBLE);

        cate = mPref.getString("cate", "");
        count = mPref.getInt("count", 0);
        song = mPref.getString("song", "");
        ischart = mPref.getInt("ischart", 0);
        is_started = mPref.getInt("is_started", 0);
        if(is_started == 0){
            //view.findViewById(R.id.top_control).setVisibility(View.GONE);
            //check_extend = true;
            panel.setPanelState(EXPANDED);
        }
        try{
            if(mPref.getInt("loop", 0) == 1){
                loopcheck = true;
            }
            if(mPref.getInt("loop", 0) == 0){
                loopcheck = false;
            }
        }catch (Exception e){
            loopcheck = true;
        }
        try{
            if(mPref.getInt("shuffle", 0) == 1){
                suffcheck = true;
            }
            if(mPref.getInt("shuffle", 0) == 0){
                suffcheck = false;
            }
        }catch (Exception e){
            suffcheck = true;
        }

        mPrefEdit.putInt("ischart", 0);
        mPrefEdit.commit();

        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // 오디오 포커스를 획득할 수 없다. }
        }

        try {
            playAudio(song);
        } catch (Exception e) {
            e.printStackTrace();
        }

        album_art.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                Mp3Singleton mp3Singleton = Mp3Singleton.getInstance();
                mp3Singleton.setMp3(mPref.getString("song", ""));
                main_lyrics = "\n" +  mp3Singleton.getLyric() + "\n";
                new MaterialDialog.Builder(getActivity())
                        .title("가사 정보")
                        .content(main_lyrics)
                        .positiveText("확인")
                        .show();
            }
        });

        Shuffle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                if (suffcheck == true) {
                    mPrefEdit.putInt("shuffle", 0);
                    mPrefEdit.commit();
                    Shuffle.setAlpha((float) 0.5);
                    suffcheck = false;
                    Toasty.normal(getActivity(), "랜덤 재생 끔").show();
                } else {
                    mPrefEdit.putInt("shuffle", 1);
                    mPrefEdit.commit();
                    Shuffle.setAlpha((float) 1);
                    suffcheck = true;
                    Toasty.normal(getActivity(), "랜덤 재생 킴").show();
                }
            }
        });

        nextsong.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                if(ischart == 1){
                    if(!(count >= 100)){
                        count++;
                        if(suffcheck == true){
                            Random rand = new Random();
                            int n = rand.nextInt(100);
                            count = n;
                        }
                        start_next(count,cate);
                    }
                    else{
                        Toasty.info(getActivity(), "마지막 곡입니다.", Toast.LENGTH_SHORT, true).show();
                    }
                }else{
                    if(position == endposition){
                        position = -1;
                    }
                    position++;
                    try {
                        song = nowplaylist[position];
                        playAudio(song);
                    } catch (Exception e) {
                        Toasty.normal(getActivity(), "재생 목록이 없습니다. 다른 곡을 재생해주세요.").show();
                    }
                }
            }
        });

        previoussong.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                if(ischart == 1){
                    if(!(count <= 1)) {
                        count--;
                        if(suffcheck == true){
                            Random rand = new Random();
                            int n = rand.nextInt(100);
                            count = n;
                        }
                        start_next(count, cate);
                    }
                    else{
                        Toasty.info(getActivity(), "첫번째 곡입니다.", Toast.LENGTH_SHORT, true).show();
                    }
                }else{
                    if(position == 0){
                        position = endposition + 1;
                    }
                    position--;
                    try {
                        song = nowplaylist[position];
                        playAudio(song);
                    } catch (Exception e) {
                        Toasty.normal(getActivity(), "재생 목록이 없습니다. 다른 곡을 재생해주세요.").show();
                    }
                }
            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (loopcheck == true) {
                    mPrefEdit.putInt("loop", 0);
                    mPrefEdit.commit();
                    loop.setAlpha((float) 0.5);
                    mediaPlayer.setLooping(false);
                    loopcheck = false;
                    Toasty.normal(getActivity(), "반복 재생 끔").show();
                } else {
                    mPrefEdit.putInt("loop", 1);
                    mPrefEdit.commit();
                    loop.setAlpha((float) 1);
                    mediaPlayer.setLooping(true);
                    loopcheck = true;
                    Toasty.normal(getActivity(), "반복 재생 킴").show();
                }
            }
        });

        panel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //iew.findViewById(R.id.top_control).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState == COLLAPSED){
                    view.findViewById(R.id.top_control).setVisibility(View.VISIBLE);
                }else{
                    view.findViewById(R.id.top_control).setVisibility(View.GONE);
                }
            }
        });


        return view;
    }


    private void playAudio(String url) throws Exception {
        song = mPref.getString("song", "");
        Intent intent = new Intent(getActivity(),Backmusic.class);
        intent.putExtra(Backmusic.MESSAGE_KEY,true);
        getActivity().startService(intent);

        sb = (SeekBarCompat) view.findViewById(R.id.seekBar);
        sb.setProgressBackgroundColor(Color.parseColor("#6e6e6e"));
        metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(mPref.getString("song", ""));//태그경로
        art = metaRetriver.getEmbeddedPicture();//앨범아트
        songImage = BitmapFactory.decodeByteArray(art, 0, art.length);

        playingname.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));//타이틀
        artist.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));//아티스트
        album.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));//앨범이름
        album_art.setImageBitmap(songImage);
        blurredBitmap = BlurBuilder.blur(getActivity(), songImage);
        album_art2.setImageBitmap(blurredBitmap);//앨범아트블러효과


        TextView title_smaoll = (TextView) view.findViewById(R.id.tittle_small);
        TextView artist_smaoll = (TextView) view.findViewById(R.id.artist_small);
        title_smaoll.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        artist_smaoll.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

        //int _color =  blurredBitmap.getPixel(64, 64);
        Bitmap bitmapp = Bitmap.createScaledBitmap(blurredBitmap, 1, 1, true);


        int newcolor = Color.rgb(255- Color.red(bitmapp.getPixel(0, 0)),
                255- Color.green(bitmapp.getPixel(0, 0)),
                255- Color.blue(bitmapp.getPixel(0, 0)));
        sb.setProgressColor(bitmapp.getPixel(0, 0));
        sb.setProgressBackgroundColor(Color.parseColor("#6e6e6e"));
        sb.setThumbColor(newcolor);
        sb.setThumbAlpha(255);

        sb2 = (ProgressBar) view.findViewById(R.id.seekBar2);
        sb2.setScaleY(3f);
        sb2.getProgressDrawable().setColorFilter(bitmapp.getPixel(0, 0), PorterDuff.Mode.SRC_IN);

        if (Build.VERSION.SDK_INT >= 21){
            try {
                int color = bitmapp.getPixel(0, 0);
                int color2 = Color.parseColor("#ffffffff");
                //getActivity().getWindow().setStatusBarColor(mixColors(color,color2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //Toasty.normal(getActivity(), song).show();
        runningThread = true;
        final Handler handler3 = new Handler();
        handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                updateSeekBar = new Thread() {
                    @Override
                    public void run() {
                        int totalDuration = mediaPlayer.getDuration();
                        int time = totalDuration/1000;
                        int minu = time/60;
                        int sec =  time - minu*60;
                        String r_sec = String.valueOf(sec);
                        final String r_minu = String.valueOf(minu);
                        if(sec < 10){
                            r_sec = "0" + r_sec ;
                        }
                        final String finalR_sec = r_sec;
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                end.setText(r_minu + ":" + finalR_sec);
                            }
                        }, 0);
                        int currentPosition = 0;
                        while (true){
                            if (!runningThread) {
                                return;
                            }
                            handler3.postDelayed(new Runnable() {
                                public void run() {
                                    try{
                                        if(mediaPlayer.isPlaying()){
                                            btplay.setImageResource(R.mipmap.pause);
                                        }else{
                                            btplay.setImageResource(R.mipmap.player);
                                        }
                                    }catch (Exception e){
                                    }
                                }
                            }, 0);

                            try {

                                currentPosition = mediaPlayer.getCurrentPosition();
                                sb.setProgress(currentPosition);
                                sb2.setProgress(currentPosition);
                                int time2 = currentPosition/1000;
                                int minu2 = time2/60;
                                int sec2 =  time2 - minu2*60;
                                String r_sec2 = String.valueOf(sec2);
                                final String r_minu2 = String.valueOf(minu2);
                                if(sec2 < 10){
                                    r_sec2 = "0" + r_sec2 ;
                                }
                                final String finalR_sec1 = r_sec2;
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        start.setText(r_minu2 + ":" + finalR_sec1);
                                    }
                                }, 0);

                                if(!leave && (loopcheck == false) && (r_minu.equals(r_minu2)) && (finalR_sec.equals(finalR_sec1))){
                                    leave = true;
                                    runningThread = false;
                                    if(!(count >= 100)) {
                                        count++;
                                        if(suffcheck == true){
                                            Random rand = new Random();
                                            int n = rand.nextInt(100);
                                            count = n;
                                        }
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                start_next(count, cate);
                                            }
                                        }, 0);
                                    }
                                    else{
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                start_next(1, cate);
                                            }
                                        }, 0);
                                    }
                                    return;
                                }

                                /*
                                if(mPref.getInt("notiplay", 0) == 1){
                                    mPrefEdit.putInt("notiplay", 0);
                                    mPrefEdit.commit();
                                    //runningThread = false;
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            try{
                                                playAudio("");
                                            }catch (Exception e){

                                            }
                                        }
                                    }, 0);
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            return;
                                        }
                                    }, 300);
                                }*/
                                sleep(400);




                            }catch (Exception e){
                            }
                        }
                    }
                };

                try{
                    sb.setMax(mediaPlayer.getDuration());
                    sb2.setMax(mediaPlayer.getDuration());
                    updateSeekBar.start();

                }catch (Exception e){
                }

                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                });

                if (loopcheck == false) {
                    loop.setAlpha((float) 0.5);
                    mediaPlayer.setLooping(false);
                } else {
                    loop.setAlpha((float) 1);
                    mediaPlayer.setLooping(true);
                }

                if (suffcheck == false) {
                    Shuffle.setAlpha((float) 0.5);
                } else {
                    Shuffle.setAlpha((float) 1);
                }
            }
        }, 0);


    }



    public int mixColors(int col1, int col2) {
        int r1, g1, b1, r2, g2, b2;

        r1 = Color.red(col1);
        g1 = Color.green(col1);
        b1 = Color.blue(col1);

        r2 = Color.red(col2);
        g2 = Color.green(col2);
        b2 = Color.blue(col2);

        int r3 = (r1 + r2)/2;
        int g3 = (g1 + g2)/2;
        int b3 = (b1 + b2)/2;

        return Color.rgb(r3, g3, b3);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imageButton:
                if(mediaPlayer.isPlaying()){
                    btplay.setImageResource(R.mipmap.player);
                    mediaPlayer.pause();
                    remoteView.setTextViewText(R.id.play, "재생");
                }
                else {
                    btplay.setImageResource(R.mipmap.pause);
                    mediaPlayer.start();
                    remoteView.setTextViewText(R.id.play, "정지");
                }
                notification.contentView = remoteView;
                notificationManager.notify(1, notification);
                break;
        }
    }

    public static class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 25f;

        public static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mediaPlayer == null) {
                }
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                /*
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
                */
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;

        }
    }

    public void gethttp1(String sstr1) {
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
            parse = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int start_next(int do_,String cate){
        runningThread = false;
        if(! MainActivity.is_downloading){
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
            }catch(Exception e){
                Random rand = new Random();
                int n = rand.nextInt(100);
                count = n;
                start_next(count,cate);
                return 0;
            }

            fileName = nameee + " - " + artisttt + ".mp3";
            File saveDir2 = new File(Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);;
            if(!saveDir2.exists()){

                String uxtk = mPref.getString("uxtk", "");
                String unm = mPref.getString("unm", "");

                if(cate.contains("bill")){
                    String search = nameee + "%20" + artisttt;
                    gethttp1("https://app.genie.co.kr/Iv3/Search/f_Search_Song.asp?query=" + search.replace(" ","%20") + "&pagesize=1");//차트가져오기

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

                gethttp1(str77);
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
                    //Toast.makeText(getActivity(), "오류", Toast.LENGTH_LONG).show();
                }

                mPrefEdit.putString("fileURL", fileURL);
                mPrefEdit.putString("fileName", fileName);
                mPrefEdit.putString("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);
                mPrefEdit.putInt("ischart", 1);
                mPrefEdit.putInt("count", count);
                mPrefEdit.putString("cate", cate);
                mPrefEdit.commit();
                MainActivity.is_downloading = true;
                Handler handler2 = new Handler();
                final String finalFileURL = fileURL;
                runningThread = false;
                handler2.postDelayed(new Runnable() {
                    public void run() {
                        try{
                            new DownloadFile().execute(finalFileURL);
                        }catch (Exception e){
                        }
                    }
                }, 301);
            }else{
                mPrefEdit.putString("fileURL", fileURL);
                mPrefEdit.putString("fileName", fileName);
                mPrefEdit.putString("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);
                mPrefEdit.putInt("ischart", 1);
                mPrefEdit.putInt("count", count);
                mPrefEdit.putString("cate", cate);
                mPrefEdit.commit();
                try {
                    runningThread = false;
                    leave = false;
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        public void run() {
                            try{
                                playAudio(song);
                            }catch (Exception e){
                            }
                        }
                    }, 301);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                /*
                try{
                    fragmentTransaction2 = fragmentManager2.beginTransaction();
                    localplayer_frag fr2 = new localplayer_frag();
                    fragmentTransaction2.replace(R.id.fragment_place2, fr2);
                    fragmentTransaction2.commit();
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        public void run() {
                            panel.setPanelState(EXPANDED);
                        }
                    }, 500);
                }catch (Exception e){
                }

                Intent intent = new Intent(getActivity(), localplayer_frag.class);
                intent.putExtra("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName).putExtra("ischart", 1).putExtra("count", count).putExtra("cate", cate);
                startActivity(intent);
                */
            }
        }
        return 0;
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();// Create progress dialog
            try{
                mProgressDialog = new ProgressDialog(getActivity());// Set your progress dialog Title
                mProgressDialog.setTitle("곡정보 수집중");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();// Show progress dialog
            }catch ( Exception e){
            }
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
            try{
                mProgressDialog.setProgress(progress[0]);// Update the progress dialog
            }catch ( Exception e){
            }
            if(progress[0] == 100){
                MainActivity.is_downloading = false;
                try{
                    mProgressDialog.dismiss();
                }catch ( Exception e){
                }
                mPrefEdit.putString("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);
                mPrefEdit.putInt("ischart", 1);
                mPrefEdit.putInt("count", count);
                mPrefEdit.putString("cate", cate);
                mPrefEdit.commit();

                try {
                    runningThread = false;
                    leave = false;
                    playAudio(song);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                /*
                try{
                    fragmentTransaction2 = fragmentManager2.beginTransaction();
                    localplayer_frag fr2 = new localplayer_frag();
                    fragmentTransaction2.replace(R.id.fragment_place2, fr2);
                    fragmentTransaction2.commit();
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        public void run() {
                            panel.setPanelState(EXPANDED);
                        }
                    }, 500);
                }catch (Exception e){
                }

                Intent intent = new Intent(getActivity(), localplayer_frag.class);
                intent.putExtra("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName).putExtra("ischart", 1).putExtra("count", count).putExtra("cate", cate);
                startActivity(intent);*/

            }
            //mProgressDialog.dismiss();// Dismiss the progress dialog
        }
    }




}
