package mdp20126376.mdpcw01.musicplayer20126376.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


import mdp20126376.mdpcw01.musicplayer20126376.R;
import mdp20126376.mdpcw01.musicplayer20126376.model.Song;

/**
 *  the song list adapter
 */
public class SongListAdapter extends ArrayAdapter<Song> {
    private final int Id;//用来放置布局文件的id


    public SongListAdapter(Context context, int Id, List<Song> objects) {
        super(context, Id, objects);
        this.Id = Id;
    }

    static class ViewHolder {
        TextView songName; //the only thing to show in the view list
    }

    /**
     * Called when each subItem is scrolled into the screen
     * @param position the song position in the list
     * @param convertView check whether it is the first time to upload
     * @param parent the ViewGroup
     * @return the View
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v;//Child layout object
        ViewHolder viewHolder;
        Song song = getItem(position); //obtain the current song in the list

        if (convertView != null) {//not the first load, that is, the layout file is loaded and ready to use
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();// retrieve the ViewHolder

        } else {
            //if it is the first time to upload
            viewHolder = new ViewHolder();
            v = LayoutInflater.from(getContext()).inflate(Id, parent, false);
            viewHolder.songName = v.findViewById (R.id.song_name);
            v.setTag(viewHolder);
        }
        //set the text of song name and the text style.
        if(song!=null && viewHolder!=null){
            viewHolder.songName.setText(song.getTitle());
            viewHolder.songName.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return v;
    }
}
