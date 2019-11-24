package com.shubham.musicbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class singleSongPage extends AppCompatActivity {

    byte[] cover_image;
    String title;
    String url;

    TextView tittle1;
    ImageView ci;

    MediaPlayer mediaPlayer;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song_page);

        tittle1 = findViewById(R.id.title);
        ci = findViewById(R.id.cover_img);
        seekBar = findViewById(R.id.mainSeekbar);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Bundle bn1 = getIntent().getExtras();

        //cover_image = bn1.getByteArray("cI");
        title = bn1.getString("song");
        url = bn1.getString("urL");

        //Bitmap bmp = BitmapFactory.decodeByteArray(cover_image, 0, cover_image.length);
        tittle1.setText(title);

        //ci.setImageBitmap(bmp);


        try {
            mediaPlayer.setDataSource(url);
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
}
