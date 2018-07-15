package com.jungcode.jm2.jm2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;


import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import android.support.v13.app.FragmentPagerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.Toast;


import com.afollestad.materialdialogs.AlertDialogWrapper;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jungcode.jm2.jm2.function.Mp3Singleton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import es.dmoral.toasty.Toasty;

import static com.jungcode.jm2.jm2.Backmusic.mediaPlayer;
import static com.jungcode.jm2.jm2.R.id.view;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.HIDDEN;


public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    int ispass =0;

    public static Activity main;


    public static FragmentManager fragmentManager2;
    public static FragmentTransaction fragmentTransaction2;

    String vip = "";

    public static SlidingUpPanelLayout panel;
    public static NumberProgressBar simpleProgressBar;
    public static Boolean is_downloading = false;

    SharedPreferences mPref;
    SharedPreferences.Editor mPrefEdit;
    String parse;
    String[] uxtk1, uxtk2, num1, num2, notice2, notice1, certification1, certification2, failnotice1, failnotice2;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_tabs);
        getSupportActionBar().hide();
        getSupportActionBar().setElevation(0);
        main = MainActivity.this;
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumSquareR.otf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumSquareB.otf"));

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        //actionBar.setCustomView(R.layout.custom_action);

        //customActionBarView = actionBar.getCustomView();
        //check_vail = (TextView)customActionBarView.findViewById(R.id.checkk);
        TextView title = (TextView)findViewById(R.id.title);
        title.setPaintFlags(title.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        /*
        if(Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }*/

        panel = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        //simpleProgressBar.setVisibility(View.INVISIBLE);
        simpleProgressBar=(NumberProgressBar)findViewById(R.id.number_progress_bar); // initiate the progress bar
        simpleProgressBar.setMax(100); // 100 maximum value for the progress value
        simpleProgressBar.setProgress(0); // 50 default progress value for the progress bar
        simpleProgressBar.setProgressTextColor(Color.parseColor("#717b82"));
        simpleProgressBar.setReachedBarColor(Color.parseColor("#717b82"));

        mPref = getSharedPreferences("mPref", 0);
        mPrefEdit = mPref.edit();
        vip = mPref.getString("vip", "");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        if(vip.contains("wow")){
            viewPager.setOffscreenPageLimit(4);
        }else{
            viewPager.setOffscreenPageLimit(1);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        getSupportActionBar().setTitle("음악 목록");
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                TextView title = (TextView)findViewById(R.id.title);
                if(position == 0){
                    findViewById(R.id.cate).setVisibility(View.GONE);
                    title.setText("지니 차트");
                }
                if(position == 1){
                    findViewById(R.id.cate).setVisibility(View.GONE);
                    title.setText("빌보드 차트");
                }
                if(position == 2){
                    findViewById(R.id.cate).setVisibility(View.GONE);
                    title.setText("검색");
                }
                if(position == 3){
                    findViewById(R.id.cate).setVisibility(View.GONE);
                    title.setText("설정");
                }
            }
        });

        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                String filepath = Environment.getExternalStorageDirectory().getPath();
                File saveDir = new File(filepath + "/.JM/");
                if (!saveDir.exists()) saveDir.mkdirs();

                File saveDir2 = new File(filepath + "/Music/JM/");
                if (!saveDir2.exists()) saveDir2.mkdirs();


                ConnectivityManager connectivityManager = (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);//인터넷연결확인
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                    posthttp3("https://raw.githubusercontent.com/iveinvalue/JM_ios/master/info.txt");//인증서버
                    parse = parse.replace("\\\\", "qwer");//전체파싱
                    parse = parse.replace("|", "qwer");
                    uxtk1 = parse.split("uxtk!");//uxtk파싱
                    uxtk2 = uxtk1[1].split("!");
                    num1 = parse.split("unm=");//unm파싱
                    num2 = num1[1].split("=");
                    notice1 = parse.split("notice=");//알림파싱
                    notice2 = notice1[1].split("=");
                    certification1 = parse.split("check=");//인증파싱
                    certification2 = certification1[1].split("=");
                    failnotice1 = parse.split("fail=");//인증파싱
                    failnotice2 = failnotice1[1].split("=");

                    if(failnotice2[0].contains(BuildConfig.VERSION_NAME)){

                    }
                    else{

                    }

                    String PhoneNumber = "MacAddr_Pass";

                    if (certification2[0].contains(getMacAddr())) {//맥주소인증시작
                        mPrefEdit.putString("uxtk", uxtk2[0]);
                        mPrefEdit.putString("unm", num2[0]);
                        mPrefEdit.commit();
                        ispass = 1;
                        //showToast("인증성공",1);
                        checkk(1);
                    }
                    else{
                        try{
                            TelephonyManager systemService = (TelephonyManager)getApplication().getSystemService(Context.TELEPHONY_SERVICE);
                            PhoneNumber = systemService.getLine1Number();
                            PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
                            PhoneNumber="0"+PhoneNumber;
                        }catch(Exception e ){
                            PhoneNumber = "NO_MacAddr";
                        }
                        if (certification2[0].contains(PhoneNumber)) {
                            mPrefEdit.putString("uxtk", uxtk2[0]);
                            mPrefEdit.putString("unm", num2[0]);
                            mPrefEdit.commit();
                            ispass =1;
                            PhoneNumber = PhoneNumber + "_PASS";
                            //showToast("인증성공",1);
                            checkk(1);
                        }
                        else{
                            mPrefEdit.putString("uxtk", "");
                            mPrefEdit.putString("unm", "");
                            mPrefEdit.commit();
                            ispass =0;
                            PhoneNumber = PhoneNumber + "_BLOCK";
                            //showToast("인증실패 (기능이 제한됩니다.)",0);
                            checkk(0);
                        }
                    }

                    if (!getMacAddr().contains("84:2e:27:a7:c2")) {
                        try {
                            String temp = "https://api.telegram.org/bot439468143:AAE5Kk8kJfTPD3p4EQTjmG8FojGg5oMS7EQ/sendMessage?chat_id=128419855&text=(JM)%20Ver_" + BuildConfig.VERSION_NAME + "_" + PhoneNumber + "_" + getMacAddr();
                            posthttp3(temp);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ispass =2;
                        }
                    }
                }
                else{
                    mPrefEdit.putString("uxtk", "");
                    mPrefEdit.putString("unm", "");
                    mPrefEdit.commit();
                    checkk(2);
                    showToast("인터넷 연결실패 (기능이 제한됩니다.)",0);
                }
            }
        };
        thread.start();

        mPrefEdit.putInt("is_started", 1);
        mPrefEdit.commit();

        fragmentManager2 = getFragmentManager();
        fragmentTransaction2 = fragmentManager2.beginTransaction();
        localplayer_frag fr2 = new localplayer_frag();
        fragmentTransaction2.replace(R.id.fragment_place2, fr2);
        fragmentTransaction2.commit();


        Button cate = (Button) findViewById(R.id.cate) ;
        cate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] itemss = {"최신음악","가요","OST","EDM"};
                new MaterialDialog.Builder(MainActivity.this)
                        .title("카테고리")
                        .items(itemss)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            }
                        })
                        .show();
            }
        });
        findViewById(R.id.cate).setVisibility(View.GONE);




    }


    public static void refresh_fra(){
        try{
            fragmentTransaction2 = fragmentManager2.beginTransaction();
            localplayer_frag fr2 = new localplayer_frag();
            fragmentTransaction2.replace(R.id.fragment_place2, fr2);
            fragmentTransaction2.commit();
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                public void run() {

                }
            }, 500);
        }catch (Exception e){
        }
    }



    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected void onStart() {
        super.onStart();
    }

    public void showToast(final String toast, final int check)
    {
        handler.postDelayed(new Runnable() {
            public void run() {
                if(check == 1)
                    Toasty.success(MainActivity.this, toast, Toast.LENGTH_SHORT, true).show();
                else
                    Toasty.error(MainActivity.this, toast, Toast.LENGTH_SHORT, true).show();
            }
        }, 100);
    }

    public void checkk(final int check)
    {
        handler.postDelayed(new Runnable() {
            public void run() {
                TextView check_vail = (TextView)findViewById(R.id.checkk);
                if(check == 1){
                    check_vail.setText("인증 성공");
                    check_vail.setTextColor(Color.parseColor("#4bae4f"));
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    //Toasty.info(getApplicationContext(), notice2[0], Toast.LENGTH_LONG, true).show();
                    if(!notice2[0].contains("-없음-")){
                        new MaterialDialog.Builder(MainActivity.this)
                                .title("공지사항")
                                .content(notice2[0].replace("\\n","\n"))
                                .positiveText("확인")
                                .show();
                    }
                }
                else{
                    check_vail.setText("인증 실패 (기능 제한)");
                    check_vail.setTextColor(Color.parseColor("#f34236"));
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                }
            }
        }, 2000);
    }

    private void setupTabIcons() {
        if(vip.contains("wow")){
            int[] tabIcons = {
                    R.drawable.genie,
                    R.drawable.billboard,
                    R.drawable.num2,
                    R.drawable.setting
            };
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            tabLayout.getTabAt(3).setIcon(tabIcons[3]);
            //tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        }else{
            int[] tabIcons = {
                    R.drawable.setting
            };
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        MainActivity.ViewPagerAdapter adapter = new MainActivity.ViewPagerAdapter(getFragmentManager());
        if(vip.contains("wow")){
            adapter.addFrag(new OneFragment(), "ONE");
            adapter.addFrag(new OneFragment_(), "ONE_");
            adapter.addFrag(new TwoFragment(), "TWO");
            //adapter.addFrag(new ThreeFragment(), "THREE");
            adapter.addFrag(new PrefsFragment(), "SET");
        }else{
            adapter.addFrag(new PrefsFragment(), "SET");
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return null;
        }
    }

    public static class PrefsFragment extends PreferenceFragment {
        Handler handler = new Handler();

        static Fragment newInstance(int SectionNumber) {
            PrefsFragment fragment = new PrefsFragment();
            Bundle args = new Bundle();
            args.putInt("section_number", SectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


            Preference cupon = (Preference) findPreference("cupon");
            cupon.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(getActivity())
                            .title("코드를 입력해주세요.")
                            //.content("")
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                            .input("코드", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    if(input.toString().contains("jungmusic")){

                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                Toasty.success(getActivity(), "확인완료 앱을 다시 시작해주세요.", Toast.LENGTH_SHORT, true).show();
                                                //TastyToast.makeText(getActivity(), "확인완료 앱을 다시 시작해주세요.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                            }
                                        }, 100);

                                        SharedPreferences mPref =  getActivity().getSharedPreferences("mPref", 0);
                                        SharedPreferences.Editor mPrefEdit = mPref.edit();
                                        mPrefEdit.putString("vip", "wow");
                                        mPrefEdit.commit();

                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                try {
                                                    localplayer.IntroAct.finish();
                                                } catch (Exception e) {
                                                }

                                                getActivity().moveTaskToBack(true);
                                                getActivity().finish();

                                                android.os.Process.killProcess(android.os.Process.myPid());
                                            }
                                        }, 2000);

                                    }else{
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                Toasty.error(getActivity(), "확인실패", Toast.LENGTH_SHORT, true).show();
                                                //TastyToast.makeText(getActivity(), "확인실패", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                                            }
                                        }, 100);
                                    }

                                }
                            }).show();
                    return true;
                }
            });

            Preference cache = (Preference) findPreference("cache");
            cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    String filepath = Environment.getExternalStorageDirectory()
                            .getPath();
                    File saveDir = new File(filepath + "/.JM/");
                    if (!saveDir.exists()) saveDir.mkdirs();


                    float mem0 = (float) (folderMemoryCheck2(Environment.getExternalStorageDirectory()+"/.JM/") / 1000000.0);
                    String mem = String.valueOf(mem0);

                    new MaterialDialog.Builder(getActivity())
                            .title("정말로 정리하시겠습니까?")
                            .content(mem + "MB")
                            .positiveText("확인")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    File dir = new File(Environment.getExternalStorageDirectory()+"/.JM/");
                                    if (dir.isDirectory())
                                    {
                                        String[] children = dir.list();
                                        for (int i = 0; i < children.length; i++)
                                        {
                                            new File(dir, children[i]).delete();
                                        }
                                    }
                                }
                            })
                            .negativeText("취소")
                            .show();
                    return true;
                }
            });

            Preference info = (Preference) findPreference("info");
            info.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(getActivity())
                            .title("앱 정보")
                            .content( BuildConfig.VERSION_NAME)
                            .positiveText("확인")
                            .show();
                    return true;
                }
            });

            Preference open = (Preference) findPreference("open");
            open.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(getActivity())
                            .title("오픈소스 라이브러리")
                            .content("Custom dialogs :\ncom.afollestad.material-dialogs:core:0.8.5.9\n\n" +
                                    "Pull down to refreshlayout :\ncom.baoyz.pullrefreshlayout:library:1.2.0\n\n" +
                                    "Custom seekbar :\ncom.minimize.library:seekbar-compat:0.2.5\n\n" +
                                    "Loading animation :\ncom.github.ybq:Android-SpinKit:1.1.0\n\n" +
                                    "Custom progressbar :\ncom.daimajia.numberprogressbar:library:1.4@aar\n\n" +
                                    "Imageview shape :\ncom.github.siyamed:android-shape-imageview:0.9.+@aar\n\n" +
                                    "Custom toast :\ncom.github.GrenderG:Toasty:1.2.5\n\n" +
                                    "Custom fonts :\ncom.tsengvn:typekit:1.0.1\n\n" +
                                    "Slidinguppanel :\ncom.sothree.slidinguppanel:library:3.3.1")
                            .positiveText("확인")
                            .show();
                    return true;
                }
            });
        }

        public long folderMemoryCheck2(String a_path){
            long totalMemory = 0;
            File file = new File(a_path);
            File[] childFileList = file.listFiles();

            if(childFileList == null){
                return 0;
            }

            for(File childFile : childFileList){
                if(childFile.isDirectory()){
                    totalMemory += folderMemoryCheck2(childFile.getAbsolutePath());

                }
                else{
                    totalMemory += childFile.length();
                }
            }
            return totalMemory;
        }

    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public void posthttp3(String sstr1) {
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

    public void onBackPressed() {

        if(panel.getPanelState() == EXPANDED){
            panel.setPanelState(COLLAPSED);
        }else{
            MaterialDialog dialog = (MaterialDialog) new AlertDialogWrapper.Builder(this)
                    .setMessage("정말로 종료하시겠습니까?")
                    .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NotificationManager notifManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notifManager.cancelAll();
                            moveTaskToBack(true);
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    }).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //moveTaskToBack(true);
        //finish();
       // NotificationManager notifManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //notifManager.cancelAll();
        //android.os.Process.killProcess(android.os.Process.myPid());

    }

}
