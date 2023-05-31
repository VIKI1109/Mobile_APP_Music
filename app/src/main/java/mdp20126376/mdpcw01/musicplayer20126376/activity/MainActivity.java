package mdp20126376.mdpcw01.musicplayer20126376.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;


import mdp20126376.mdpcw01.musicplayer20126376.R;
import mdp20126376.mdpcw01.musicplayer20126376.model.ActivityCollector;
import mdp20126376.mdpcw01.musicplayer20126376.model.BaseActivity;
import mdp20126376.mdpcw01.musicplayer20126376.model.MP3Player;
import mdp20126376.mdpcw01.musicplayer20126376.model.Song;
import mdp20126376.mdpcw01.musicplayer20126376.model.SongsList;
import mdp20126376.mdpcw01.musicplayer20126376.service.MusicService;
import mdp20126376.mdpcw01.musicplayer20126376.adapter.SongListAdapter;
import mdp20126376.mdpcw01.musicplayer20126376.manager.ColorChangeManager;


public class MainActivity extends BaseActivity implements ColorChangeManager.IColorChangeListener, View.OnClickListener {

    /*widgets*/

    private MusicService.MusicServiceBinder serviceBinder;
    private RelativeLayout info_bar =null; //the playing song information bar
    private RelativeLayout bottomBar =null;//the bottom bar
    private LinearLayout seek_column=null;
    private Toolbar toolbar = null;
    private ListView songListVIew = null;
    private SongListAdapter listViewAdapter;//the song list view adapter
    private SeekBar seekBar = null;
    private Button preference=null;
    private TextView songName = null;
    private ImageButton playOrPauseButton = null;
    private Button stop_btn = null;
    private TextView duration_text, playProgressText;
    private static int numberOfSongs = 0; // the number of the songs in the list
    private MP3Player.MP3PlayerState current_status;//the current status of the mp3player
    private final int REQ_READ_EXTERNAL_STORAGE = 1;
    private boolean isBackKey=false; //whether click the back key
    private boolean isServerUnbind=false; //whether the binder is unbound
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display);

        loadWidget();//load the widgets

        setSupportActionBar(toolbar);

        processExtraData();//check the intent

        Intent intentService = new Intent(MainActivity.this, MusicService.class);
        startService(intentService);//start the service
        bindService(intentService, serviceConnection, BIND_AUTO_CREATE);//bind the service

        requestPermission();//dynamic request the permission
        songListViewAdapter(); //set the song view list adapter


        //the song list listener
        songListVIew.setOnItemClickListener((parent, view, position, id) -> {
            if(MusicService.getCurrent_status()== MP3Player.MP3PlayerState.PLAYING){
                serviceBinder.stopPlay();
            }
            MusicService.setSongAnotherApplication(false);//the song is not from outside
            Song music = SongsList.getSongsList().get(position);
            MusicService.setCurrentMusic(music);
            serviceBinder.startPlay();
        });


        clickEventListener();


        //seek bar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    serviceBinder.seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                serviceBinder.pausePlay();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                serviceBinder.resumePlay();
            }
        });

        
        ColorChangeManager.manage(this).registerColorChangeListener(this);
        //set the background theme
        toolbar.setBackgroundColor(ColorChangeManager.manage(this).getColor());
        info_bar.setBackgroundColor(ColorChangeManager.manage(this).getColor());
        bottomBar.setBackgroundColor(ColorChangeManager.manage(this).getColor());
        seek_column.setBackgroundColor(ColorChangeManager.manage(this).getColor());

    }


    /**
     * when the user come back to the application, the interface will be updated
     * including the song name and the control play button.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();


        current_status=MusicService.getCurrent_status();


        if(current_status!=MP3Player.MP3PlayerState.STOPPED) {

            //update the song name
            if(!MusicService.isSongAnotherApplication()) {
                if (numberOfSongs != 0) {
                    songName.setText(MusicService.getCurrentMusic().getTitle());
                }
            }else{
                songName.setText(getString(R.string.songNameText));}

            //update the control button
            if (current_status == MP3Player.MP3PlayerState.PAUSED) {

                playOrPauseButton.setBackground(getDrawable(R.drawable.pause_button));
            } else {

                playOrPauseButton.setBackground(getDrawable(R.drawable.play_button));
            }

        }
        
    }


    /**
     * Change the theme color.
     * @param color the theme color
     */
    @Override
    public void onColorBackgroundChange(int color) {
        toolbar.setBackgroundColor(color);
        info_bar.setBackgroundColor(color);
        bottomBar.setBackgroundColor(color);
        seek_column.setBackgroundColor(color);
    }

    @Override
    public void onRestart() {
            super.onRestart();

    }

    /**
     * If Activity is already in the task stack,
     * start Activity again, then the onNewIntent() method will be called at this time.
     * Jump to MainActivity—> onNewIntent—> onRestart—> onStart—>onResume
     * @param intent the intent 
     */
    @Override
    protected  void  onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        setIntent(intent);

        processExtraData();

    }

    /**
     * process the data from the intent
     */
    private  void  processExtraData(){

        intent = getIntent();

        if(intent.getData()!=null) {
            //deal with playing the song of other application in this Main Activity.
            if (intent.getAction().equals("android.intent.action.VIEW")) {
                try {
                    if(serviceBinder!=null){
                    serviceBinder.stopPlay(); //stop play the current music
                    MusicService.setSongAnotherApplication(true); // set the songAnotherApplication to true.
                    serviceBinder.startPlaySongsFromAnotherApplication(intent); //play this song.
                    }
                 else{
                       isServerUnbind=true; // if the activity is destroyed and the serverBinder is null
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * User return to the activity
     * the activity is ready for being used by users.
     */
    @Override
    protected void onResume() {

        super.onResume();

        listViewAdapter.notifyDataSetChanged();
    }

    /**
     * Activity is stopping
     */
    @Override
    protected void onPause() {

        super.onPause();
    }

    /**
     * when the activity is ready to back to the background, the OnStop() will be called.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Destroy the activity.
     * if the onDestroy() is called after the OnBackPressed(), then the activities will be destroy and the service won't be stopped.
     * else the activity and service will be destroyed both.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        ActivityCollector.finishAllActivities();//finish all the activities

        if(serviceConnection != null){
        unbindService(serviceConnection);}

            if (!isBackKey) {
                stopService(new Intent(this, MusicService.class));
                MusicService.setSongAnotherApplication(false);
            }
            else{
                isBackKey=false;
            }

    }

    /**
     * when the user press the back button, this method will be called
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackKey=true;
        ActivityCollector.finishAllActivities();
    }

    /**
     * load the widgets
     */
    public void loadWidget() {
        preference=findViewById(R.id.preference);
        toolbar = findViewById(R.id.toolbar_activity_display);
        songListVIew = findViewById(R.id.songList);
        bottomBar = findViewById(R.id.bottomBar);
        songName = findViewById(R.id.songName);
        info_bar =findViewById(R.id.info_bar);
        playOrPauseButton = findViewById(R.id.play_btn);
        duration_text = findViewById(R.id.duration_text);
        playProgressText = findViewById(R.id.progress);
        seekBar =findViewById(R.id.seekBarContainer);
        seek_column= findViewById(R.id.seekBar);
        stop_btn = findViewById(R.id.stop_btn);
    }

    /**
     * Load the songs from the SDCards
     */
    private void loadSongsFromDeviceSDcards() {


        File musicDir = new File(
                Environment.getExternalStorageDirectory().getPath() + "/Music/");
        File[] file = musicDir.listFiles();

        if (file != null) {
            for (File f : file) {
                String[] path= f.getPath().split("/");
                String title= path[path.length-1];
                String dataPath= f.getPath();
                Song song = new Song(title, dataPath);
                if(SongsList.isContainSong(dataPath)) {
                    continue;
                }
                SongsList.addSong(song);
                numberOfSongs++;
            }
        }
        if (numberOfSongs == 0) {
            Toast.makeText(MainActivity.this, "No audio locally, download it from browser.", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     *  set the button click listener
     */
    public void clickEventListener() {

        current_status=MusicService.getCurrent_status();

        //click the control button to play or pause the music
        playOrPauseButton.setOnClickListener(view -> {
            if (numberOfSongs != 0) {
               //check the status of the MP3 player
                switch (current_status) {
                    case PLAYING:
                        serviceBinder.pausePlay();
                        break;
                    case PAUSED:
                        serviceBinder.resumePlay();
                        break;
                    case STOPPED: //if the status is stopped, ask the usr to choose one music in the list.
                        Toast.makeText(MainActivity.this, "Choose one music in the song list please!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            } else {
                Toast.makeText(MainActivity.this, "No audio locally, please download it from browser", Toast.LENGTH_SHORT).show();
            }
        });

        //click the "appearance" button to move to theme change activity
        preference.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, ColorChangeActivity.class);
            startActivity(intent);
        });

        //click the "stop" button to stop the music
        stop_btn.setOnClickListener(view -> serviceBinder.stopPlay());

    }

    /**
     * The song view list adapter
     */
    public void songListViewAdapter() {
        listViewAdapter = new SongListAdapter(MainActivity.this, R.layout.song_list, SongsList.getSongsList());
        songListVIew.setAdapter(listViewAdapter);

    }

    /**
     * Connect with the background service
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (MusicService.MusicServiceBinder) service;
            serviceBinder.registerOnStateChangeListener(musicServiceChangeListenerInterface);


            if(isServerUnbind){
                serviceBinder.stopPlay();
                MusicService.setSongAnotherApplication(true);
                try {
                    serviceBinder.startPlaySongsFromAnotherApplication(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBinder.unregisterOnStateChangeListener(musicServiceChangeListenerInterface);
        }
    };

    /**
     * listen for the change of the music service
     */

    private final MusicService.MusicServiceChangeListenerInterface musicServiceChangeListenerInterface = new MusicService.MusicServiceChangeListenerInterface() {


        /**
         * When the progress of MP3player changes, the text of duration and progress will change.
         * @param played the current progress
         * @param duration  the current song duration
         */
        @Override
        public void mediaPlayerProgressChange(long played, long duration) {
            seekBar.setMax((int) duration);
            duration_text.setText(durationTimeToTextString((int) duration));
            seekBar.setProgress((int) played);
            playProgressText.setText(durationTimeToTextString((int) played));
        }


        @Override
        public void mediaPlayerSeekToChange() {}


        /**
         * When MP3player is playing, change the pause image button to play image button
         * And if the song is from other application, set the song name to "Song from Another Application"
         * Otherwise, set the text of the song name to the title of this song in the song list.
         */

        @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void mediaPlayerIsPlaying() {

            Log.w("DisplayActivity", "STATUS_PLAYING");
            playOrPauseButton.setBackground(getDrawable(R.drawable.play_button));//change the image of button


            //if no song in the song list is playing and no song from another app is playing
            if(MusicService.getCurrentMusic()==null&&!MusicService.isSongAnotherApplication()){
                songName.setText(getString(R.string.noSong));
            }

            //if no song in the song list is playing and the song from another app is playing
            if(MusicService.getCurrentMusic()==null&&MusicService.isSongAnotherApplication()){
                songName.setText(getString(R.string.songNameText));
            }

            //if the song from other application is playing and the song in the song list are ready to play.
            if(MusicService.getCurrentMusic()!=null&&MusicService.isSongAnotherApplication()) {
                songName.setText(getString(R.string.songNameText));
            }

            //if no song from other application is playing and the song in the song list is playing.
            if(MusicService.getCurrentMusic()!=null&&!MusicService.isSongAnotherApplication()){
            songName.setText(MusicService.getCurrentMusic().getTitle());
            }
            current_status = MusicService.getCurrent_status();

        }


        /**
         * When the player is paused, set the play button to pause button
         */

        @SuppressLint("UseCompatLoadingForDrawables")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void mediaPlayerIsPaused() {
            Log.w("DisplayActivity", "STATUS_PAUSED");
            playOrPauseButton.setBackground(getDrawable(R.drawable.pause_button));
            current_status = MusicService.getCurrent_status();

        }

        /**
         *When the player resume to play, set the pause button to play button
         */

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void mediaPlayerIsResumed() {
            Log.w("DisplayActivity", "STATUS_PLAYING");
            playOrPauseButton.setBackground(getDrawable(R.drawable.play_button));
            current_status = MusicService.getCurrent_status();
        }


        /**
         * When the player is stopped, reset the duration text and progress text to 0.
         *
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void mediaPlayerStop() {
            current_status = MusicService.getCurrent_status();
            playOrPauseButton.setBackground(getDrawable(R.drawable.pause_button));
            songName.setText(" ");
            seekBar.setProgress(0);// set the progress to 0
            playProgressText.setText(getString(R.string.timeText));
            duration_text.setText(getString(R.string.timeText));
        }

    };


    /**
     * Change the time to string type 0:00
     */

    public String durationTimeToTextString(int duration){
        int durationTosecond = duration / 1000;
        int durationTominute = durationTosecond / 60;
        int second = durationTosecond % 60;
        StringBuilder stringBuilder = new StringBuilder();
        if(durationTominute < 10)
        {stringBuilder.append(0);}
        stringBuilder.append(durationTominute);
        stringBuilder.append(':');
        if(second < 10)
        {  stringBuilder.append(0);}
        stringBuilder.append(second);
        return stringBuilder.toString();
    }

    /**
     * Request the permission from users
     */
    public void requestPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            int checkReadStoragePermission = ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkReadStoragePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, REQ_READ_EXTERNAL_STORAGE);
            } else {
                loadSongsFromDeviceSDcards();//已有权限,加载歌曲
            }
        }
    }

    /**
     * A callback after requesting permission from a user
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (numberOfSongs == 0) {
                    loadSongsFromDeviceSDcards();
                }
                listViewAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "Failed to apply for read/write storage permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * The method used to change the orientation of the interface
     * @param newConfig the configuration of the phone
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(MainActivity.this, "Orientation Portrait", Toast.LENGTH_SHORT).show();
        }

        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(MainActivity.this, "Orientation Landscape", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onClick(View view) {

    }
}
