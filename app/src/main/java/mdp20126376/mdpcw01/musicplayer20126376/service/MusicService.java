package mdp20126376.mdpcw01.musicplayer20126376.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import mdp20126376.mdpcw01.musicplayer20126376.model.MP3Player;
import mdp20126376.mdpcw01.musicplayer20126376.model.Song;
import mdp20126376.mdpcw01.musicplayer20126376.model.SongsList;


public class MusicService extends Service {

    /*
    This interface is used to listen the changing of music service.
     */
    public interface MusicServiceChangeListenerInterface {
        void mediaPlayerProgressChange(long played, long duration);
        void mediaPlayerSeekToChange();
        void mediaPlayerIsPlaying();
        void mediaPlayerIsPaused();
        void mediaPlayerIsResumed();
        void mediaPlayerStop();
    }


    Uri uri ; //the Uri of the song from another application
    //The current status of the MediaPlayer
    public static MP3Player.MP3PlayerState current_status = MP3Player.MP3PlayerState.STOPPED;

    //Message content for the update progress
    public static final int PROGRESS_UPDATE = 1;

    //Whether the song is from another application
    private static boolean isSongAnotherApplication=false;

    private MusicServiceBinder binder;//music service binder
    private MP3Player player = null;// MP3player
    private MusicNotification musicPlayerNotification;//
    private List<MusicServiceChangeListenerInterface> listenerInterfaceList;
    private static Song currentMusic; // the music playing
    public static MP3Player.MP3PlayerState getCurrent_status() {
        return current_status;
    }

    public static boolean isSongAnotherApplication() {
        return isSongAnotherApplication;
    }

    public static void setSongAnotherApplication(boolean songAnotherApplication) {
        isSongAnotherApplication = songAnotherApplication;
    }

    public static Song getCurrentMusic() {
        return currentMusic;
    }

    public static void setCurrentMusic(Song currentMusic) {
        MusicService.currentMusic = currentMusic;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MusicService", "进入onBind");
        return binder;
    }

    /**
     * the service binder class
     */
    public class MusicServiceBinder extends Binder {

        /**
         * start to play the song in the view list
         */
        public void startPlay() {
            startTheMp3Player(); //play music

            for (MusicServiceChangeListenerInterface listener : listenerInterfaceList) {
                listener.mediaPlayerIsPlaying(); //the state of the media player is playing
            }

            if(handler.hasMessages(MusicService.PROGRESS_UPDATE)){
            handler.removeMessages(MusicService.PROGRESS_UPDATE);}
            handler.sendEmptyMessage(MusicService.PROGRESS_UPDATE);
        }

        /**
         * start to play the song from another application
         * @param it the intent
         */
        public void startPlaySongsFromAnotherApplication(Intent it) throws IOException {

            uri= it.getData(); //obtain the data path of this intent

            startTheMp3PlayerToPlayTheSongFromAnotherApp();
            for (MusicServiceChangeListenerInterface listener : listenerInterfaceList) {
                listener.mediaPlayerIsPlaying(); //the state of the media player is playing
            }
            if(handler.hasMessages(MusicService.PROGRESS_UPDATE)){
                handler.removeMessages(MusicService.PROGRESS_UPDATE);}
            handler.sendEmptyMessage(MusicService.PROGRESS_UPDATE);
        }


        /**
         * pause the player
         */
        public void pausePlay(){
            pause();
            for (MusicServiceChangeListenerInterface listener : listenerInterfaceList) {

                listener.mediaPlayerIsPaused();
            }

        }

        /**
         * resume the player
         */
        public void resumePlay(){

            resume();
            for (MusicServiceChangeListenerInterface listener : listenerInterfaceList) {
                listener.mediaPlayerIsResumed();
            }
        }

        /**
         *  the Seek To action
         * @param ms
         */
        public void seekTo(int ms) {

            player.seekTo(ms);
            current_status= player.getState();
            for (MusicServiceChangeListenerInterface listener : listenerInterfaceList) {
                listener.mediaPlayerSeekToChange();
            }


        }

        /**
         * stop the player
         */
        public void stopPlay(){
            player.stop();
            current_status= player.getState();
            for (MusicServiceChangeListenerInterface listener : listenerInterfaceList) {
                listener.mediaPlayerStop();
            }
            handler.removeMessages(MusicService.PROGRESS_UPDATE);
        }


        public void registerOnStateChangeListener(MusicServiceChangeListenerInterface listener) {
            listenerInterfaceList.add(listener);
        }

        public void unregisterOnStateChangeListener(MusicServiceChangeListenerInterface listener) {
            listenerInterfaceList.remove(listener);
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        listenerInterfaceList = new ArrayList<>();
        player=new MP3Player();
        current_status= player.getState();
        player.setContext(MusicService.this);
        binder = new MusicServiceBinder();
        Log.w("MusicService", "onCreate");
        musicPlayerNotification = new MusicNotification(this);
        musicPlayerNotification.startDisplayNotification(this); //start notification
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("MusicService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("MusicService", "onUnbind");
        return true;
    }

    /**
     * After Unbind the service, when the user come back to the activities, the service will be rebound.
     * @param intent
     */
    @Override
    public void onRebind(Intent intent) {
        Log.e("MusicService", "onRebind");
        super.onRebind(intent);
    }


    @Override
    public void onDestroy() {
        Log.w("MusicService", "onDestroy");
        super.onDestroy();
        player.stop();
        current_status= player.getState();
        musicPlayerNotification.stopPlayerNotification(this);
        SongsList.getSongsList().clear();
        listenerInterfaceList.clear();
        handler.removeMessages(MusicService.PROGRESS_UPDATE);
    }

    /**
     * start the Mp3 player
     */
    public void startTheMp3Player() {
        player.load(MusicService.currentMusic.getDataPath());
        current_status= player.getState();
        musicPlayerNotification.startDisplayNotification(this);
    }

    /**
     * start the Mp3 player to play the song from another application
     */
    public void startTheMp3PlayerToPlayTheSongFromAnotherApp() {
        player.load_out_music(uri);
        current_status= player.getState();
        musicPlayerNotification.startDisplayNotification(this);
    }


    private void pause() {
      player.pause();
      current_status= player.getState();
      musicPlayerNotification.pausePlayerNotification(this);
    }

    private void resume() {
        player.play();
        current_status= player.getState();
        musicPlayerNotification.startDisplayNotification(this);
    }


    /**
     * when the progress of the player is updated, the msg will be send and handled.
     * Then the interface of the activity will be updated
     */
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == MusicService.PROGRESS_UPDATE) {
                long played = player.getProgress();
                long duration = player.getDuration();

                for (MusicServiceChangeListenerInterface listener : listenerInterfaceList) {
                    listener.mediaPlayerProgressChange(played, duration);
                }
                handler.sendEmptyMessage(MusicService.PROGRESS_UPDATE);
            }
        }
    };




}