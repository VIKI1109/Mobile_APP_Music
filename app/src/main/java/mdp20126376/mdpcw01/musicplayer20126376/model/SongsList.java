package mdp20126376.mdpcw01.musicplayer20126376.model;

import java.util.ArrayList;


/**
 * the list to store the songs
 */
public class SongsList {
    private static final ArrayList<Song> songArrayList = new ArrayList<>();//歌曲数据

    public static void addSong(Song song){
        songArrayList.add(song);
    }

    public static ArrayList<Song> getSongsList(){
        return songArrayList;
    }

    /**
     *  check whether the song is in the song list
     * */
    public static boolean isContainSong(String dataPath){
        for (Song mySong : songArrayList) {
            if (mySong.getDataPath().equals(dataPath)) {
                return true;
            }
        }
        return false;
    }
}
