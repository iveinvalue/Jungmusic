package com.jungcode.jm2.jm2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.widget.PullRefreshLayout;
import com.jungcode.jm2.jm2.MainActivity;
import com.jungcode.jm2.jm2.R;
import com.jungcode.jm2.jm2.localplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;


public class ThreeFragment extends Fragment{
    View view;
    ListView mListView = null;
    ListViewAdapter mAdapter = null;
    Handler handler = new Handler();
    int stopasync;
    File[] files;
    String [] titleList;
    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_three, null);

        //((MainActivity) getActivity()).setTitle("다운로드 목록");

        mListView = (ListView) view.findViewById(R.id.listView);
        //mAdapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        final PullRefreshLayout layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        layout.setRefreshing(false);
                    }
                }, 100);
            }
        });
        layout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);

        Thread thread = new Thread() {
            @Override
            public void run() {
                FilenameFilter fileFilter = new FilenameFilter()
                {
                    public boolean accept(File dir, String name)
                    {
                        return name.endsWith("mp3");
                        //return dir.isDirectory();
                    }
                };

                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/music/jm");
                files = file.listFiles(fileFilter);


                titleList = new String [files.length];
                Arrays.sort(files);

                final List<String> list = Arrays.asList(titleList);

                for(int i = 0 ;i <= files.length-1;i++)
                {
                    titleList[i] = files[i].getPath();

                }
                stopasync = files.length-1;

                handler.postDelayed(new Runnable() {
                    public void run() {
                        //TextView songcount = (TextView) view.findViewById(R.id.songcount);
                        //songcount.setText(String.valueOf(stopasync) + "곡");

                        mAdapter = new ListViewAdapter(getActivity(), R.layout.song_list_view, list);

                        mListView.setAdapter(mAdapter);
                        //mAdapter.notifyDataSetChanged();
                        //Helper.getListViewSize(mListView);
                        view.findViewById(R.id.spin_kit).setVisibility(View.INVISIBLE);
                    }
                }, 1);
            }
        };
        thread.start();

        return view;
    }

    private class ListViewAdapter extends ArrayAdapter<String> {
        List<String> items;
        class ViewHolder {
            TextView mText;
            ImageView mImage;
            TextView mText2;
            int position;
        }
        public ListViewAdapter(Context context, int textViewResourceId, List<String> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView( final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final MediaMetadataRetriever metaRetriver2;
            metaRetriver2 = new MediaMetadataRetriever();


            if(convertView == null){
                LayoutInflater videoInflator = LayoutInflater.from(getContext());
                convertView = videoInflator.inflate(R.layout.song_list_view, null);

                viewHolder = new ViewHolder();
                //viewHolder.position = position;
                viewHolder.mText = (TextView) convertView.findViewById(R.id.mText);
                viewHolder.mText2 = (TextView) convertView.findViewById(R.id.mText2);
                viewHolder.mImage = (ImageView) convertView.findViewById(R.id.nouse);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //



            metaRetriver2.setDataSource(items.get(position));
            viewHolder.mText.setText(metaRetriver2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            viewHolder.mText2.setText(metaRetriver2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

            final View finalConvertView1 = convertView;
            new AsyncTask<ViewHolder, Void, Bitmap>() {
                ViewHolder v = null;
                String title = null;
                String artist = null;
                @Override
                protected void onPreExecute() {

                }
                @Override
                protected Bitmap doInBackground(ViewHolder... params) {
                    v = params[0];

                    metaRetriver2.setDataSource(items.get(position));
                    byte[] art;
                    art = metaRetriver2.getEmbeddedPicture();//앨범아트

                    title = metaRetriver2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    //artist = metaRetriver2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);


                    return BitmapFactory.decodeByteArray(art, 0, art.length);
                }
                @Override
                protected void onPostExecute(Bitmap result) {
                    if(title.contains(v.mText.getText().toString())){
                        v.mImage.setVisibility(View.VISIBLE);
                        v.mImage.setImageBitmap(result);
                    }else{
                        v.mImage.setVisibility(View.INVISIBLE);
                    }
                }
            }.execute(viewHolder);
            //new DownloadAsyncTask().execute(viewHolder);
            Button Button1 = (Button) convertView.findViewById(R.id.button);
            ImageButton imageButton3 = (ImageButton) convertView.findViewById(R.id.imageButton3);

            final View finalConvertView = convertView;

            final ViewHolder finalViewHolder = viewHolder;
            imageButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(getActivity())
                            .content("준비중")
                            .positiveText("확인")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    //File dir = new File(Environment.getExternalStorageDirectory()+"/Music/JM/" + finalViewHolder.mText.getText().toString() + " - " + finalViewHolder.mText2.getText().toString());
                                    //dir.delete();
                                }
                            })
                            .negativeText("취소")
                            .show();
                }

            });

            Button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), mDatee, Toast.LENGTH_LONG).show();
                    //startActivity(new Intent(getActivity(), localplayer.class).putExtra("song", items.get(position)));
                    //finalConvertView.findViewById(R.id.backimg).setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(getActivity(), localplayer.class);
                    intent.putExtra("song", items.get(position)).putExtra("nowplaylist",titleList).putExtra("position",position).putExtra("endposition",stopasync);
                    Pair<View, String> p1 = Pair.create((View) finalConvertView.findViewById(R.id.nouse), "image");
                    Pair<View, String> p2 = Pair.create((View) finalConvertView.findViewById(R.id.mText), "title");
                    Pair<View, String> p3 = Pair.create((View) finalConvertView.findViewById(R.id.mText2), "artist");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), p1, p2, p3);
                    startActivity(intent, options.toBundle());
                }

            });

            return convertView;
        }
    }
}
