package mdp20126376.mdpcw01.musicplayer20126376.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import mdp20126376.mdpcw01.musicplayer20126376.R;
import mdp20126376.mdpcw01.musicplayer20126376.model.BaseActivity;
import mdp20126376.mdpcw01.musicplayer20126376.manager.ColorChangeManager;


/**
 * This class is for changing the theme of this application
 */
public class ColorChangeActivity extends BaseActivity implements ColorChangeManager.IColorChangeListener,View.OnClickListener {


    private Toolbar toolbar;
    private LinearLayout colorSelection;
    private Button changeConfirmation;
    int color=0;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_theme_change);

        toolbar = findViewById(R.id.toolbar_activity_color);
        setSupportActionBar(toolbar);

        //back to the main activity
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ColorChangeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ColorChangeManager.manage(this).registerColorChangeListener(this);//set the listener of changing the theme

        toolbar.setBackgroundColor(ColorChangeManager.manage(this).getColor());


        colorSelection =  findViewById(R.id.colorselection);
        changeConfirmation= findViewById(R.id.changeOK);

        createTheColorPanel();

        changeConfirmation.setOnClickListener(view -> {
            ColorChangeManager.manage(this).rememberTheColor(color);
        });

    }

    /**
     * create the color panel
     */
    public void createTheColorPanel(){

        for (int i = 0; i < ColorChangeManager.BackgroundColor.length; i++) {
            View view = new View(this);//new View for certain color
            view.setBackgroundColor(ColorChangeManager.BackgroundColor[i]);


            int dp = (int) dpToPx(this, 80);
            int margin_vertical = (int) dpToPx(this, 10);
            int margin_horizontal = (int) dpToPx(this, 6);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp, dp);
            layoutParams.setMargins(margin_vertical, margin_vertical, margin_horizontal, margin_horizontal);

            view.setOnClickListener(this);//add the click listener
            colorSelection.addView(view, layoutParams);//add view
        }

    }


    @Override
    public void onClick(View view) {

//click the color panel to select the color
        for (int i = 0; i < colorSelection.getChildCount(); i++) {
            if (view == colorSelection.getChildAt(i)) {
                color=i;

                Toast toast= Toast.makeText(ColorChangeActivity.this, "Click the button to confirm the change", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                break;
            }
        }

    }


    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    /**
     * change the background
     * @param color the current color
     */
    @Override
    public void onColorBackgroundChange(int color) {
        toolbar.setBackgroundColor(color);
    }



    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(ColorChangeActivity.this, "Orientation Portrait", Toast.LENGTH_SHORT).show();
        }

        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(ColorChangeActivity.this, "Orientation Landscape", Toast.LENGTH_SHORT).show();
        }



    }
}










