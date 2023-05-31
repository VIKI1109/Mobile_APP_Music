package mdp20126376.mdpcw01.musicplayer20126376.model;


/**
 * Song class
 */
public class Song {
    private String title;
    private String dataPath;

    public Song(
            String title,
            String dataPath
    )
    {
        this.title = title;
        this.dataPath = dataPath;
    }

    public String getTitle() { return this.title; }

    public String getDataPath() { return dataPath; }


}
