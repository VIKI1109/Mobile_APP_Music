package mdp20126376.mdpcw01.musicplayer20126376.manager;


import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Iterator;

import mdp20126376.mdpcw01.musicplayer20126376.utils.SharedPreferencesUtils;

public class ColorChangeManager {

    public final static String COLOR = "themeColor";

    private static ColorChangeManager colorChangeManager;
    private Context mContext;
    private ArrayList<IColorChangeListener> iColorChangeListeners;

    public static int[] BackgroundColor = {Color.rgb(30,144,255),
            Color.rgb(119,136,153),
            Color.rgb(147,112,219),
            Color.rgb(255,69,0),
            Color.rgb(105, 200, 78),
            Color.rgb(127,255,0),
            Color.rgb(161, 161, 161),
            Color.rgb(255,182,193),
            Color.rgb(100,149,237),
            Color.rgb(0,206,209),
            Color.rgb(255,255,224),}; ///the color can be chose

    private ColorChangeManager(Context context) {
        this.mContext = context;
    }

    public static ColorChangeManager manage(Context context) {
        if (colorChangeManager == null) {
            colorChangeManager = new ColorChangeManager(context);
        } else {
            colorChangeManager.mContext = context;
        }

        return colorChangeManager;
    }

    /**
     * The current color of this app that the user have chosen.
     * @return the color id
     */
    public int getColor() {
        return SharedPreferencesUtils.getColorID(mContext, COLOR, BackgroundColor[0]);
    }


    public void rememberTheColor(int index) {
        SharedPreferencesUtils.setColorID(mContext, COLOR, BackgroundColor[index]);
        colorChange();
    }


    public void colorChange() {
        if (iColorChangeListeners == null) return;
        int color = getColor();
        Iterator<IColorChangeListener> iterator = iColorChangeListeners.iterator();

        //change all the activity interfaces which have set the listener
        while (iterator.hasNext()) {
            IColorChangeListener next = iterator.next();
            if (next == null) {
                iterator.remove();
            } else {
                next.onColorBackgroundChange(color);//change the color
            }
        }
    }

    /**
     * the theme change listener interface
     */
    public interface IColorChangeListener {
        void onColorBackgroundChange(int color);
    }

    /**
     * register the color change listener and add them into the listener list.
     * @param listener
     */
    public void registerColorChangeListener(IColorChangeListener listener) {
        if (iColorChangeListeners == null) {
            iColorChangeListeners = new ArrayList<>();
        }
        iColorChangeListeners.add(listener);
    }

}
