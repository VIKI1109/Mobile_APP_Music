package mdp20126376.mdpcw01.musicplayer20126376.model;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {
    private static Context context;

    /*Called when the activity is first created*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("BaseActivity",getClass().getSimpleName()+"onCreate");
        ActivityCollector.addActivity(this);
    }
    /*Called when an activity changes from invisible to visible*/
    @Override
    protected void onStart(){
        super.onStart();
        Log.w("BaseActivity",getClass().getSimpleName()+"onStart");
    }
    /*Called when the activity is ready to interact with the user*/
    @Override
    protected void onResume(){
        super.onResume();
        Log.w("BaseActivity",getClass().getSimpleName()+"onResume");
    }
    /*The system calls when it is ready to start or resume another activity*/
    @Override
    protected void onPause(){
        super.onPause();
        Log.w("BaseActivity",getClass().getSimpleName()+"onPause");
    }
    /*Called when the activity is completely invisible*/
    @Override
    protected void onStop(){
        super.onStop();
        Log.w("BaseActivity",getClass().getSimpleName()+"onStop");
    }
    /*Called before the activity is destroyed*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w("BaseActivity",getClass().getSimpleName()+"onDestroy");
        ActivityCollector.removeActivity(this);//remove oneself from the activity manager
    }

    @Override
    protected void onRestart(){
        Log.w("BaseActivity",getClass().getSimpleName()+"onRestart");
        super.onRestart();
    }

    @Override
    protected  void  onNewIntent(Intent intent) {
        super .onNewIntent(intent);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.w("BaseActivity", getClass().getSimpleName()+"onBackPressed");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.w("BaseActivity", getClass().getSimpleName()+"onSaveInstanceState");
    }
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.w("BaseActivity", getClass().getSimpleName()+"onRestoreInstanceState");
    }
    public static Context getContext() {
        return context;
    }


}
