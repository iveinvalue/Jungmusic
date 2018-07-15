package com.jungcode.jm2.jm2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Fragment;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.widget.PullRefreshLayout;
import com.jungcode.jm2.jm2.MainActivity;
import com.jungcode.jm2.jm2.R;
import com.jungcode.jm2.jm2.function.ListData;
import com.jungcode.jm2.jm2.localplayer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.jungcode.jm2.jm2.MainActivity.refresh_fra;


public class TwoFragment extends Fragment{
    View view;
    int isstreaming = 0;

    View ConvertVieww;

    String parse, fileName, fileURL, result ,uxtk , unm,counttt;
    ProgressDialog mProgressDialog;
    Handler handler = new Handler();
    Bitmap bmp;
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    String check;
    int finalSearch_check = 0;
    EditText mEdit;
    SharedPreferences mPref;
    public TwoFragment() {

    }
    boolean check_click = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, null);

        mPref = getActivity().getSharedPreferences("mPref", 0);
        view.findViewById(R.id.spin_kit).setVisibility(View.INVISIBLE);

        mListView = (ListView) view.findViewById(R.id.listView);
        mAdapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mEdit = (EditText) view.findViewById(R.id.editText);

        final PullRefreshLayout layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                view.findViewById(R.id.spin_kit).setVisibility(View.VISIBLE);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                //mAdapter.mListData.clear();
                            }
                        }, 100);
                        //start2();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                layout.setRefreshing(false);
                                view.findViewById(R.id.spin_kit).setVisibility(View.INVISIBLE);
                            }
                        }, 100);
                    }
                };
                thread.start();
            }
        });
        layout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);

        Button search = (Button) view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                if(!check_click){
                    check_click = true;
                    mAdapter.mListData.clear();
                    view.findViewById(R.id.spin_kit).setVisibility(View.VISIBLE);
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            start2();
                        }
                    };
                    thread.start();
                }
            }
        });

        return view;
    }


    void start2(){
        finalSearch_check = 1;

        gethttp1("https://app.genie.co.kr/Iv3/Search/f_Search_Song.asp?query=" + mEdit.getText().toString().replace(" ","%20") + "&pagesize=50");//차트가져오기

        parse = parse.replace("<span class=\\\"t_point\\\">","");//태그제거
        parse = parse.replace("<\\/span>","");
        parse = parse.replace("%28", "(");
        parse = parse.replace("%29" ,")");
        parse = parse.replace("%2C" ,",");
        parse = parse.replace("%26" ,"&");
        parse = parse.replace("%3A" ,":");
        parse = parse.replace("\\" ,"");

        SharedPreferences mPref = getActivity().getSharedPreferences("mPref", 0);
        SharedPreferences.Editor mPrefEdit = mPref.edit();
        mPrefEdit.putString("chartdata3", parse);
        mPrefEdit.commit();

        for (int aa = 1; aa < 101; aa++){
            try {
                String[] data = parse.split("SONG_NAME\":\"");
                String[] data2 = data[aa].split("\",");
                String[] data3 = parse.split("SONG_ID\":\"");
                String[] data4 = data3[aa].split("\"");
                String[] data5 = parse.split("ARTIST_NAME\":\"");
                String[] data6 = data5[aa].split("\",");
                String[] data8;
                if(finalSearch_check == 0){
                    String[] data7 = parse.split("THUMBNAIL_IMG_PATH\":\"");
                    data8 = data7[aa].split("\",");
                }else{
                    String[] data7 = parse.split("IMG_PATH\":\"");
                    data8 = data7[aa].split("\",");
                    data8[0] = data8[0].replace("\\", "");
                }
                data8[0] = data8[0].replace("%3A", ":");
                data8[0] = data8[0].replace("%2F", "/");
                //URL url = new URL(data8[0]);
                check = "✓";
                String filepath = Environment.getExternalStorageDirectory()
                        .getPath();
                File saveDir2 = new File(filepath + "/music/JM/"
                        + data2[0] + " - " + data6[0] + ".mp3");
                if(!saveDir2.exists()){
                    check = "";
                }

                File file = new File(filepath + "/.JM/" + data4[0] + ".jpg");
                //Toast.makeText(getActivity(),data8[0], Toast.LENGTH_LONG).show();
                if (!file.exists()){

                    if(finalSearch_check == 0){
                        StringBuilder html = new StringBuilder();
                        try{
                            URL url = new URL(data8[0]);

                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                            conn.setConnectTimeout(10000);
                            conn.setUseCaches(false);
                            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){

                                FileOutputStream fileOutput = new FileOutputStream(file);
                                InputStream inputStream = conn.getInputStream();

                                int downloadedSize = 0;
                                byte[] buffer = new byte[1024];
                                int bufferLength = 0;

                                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                                    fileOutput.write(buffer, 0, bufferLength);
                                    downloadedSize += bufferLength;

                                }
                                fileOutput.close();
                            }
                            conn.disconnect();

                        }catch(Exception ex){;}
                    }else{
                        StringBuilder html = new StringBuilder();
                        try{
                            URL url = new URL("http://image.genie.co.kr" + data8[0]);
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                            conn.setConnectTimeout(10000);
                            conn.setUseCaches(false);
                            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){

                                FileOutputStream fileOutput = new FileOutputStream(file);
                                InputStream inputStream = conn.getInputStream();

                                int downloadedSize = 0;
                                byte[] buffer = new byte[1024];
                                int bufferLength = 0;

                                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                                    fileOutput.write(buffer, 0, bufferLength);
                                    downloadedSize += bufferLength;

                                }
                                fileOutput.close();
                            }
                            conn.disconnect();

                        }catch(Exception ex){;}
                    }



                }

                bmp = BitmapFactory.decodeFile(filepath + "/.JM/" + data4[0] + ".jpg");
                //bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Drawable d = new BitmapDrawable(getResources(), bmp);


                data2[0] = data2[0].replace("/", "");
                if(check.contains("✓")){
                    mAdapter.addItem(d,
                            data2[0] , data6[0],
                            data4[0], check, "",getResources().getDrawable(R.drawable.donedown),String.valueOf(aa));
                    //list.add(data2[0] + " - " + data6[0] + " - " + data4[0]);
                }
                else{
                    mAdapter.addItem(d,
                            data2[0] , data6[0],
                            data4[0], check, "",getResources().getDrawable(R.drawable.startdown),String.valueOf(aa));
                    //list.add(data2[0] + " - " + data6[0] + " - " + data4[0]);
                }

            } catch (Exception e) {
            }



        }
        handler.postDelayed(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
                view.findViewById(R.id.spin_kit).setVisibility(View.INVISIBLE);
                check_click = false;
            }
        }, 100);
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ConvertVieww = convertView;
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_card2_norank, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.downcheck = (ImageView) convertView.findViewById(R.id.downcheck);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
                holder.mText2 = (TextView) convertView.findViewById(R.id.mText2);
                holder.mcheck = (TextView) convertView.findViewById(R.id.mcheck);
                holder.count = (TextView) convertView.findViewById(R.id.count);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            final ListData mData = mListData.get(position);

            if (mData.mIcon != null) {
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageDrawable(mData.mIcon);
            }else{
                holder.mIcon.setVisibility(View.GONE);
            }

            if (mData.downcheck != null) {
                holder.downcheck.setVisibility(View.VISIBLE);
                holder.downcheck.setImageDrawable(mData.downcheck);
            }else{
                holder.downcheck.setVisibility(View.GONE);
            }


            holder.mText.setText(mData.mTitle);
            holder.mDate.setText(mData.mDate);
            holder.mText2.setText(mData.mTitle2);
            holder.mcheck.setText(mData.mcheck);
            holder.count.setText(mData.count);

            Button Button1= (Button)  convertView  .findViewById(R.id.button);
            Button Button2= (Button)  convertView  .findViewById(R.id.button2);

            TextView mText= (TextView)  convertView  .findViewById(R.id.mText);
            final String mTextt = (String) mText.getText();

            TextView mText2= (TextView)  convertView  .findViewById(R.id.mText2);
            final String mTextt2 = (String) mText2.getText();

            TextView mDate= (TextView)  convertView  .findViewById(R.id.mDate);
            final String mDatee = (String) mDate.getText();


            final View finalConvertView = convertView;
            Button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(! MainActivity.is_downloading){
                        uxtk = mPref.getString("uxtk", "");
                        unm = mPref.getString("unm", "");
                        String str77 = "https://app.genie.co.kr/Iv3/Player/j_AppStmInfo_V2.asp?xgnm=" + mDatee + "&uxtk=" + uxtk + "&unm=" + unm + "&bitrate=" + "192&svc=DI";

                        //Toast.makeText(getActivity(),str77, Toast.LENGTH_LONG).show();

                        gethttp1(str77);
                        String[] data3 = parse.split("STREAMING_MP3_URL\":\"");

                        try {
                            String[] data4 = data3[1].split("\"");
                            try {
                                result = java.net.URLDecoder.decode(data4[0], "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            fileName = mTextt + " - " + mTextt2 + ".mp3";
                            fileURL = result;
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "오류", Toast.LENGTH_LONG).show();
                        }

                        counttt = mData.count;
                        SharedPreferences mPref = getActivity().getSharedPreferences("mPref", 0);
                        SharedPreferences.Editor mPrefEdit = mPref.edit();
                        mPrefEdit.putString("fileURL", fileURL);
                        mPrefEdit.putString("fileName", fileName);
                        //mPrefEdit.putString("lyric", data41[0]);
                        mPrefEdit.putString("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);
                        mPrefEdit.putInt("ischart", 1);
                        mPrefEdit.putInt("count", Integer.parseInt(counttt));
                        mPrefEdit.putString("cate", "search");
                        mPrefEdit.commit();

                        isstreaming =1;

                        File saveDir2 = new File(Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);;
                        if(!saveDir2.exists()){
                            MainActivity.simpleProgressBar.setVisibility(View.VISIBLE);
                            MainActivity.is_downloading = true;
                            new DownloadFile().execute(fileURL);
                            mProgressDialog.dismiss();
                        }else{
                            refresh_fra();

                            /*
                            Intent intent = new Intent(getActivity(), localplayer.class);
                            intent.putExtra("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName).putExtra("ischart", 1).putExtra("count", Integer.parseInt(counttt)).putExtra("cate","search");;;
                            startActivity(intent);*/
                        }

                        //Toast.makeText(getActivity(),Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName, Toast.LENGTH_LONG).show();

                        //Intent intent = new Intent(getActivity(), localplayer.class);
                        //intent.putExtra("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);

                        //startActivity(intent);
                        //startActivity(new Intent(getActivity(), Player.class).putExtra("pos", fileURL).putExtra("song", fileName).putExtra("lyric", data41[0]));
                        //Toast.makeText(getApplicationContext(),mTextt, Toast.LENGTH_LONG).show();
                    }else{
                        Toasty.error(getActivity(), "다른 다운로드가 진행중입니다.", Toast.LENGTH_SHORT, true).show();
                    }

                }

            });
            Button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(! MainActivity.is_downloading){
                        String checktitle;
                        if(mData.mcheck.contains("✓")){
                            checktitle = "다시 다운로드하시겠습니까?";
                        }else{
                            checktitle = "정말로 다운로드하시겠습니까?";
                        }
                        MaterialDialog dialog = (MaterialDialog) new AlertDialogWrapper.Builder(getActivity())
                                .setTitle(checktitle)
                                .setMessage(mTextt + " - " + mTextt2)
                                .setNegativeButton("다운로드", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        uxtk = mPref.getString("uxtk", "");
                                        unm = mPref.getString("unm", "");
                                        String str77 = "https://app.genie.co.kr/Iv3/Player/j_AppStmInfo_V2.asp?xgnm=" + mDatee + "&uxtk=" + uxtk + "&unm=" + unm + "&bitrate=" + "500&svc=DI";
                                        gethttp1(str77);
                                        String[] data3 = parse.split("STREAMING_MP3_URL\":\"");
                                        try {
                                            String[] data4 = data3[1].split("\"");
                                            try {
                                                result = java.net.URLDecoder.decode(data4[0], "UTF-8");
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                            fileName = mTextt + " - " + mTextt2 + ".mp3";
                                            fileURL = result;

                                            String filepath = Environment.getExternalStorageDirectory()
                                                    .getPath();
                                            File saveDir = new File(filepath + "/Music/JM/");
                                            if (!saveDir.exists()) saveDir.mkdirs();
                                            MainActivity.simpleProgressBar.setVisibility(View.VISIBLE);
                                            MainActivity.is_downloading = true;
                                            new DownloadFile().execute(fileURL);
                                            mProgressDialog.dismiss();
                                            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                                            //startActivity(browserIntent);

                                        } catch (Exception e) {
                                            Toast.makeText(getActivity(), "오류", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).show();
                    }else{
                        Toasty.error(getActivity(), "다른 다운로드가 진행중입니다.", Toast.LENGTH_SHORT, true).show();
                    }



                }

            });

            return convertView;
        }



        public void addItem(Drawable icon, String mTitle, String mTitle2 , String mDate, String mcheck,String count,Drawable downcheck,String aa){
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mTitle2 = mTitle2;
            addInfo.mDate = mDate;
            addInfo.mcheck = mcheck;
            addInfo.count = aa;
            addInfo.downcheck = downcheck;

            mListData.add(addInfo);
        }
        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }
        public void removeall(){
            mListData.clear();
            dataChange();
        }
        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }
    }



    private class ViewHolder {
        public ImageView mIcon;
        public ImageView downcheck;
        public TextView mText;
        public TextView mDate;
        public TextView mText2;
        public TextView mcheck;
        public TextView count;
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();// Create progress dialog
            mProgressDialog = new ProgressDialog(getActivity());// Set your progress dialog Title
            if(isstreaming == 0){
                mProgressDialog.setTitle("MP3 다운로드중...");// Set your progress dialog Message
            }else{
                mProgressDialog.setTitle("곡정보 수집중");// Set your progress dialog Message
            }
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
                if(isstreaming == 0){
                    output = new FileOutputStream(filepath + "/Music/JM/"// Save the downloaded file
                            + fileName);
                }else{
                    output = new FileOutputStream(filepath + "/.JM/"// Save the downloaded file
                            + fileName);
                }

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
            MainActivity.simpleProgressBar.setProgress(progress[0]);
            //mProgressDialog.setProgress(progress[0]);// Update the progress dialog
            if(progress[0] == 100){
                MainActivity.simpleProgressBar.setVisibility(View.GONE);
                MainActivity.is_downloading = false;
                if(isstreaming == 0) {
                    mProgressDialog.dismiss();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                FilenameFilter fileFilter = new FilenameFilter()  //이부분은 특정 확장자만 가지고 오고 싶을 경우 사용하시면 됩니다.
                                {
                                    public boolean accept(File dir, String name) {
                                        return name.endsWith("mp3"); //이 부분에 사용하고 싶은 확장자를 넣으시면 됩니다.
                                    } //end accept
                                };
                                String filepath = Environment.getExternalStorageDirectory()
                                        .getPath();
                                File file = new File(filepath + "/Music/JM/"); //경로를 SD카드로 잡은거고 그 안에 있는 A폴더 입니다. 입맛에 따라 바꾸세요.
                                File[] files = file.listFiles(fileFilter);//위에 만들어 두신 필터를 넣으세요. 만약 필요치 않으시면 fileFilter를 지우세요.
                                String[] titleList = new String[files.length]; //파일이 있는 만큼 어레이 생성했구요
                                for (int i = 0; i < files.length; i++) {
                                    titleList[i] = files[i].getName();    //루프로 돌면서 어레이에 하나씩 집어 넣습니다.
                                    File hFile = new File(filepath + "/Music/JM/" + titleList[i]);
                                    new SingleMediaScanner(getActivity(), hFile);
                                }//end for


                            } catch (Exception e) {
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), "스캔실패", Toast.LENGTH_LONG).show();
                                    }
                                }, 1);
                            }//end catch()
                            handler.postDelayed(new Runnable() {
                                public void run() {

                                    Toasty.success(getActivity(), "저장완료!", Toast.LENGTH_LONG, true).show();
                                }
                            }, 0);
                        }
                    };
                    thread.start();
                }else{
                    mProgressDialog.dismiss();
                    isstreaming = 0;

                    SharedPreferences mPref = getActivity().getSharedPreferences("mPref", 0);
                    SharedPreferences.Editor mPrefEdit = mPref.edit();

                    mPrefEdit.putString("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName);
                    mPrefEdit.putInt("ischart", 1);
                    mPrefEdit.putInt("count", Integer.parseInt(counttt));
                    mPrefEdit.putString("cate", "search");

                    mPrefEdit.commit();
                    refresh_fra();

                    /*
                    Intent intent = new Intent(getActivity(), localplayer.class);
                    intent.putExtra("song", Environment.getExternalStorageDirectory().getPath() + "/.JM/" + fileName).putExtra("ischart", 1).putExtra("count", Integer.parseInt(counttt)).putExtra("cate","search");;
                    startActivity(intent);*/
                }

            }
            //mProgressDialog.dismiss();// Dismiss the progress dialog
        }
    }

    public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;
        private File mFile;

        public SingleMediaScanner(Context context, File f) {
            mFile = f;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mMs.scanFile(mFile.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mMs.disconnect();
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
}
