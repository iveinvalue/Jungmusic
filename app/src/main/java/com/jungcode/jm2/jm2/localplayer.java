package com.jungcode.jm2.jm2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jungcode.jm2.jm2.function.Mp3Singleton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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

import static com.jungcode.jm2.jm2.R.id.textView;


public class localplayer extends AppCompatActivity implements View.OnClickListener ,AudioManager.OnAudioFocusChangeListener {
    ProgressDialog mProgressDialog;
    int count;
    boolean leave = false;
    String  song , filepath = Environment.getExternalStorageDirectory().getPath(), parse, fileName,cate;
    Thread updateSeekBar;
    TextView album, artist, playingname, lyric ,start, end;
    MediaMetadataRetriever metaRetriver;
    byte[] art;
    SeekBarCompat sb;
    ImageButton btplay,nextsong,previoussong;
    Bitmap blurredBitmap, songImage;
    Handler handler = new Handler();
    MediaPlayer mediaPlayer;
    private long backKeyPressedTime = 0;
    ImageView album_art, album_art2 ,blur, next;
    public static Activity IntroAct;
    Boolean loopcheck = false, effectcheck = false,suffcheck=false;
    Button loop, effectb,Playlist,Shuffle;
    String[] nowplaylist;
    int position,endposition,ischart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localplayer);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        playingname = (TextView)findViewById(R.id.tittle);
        start = (TextView)findViewById(R.id.start);
        end = (TextView)findViewById(R.id.end);
        artist = (TextView)findViewById(R.id.artist);
        album = (TextView)findViewById(R.id.album);
        btplay = (ImageButton) findViewById(R.id.imageButton);
        next = (ImageButton) findViewById(R.id.next);
        album_art = (ImageView) findViewById(R.id.mImage);
        album_art2 = (ImageView) findViewById(R.id.imageView4);
        loop = (Button) findViewById(R.id.Repeat);
        //effectb = (Button) findViewById(R.id.Effect);
        nextsong = (ImageButton) findViewById(R.id.next);
        previoussong = (ImageButton) findViewById(R.id.previous);
        //Playlist = (Button) findViewById(R.id.Playlist);
        Shuffle = (Button) findViewById(R.id.Shuffle);

        playingname.setPaintFlags(playingname.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        btplay.setOnClickListener(this);
        next.setOnClickListener(this);
        nextsong.setOnClickListener(this);
        previoussong.setOnClickListener(this);
        //btplay.setAlpha(50);
        //nextsong.setAlpha(80);
        //previoussong.setAlpha(80);
        //mVisualizerView.setAlpha((float) 0.5);
        lyric.setMovementMethod(new ScrollingMovementMethod());
        lyric.setVisibility(View.INVISIBLE);

        getSupportActionBar().hide();
        IntroAct = this;

        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Intent i = getIntent();
        Bundle b = i.getExtras();

        try {
            cate = b.getString("cate");
            count = b.getInt("count");
            song = b.getString("song");
            ischart = b.getInt("ischart");
            position = b.getInt("position");
            nowplaylist = b.getStringArray("nowplaylist");
            endposition = b.getInt("endposition");
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                public void run()
                {
                    Toasty.error(getApplicationContext(), "재생중이 아닙니다.", Toast.LENGTH_LONG, true).show();
                    //TastyToast.makeText(getApplicationContext(), "재생중이 아닙니다.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            });
            finish();
        }



        SharedPreferences mPref = getSharedPreferences("mPref", 0);
        SharedPreferences.Editor mPrefEdit = mPref.edit();
        mPrefEdit.putString("song", song);
        mPrefEdit.commit();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // 오디오 포커스를 획득할 수 없다. }
        }

        try {
            playAudio(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
        blur.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                lyric.setVisibility(View.VISIBLE);
                //playingname.setVisibility(View.INVISIBLE);
                //artist.setVisibility(View.INVISIBLE);
                //album.setVisibility(View.INVISIBLE);
            }
        });

        Shuffle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                if (suffcheck == true) {
                    Shuffle.setAlpha((float) 0.5);
                    suffcheck = false;
                } else {
                    Shuffle.setAlpha((float) 1);
                    suffcheck = true;
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
                        Toasty.info(getApplicationContext(), "마지막 곡입니다.", Toast.LENGTH_SHORT, true).show();
                    }
                        //TastyToast.makeText(getApplicationContext(), "마지막 곡입니다.", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                }else{
                    if(position == endposition){
                        position = -1;
                    }
                    position++;
                    song = nowplaylist[position];
                    try {
                        playAudio(song);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Intent intent = new Intent(getApplication(), localplayer.class);
                //intent.putExtra("song",  nowplaylist[position+1]).putExtra("nowplaylist",nowplaylist).putExtra("position",position + 1).putExtra("endposition",endposition);
                //startActivity(intent);
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
                        Toasty.info(getApplicationContext(), "첫번째 곡입니다.", Toast.LENGTH_SHORT, true).show();
                    }
                        //TastyToast.makeText(getApplicationContext(), "첫번째 곡입니다.", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                }else{
                    if(position == 0){
                        position = endposition + 1;
                    }
                    position--;
                    song = nowplaylist[position];
                    try {
                        playAudio(song);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Intent intent = new Intent(getApplication(), localplayer.class);
                //intent.putExtra("song",  nowplaylist[position-1]).putExtra("nowplaylist",nowplaylist).putExtra("position",position - 1).putExtra("endposition",endposition);
                //startActivity(intent);
            }
        });

        lyric.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    backKeyPressedTime = System.currentTimeMillis();
                }
                else if(event.getAction() == MotionEvent.ACTION_MOVE){

                }
                else if(event.getAction() == MotionEvent.ACTION_UP){

                    if (System.currentTimeMillis() <= backKeyPressedTime + 100) {
                        lyric.setVisibility(View.INVISIBLE);
                        //playingname.setVisibility(View.VISIBLE);
                        //artist.setVisibility(View.VISIBLE);
                        //album.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        } );



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
        loop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (loopcheck == true) {
                    loop.setAlpha((float) 0.5);
                    mediaPlayer.setLooping(false);
                    loopcheck = false;
                } else {
                    loop.setAlpha((float) 1);
                    mediaPlayer.setLooping(true);
                    loopcheck = true;
                }
            }
        });



        /*
        new ParticleSystem(this, 80, R.drawable.confeti2, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.2f, 180, 180)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.emiter_top_right), 8);

        new ParticleSystem(this, 80, R.drawable.confeti1, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.2f, 0, 0)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.emiter_top_left), 8);*/
    }



    private void playAudio(String url) throws Exception {
        mic_effect();
        if(mediaPlayer!=null) {
            try {
                mediaPlayer.release();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = getSharedPreferences("music", MODE_PRIVATE).edit();
        editor.putString("url", url);
        editor.commit();


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(url);

        mediaPlayer.prepare();
        mediaPlayer.start();


        sb = (SeekBarCompat) findViewById(R.id.seekBar);
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
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        sb.setProgress(currentPosition);
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
                            else
                                start_next(1, cate);
                            updateSeekBar.interrupt();
                        }



                    }catch (Exception e){
                    }
                }
            }
        };
        try{
            updateSeekBar.stop();
        }catch (Exception e){
        }
        try{
            sb.setMax(mediaPlayer.getDuration());
            updateSeekBar.start();
        }catch (Exception e){
        }

        metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(song);//태그경로
        art = metaRetriver.getEmbeddedPicture();//앨범아트
        songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
        album_art.setImageBitmap(songImage);
        blurredBitmap = BlurBuilder.blur(this, songImage);
        album_art2.setImageBitmap(blurredBitmap);//앨범아트블러효과
        playingname.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));//타이틀
        artist.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));//아티스트
        album.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));//앨범이름

        //MainActivity.tittle.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        //MainActivity.artist.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        //MainActivity.album_art.setImageBitmap(songImage);

        //int _color =  blurredBitmap.getPixel(64, 64);
        Bitmap bitmapp = Bitmap.createScaledBitmap(blurredBitmap, 1, 1, true);

        int newcolor = Color.rgb(255- Color.red(bitmapp.getPixel(0, 0)),
                255- Color.green(bitmapp.getPixel(0, 0)),
                255- Color.blue(bitmapp.getPixel(0, 0)));
        sb.setProgressColor(bitmapp.getPixel(0, 0));
        sb.setProgressBackgroundColor(Color.parseColor("#6e6e6e"));
        sb.setThumbColor(newcolor);
        sb.setThumbAlpha(255);
        //start.setTextColor(newcolor);
        //end.setTextColor(newcolor);
        //sb.setThumbColor(newcolor);
        if (Build.VERSION.SDK_INT >= 21){
            try {
                int color = bitmapp.getPixel(0, 0);
                int color2 = Color.parseColor("#ffffffff");
                getWindow().setStatusBarColor(mixColors(color,color2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //File file = new File(filepath + "/.JM/" + song + ".txt");
        //lyric.setText("가사없음");
        Mp3Singleton mp3Singleton = Mp3Singleton.getInstance();
        mp3Singleton.setMp3(song);
        lyric.setText("\n" +  mp3Singleton.getLyric() + "\n");

        btplay.setImageResource(R.mipmap.pause);
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

        //Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        //intent.setAction( MediaPlayerService.ACTION_PLAY );
        //startService( intent );

    }//미디어재생

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
                    //btplay.setText(">");
                    btplay.setImageResource(R.mipmap.player);
                    //btplay.setAlpha(255);
                    mediaPlayer.pause();
                }
                else {
                    //btplay.setText("||");
                    btplay.setImageResource(R.mipmap.pause);
                    //btplay.setAlpha(50);
                    mediaPlayer.start();
                }
                break;
        }
    }//일시정지

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
    }//블러함수

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }



    @Override
    public void onResume() {
        super.onResume();
        // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    void mic_effect(){
        MediaRecorder recorder = null;
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        File effect = new File(filepath + "/.JM/effect");
        if(effect.exists()){
            File dir = new File(filepath + "/.JM/effect");
            dir.delete();
        }
        recorder.setOutputFile(filepath + "/.JM/effect");
        try {
            recorder.prepare();
            //recorder.stop();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }



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
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
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
    }//포스트1

    public int start_next(int do_,String cate){
        if(! MainActivity.is_downloading){
            String  fileURL= "" , resultt="";
            SharedPreferences mPref = getSharedPreferences("mPref", 0);
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


                SharedPreferences mPref2 = getSharedPreferences("mPref", 0);
                SharedPreferences.Editor mPrefEdit = mPref2.edit();
                mPrefEdit.putString("fileURL", fileURL);
                mPrefEdit.putString("fileName", fileName);
                //mPrefEdit.putString("lyric", data41[0]);
                mPrefEdit.commit();
                MainActivity.is_downloading = true;
                new DownloadFile().execute(fileURL);
            }else{
                //finalConvertView.findViewById(R.id.m).setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), localplayer.class);
                intent.putExtra("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName).putExtra("ischart", 1).putExtra("count", count).putExtra("cate", cate);
                startActivity(intent);
                //startActivity(intent);
            }
            //TastyToast.makeText(getApplicationContext(), "차트,검색시 작동하지 않습니다.", TastyToast.LENGTH_SHORT, TastyToast.INFO);
            //Toast.makeText(getApplicationContext(),"차트,검색시 작동하지 않습니다.", Toast.LENGTH_SHORT).show();

        }


        return 0;
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();// Create progress dialog
            mProgressDialog = new ProgressDialog(localplayer.this);// Set your progress dialog Title
            mProgressDialog.setTitle("곡정보 수집중");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();// Show progress dialog
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
            mProgressDialog.setProgress(progress[0]);// Update the progress dialog
            if(progress[0] == 100){
                MainActivity.is_downloading = false;
                mProgressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), localplayer.class);
                intent.putExtra("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName).putExtra("ischart", 1).putExtra("count", count).putExtra("cate", cate);
                startActivity(intent);

            }
            //mProgressDialog.dismiss();// Dismiss the progress dialog
        }
    }

}
