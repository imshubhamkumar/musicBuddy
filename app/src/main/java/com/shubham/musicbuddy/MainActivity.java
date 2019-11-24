package com.shubham.musicbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shubham.musicbuddy.adapter.playListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements playListAdapter.SongItemClickListner {

    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    public static Context context;

    ImageView bottom_Img;
    TextView playing;
    ImageView play_btn;
    SeekBar seekBar;
    TextView urlHolder;

    RelativeLayout bottom_player;


     MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.playList);
        bottom_Img = findViewById(R.id.bottom_img);
        playing = findViewById(R.id.playing_song_name);
        play_btn = findViewById(R.id.play_btn);
       seekBar = findViewById(R.id.play_seekbar);
       bottom_player = findViewById(R.id.bottom_player);

       urlHolder = findViewById(R.id.urlHolder);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        recyclerView.setHasFixedSize(true);
        context = getApplicationContext();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        songsData sd = new songsData();
        sd.execute("http://starlord.hackerearth.com/studio");


        bottom_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView songname = v.findViewById(R.id.playing_song_name);
                ImageView cI = v.findViewById(R.id.bottom_img);
                TextView songUrl = v.findViewById(R.id.urlHolder);


                BitmapDrawable drawable = (BitmapDrawable) cI.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();

                Intent gotoSolo = new Intent(MainActivity.this, singleSongPage.class);

                gotoSolo.putExtra("song", songname.getText());
                //gotoSolo.putExtra("ci", baos.toByteArray());
                gotoSolo.putExtra("urL", songUrl.getText());

                startActivity(gotoSolo);
            }
        });


        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    play_btn.setImageResource(R.drawable.pause_btn);
                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
                } else {
                    mediaPlayer.pause();
                    play_btn.setImageResource(R.drawable.play_btn);
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                }
            }
        });


        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            mSeekbarUpdateHandler.postDelayed(this, 50);
        }
    };

    @Override
    public void onTapToPlay(View view) {
        TextView songUrl = view.findViewById(R.id.songUrl);
        ImageView getImg = view.findViewById(R.id.songCoverImage);
        TextView getSong = view.findViewById(R.id.song_name);
        BitmapDrawable drawable = (BitmapDrawable) getImg.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        bottom_Img.setImageBitmap(bitmap);
        playing.setText(getSong.getText());
        play_btn.setImageResource(R.drawable.pause_btn);
        urlHolder.setText(songUrl.getText());
        bottom_player.setVisibility(View.VISIBLE);
        try {
            mediaPlayer.setDataSource((String) songUrl.getText());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    public class songsData extends AsyncTask<String, Void, String> {
        private String result;
        private List<String> songName;
        private List<String> songUrl;
        private List<String> artists;
        private List<String> coverImage;
        JSONArray JA;
        @Override
        protected String doInBackground(String... urls) {

            result ="";
            URL link;

            String url = urls[0];
            BufferedReader reader;

            try {
                link = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) link.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");

                }
                result = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            songName = new ArrayList<>();
            songUrl = new ArrayList<>();
            artists = new ArrayList<>();
            coverImage = new ArrayList<>();
            try {
                JA = new JSONArray(result);
                for (int i = 0; i <JA.length(); i++){
                    JSONObject Jo = (JSONObject) JA.get(i);
                    String song = Jo.getString("song");
                    String song_url = Jo.getString("url");
                    String artists_name = Jo.getString("artists");
                    String cover_Image = Jo.getString("cover_image");



                    songName.add(song);
                    songUrl.add(song_url);
                    artists.add(artists_name);
                    coverImage.add(cover_Image);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MainActivity.adapter = new playListAdapter(songName, songUrl, artists, coverImage, MainActivity.this, MainActivity.this);
            MainActivity.recyclerView.setAdapter(adapter);
        }

    }

}
