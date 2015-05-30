package com.johnnymolina.ImgurWorkout.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.johnnymolina.ImgurWorkout.R;
import com.nanotasks.Tasks;

import java.io.File;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;


public class BaseActivity extends ActionBarActivity{

    //Declare Titles And Icons in their respective Arrays For Our Navigation Drawer List View
    String TITLES[] = {"Albums","Log","Import","Settings","Tutorial","About"};
    int ICONS[] = {R.drawable.ic_grid,R.drawable.ic_playlist_add,R.drawable.fab_import_white,R.drawable.ic_settings,R.drawable.ic_help, R.drawable.ic_about};
    public CharSequence defaultTitle = "IMGUR WORKOUT";


    String ttsPreference = "TTS_PREFERENCE";
    SharedPreferences settings;
    boolean ttsValue;



    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String TITLE = "Imgur Workout";
    String TITLE2 = "Your Custom Workout App";


    protected Toolbar toolbar;              // Declaring the Toolbar Object
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        settings = getSharedPreferences( ttsPreference, MODE_PRIVATE);
        ttsValue = settings.getBoolean(ttsPreference,true);



        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

        }



        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new BaseActivityAdapter(TITLES,ICONS, TITLE, TITLE2);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view

        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }





        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle

        mDrawerToggle.syncState();
    }

public void closeDrawer(){
    Drawer.closeDrawers();
}


    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer();

    }

    /**Called to start the Playlist **/
    public void goToImgurImportActivity(View view){
        Intent intent = new Intent(this,Imgur.class);
        startActivity(intent);
    }







    public void switchTts(boolean b){

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(ttsPreference, b).commit();
        editor.commit();
        ttsValue = settings.getBoolean(ttsPreference,true);



    }

    public void deleteImageCache(){

        Context context = getBaseContext();

        Tasks.executeInBackground(context, new BackgroundWork<String>() {
            @Override
            public String doInBackground() throws Exception {

                Glide.get(getApplicationContext()).clearDiskCache();

                return "Temp Cache Deleted";
            }
        }, new Completion<String>() {
            @Override
            public void onSuccess(Context context, String result) {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Context context, Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    public void deleteCache()
    {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        }
    }

    public static boolean deleteDir(File dir)
    {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


}
