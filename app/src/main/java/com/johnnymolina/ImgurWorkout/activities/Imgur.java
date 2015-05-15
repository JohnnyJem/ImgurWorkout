package com.johnnymolina.ImgurWorkout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

import io.realm.Realm;

public class Imgur extends BaseActivity {

    public static final String TAG = Imgur.class.getName();


    protected FrameLayout parent;
    protected RelativeLayout imgurList;
    protected ImageButton playlistSubmitButton;
    protected EditText editText;
    protected JsonObject album;
    protected String imgurImportLink = "";

    /* --------------progress bar declarations---------*/
    View fabImportToDatabase;
    LinearLayout downloadLayout;
    TextView downloadInProgress;
    TextView downloadCount;
    ProgressBar progressBar;
    Future<File> downloading;
    int count = 1;
    String filePath;
    /* ------------------------------------------------*/

    // adapter that holds imgur json object, obviously :)
    ArrayAdapter<JsonObject> imgurAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup of our layout
        parent = (FrameLayout) findViewById(R.id.placeholder);
        imgurList = (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.imgur_list, null);
        parent.addView(imgurList);

        /*------------------------------------OFFLINE DOWNLOAD SETUP BEGIN--------------------------*/
        fabImportToDatabase = findViewById(R.id.fab_import_to_database);
        downloadLayout = (LinearLayout) findViewById(R.id.download_layout);
        downloadLayout.setVisibility(View.INVISIBLE);
        downloadInProgress = (TextView)findViewById(R.id.download_progress);
        downloadCount = (TextView)findViewById(R.id.download_count);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        fabImportToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (downloading != null && !downloading.isCancelled()) {
                    resetDownload();
                    return;
                }
                */
                filePath = "null";
                download(fabImportToDatabase);
            }
        });
 /*--------------------------------OFFLINE DOWNLOAD SETUP FINISHED--------------------------*/



        //our submit button
        playlistSubmitButton = (ImageButton) findViewById(R.id.playlist_submit_button);
        playlistSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitImgurID();
            }
        });

        //our EditTextBox with an OnKey Listener for "enter" softKeypad event.
        editText = (EditText) findViewById(R.id.import_Img_Link);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            submitImgurID();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        // Setup of our adapter for our list view
        imgurAdapter = new ArrayAdapter<JsonObject>(this, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.imgur_row, null);

                // grab the image object
                JsonObject image = getItem(position);

                // get the image id to load the thumbnail
                String imageUrl = image.get("id").getAsString().replace("[()]", "");
                String imageThumbnailUrl = "http://i.imgur.com/" + imageUrl + "t.png";

                ImageView imageView = (ImageView)convertView.findViewById(R.id.image);

                //start with Glide and load the imageThumbnail into the imageView
                Glide.with(getContext())
                        .load(imageThumbnailUrl)
                        .placeholder(R.drawable.imageplaceholder)
                        .error(R.drawable.imageplaceholder)
                        .into(imageView);


                // Set the Title and Description TextViews
                // first checking if the json object has a value for title and description
                TextView title = (TextView)convertView.findViewById(R.id.imgur_img_title);
                if(image.get("title").isJsonNull()) {
                    title.setText("");
                }else{
                    title.setText(image.get("title").getAsString());
                }

                TextView description = (TextView)convertView.findViewById(R.id.imgur_img_description);
                if(image.get("description").isJsonNull()) {
                    description.setText("");
                }else{
                    description.setText(image.get("description").getAsString());
                }

                return convertView;
            }
        };

        //Now that our Listview adapter is setup we initialize our Listview & Adapter
        ListView listView = (ListView)findViewById(R.id.imgur_list);
        listView.setAdapter(imgurAdapter);
    }

    void resetDownload(String filePathString) {
        // cancel any pending download
         filePath= filePathString;
        if (downloading != null && !downloading.isCancelled()) {
            downloading.cancel();
            downloading = null;

            // downloadCount.setText(null);
            progressBar.setProgress(0);
            return;
        }

    }


    public void submitImgurID(){
        imgurImportLink = editText.getText().toString();
        //attempts to loads the JSON album object
        load();
        //forces softkeyboard to close on submission
        InputMethodManager imm = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
    }


    //The following method Loads the IMGUR album Object with ION and uses GSON to read the JSONOBJECTS fields
    //and adds the Album's array of Image Json Objects into our listview
    Future<JsonObject> loading;
    private void load() {
        if (loading != null && !loading.isDone() && !loading.isCancelled())
            return;

        final String enteredUrl = "https://api.imgur.com/3/album/"+imgurImportLink+".json";
        final String CLIENTID = "3b78168400c66fd";
        // This request loads a URL as JsonObject and invokes
        // a callback on completion.


        // This request loads a URL as JsonObject and invokes
        // a callback on completion.
        loading = Ion.with(this)
                .load(enteredUrl)
                .setLogging("ION_VERBOSE_LOGGING", Log.VERBOSE)
                .setHeader("Authorization", "Client-ID " + CLIENTID)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        // this is called back onto the ui thread, no Activity.runOnUiThread or Handler.post necessary.

                        if (e != null) {
                            Toast.makeText(Imgur.this, "Error loading images", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Imgur.this, enteredUrl, Toast.LENGTH_LONG).show();

                            return;
                        }

                        album = result.getAsJsonObject("data");

                        if (album.has("images")) {

                            JsonArray images = album.getAsJsonArray("images");

                            playlistSubmitButton.setVisibility(View.INVISIBLE);

                            // add the image Json objects to our adapter
                            for (int i = 0; i < images.size(); i++) {
                                if (images.get(i).isJsonNull()) {
                                    break;
                                } else {
                                    imgurAdapter.add(images.get(i).getAsJsonObject());
                                    fabImportToDatabase.setVisibility(View.VISIBLE);
                                }
                            }

                        } else {
                            Toast.makeText(Imgur.this, "Failed to connect. " +
                                    "Please try again." + "Make sure capitalization is correct", Toast.LENGTH_LONG).show();
                        }


                    }
                });

    }


    //Uses the downloaded Album JSONOBJECT and its Image JSON Object array
    // and assigns its fields to a Realm Album Objects and its associated Image objects.
    //The Realm object is then saved to the Realm database.
    public void download(View view){
        downloadLayout.setVisibility(View.VISIBLE);
        downloadLayout.setOrientation(LinearLayout.VERTICAL);
        View fab = findViewById(R.id.fab_import_to_database);
        View goNext = findViewById(R.id.fab_go_to_library);
        if (fab.getVisibility() == View.VISIBLE) {
            // Its visible and so objects have loaded into the adapter.
            // Open the default realm. All threads must use it's own reference to the realm.
            // Those can not be transferred across threads.
            Realm realm = Realm.getInstance(this);
            realm.beginTransaction();
            ImgurAlbum realmImgurAlbum = realm.createObject(ImgurAlbum.class);
            //generate unique album Identifier that will conect Albums and its images.
            String albumUnique = nextSessionId();
            realmImgurAlbum.setId(album.get("id").toString().replace("\"","") + albumUnique );
            realmImgurAlbum.setTitle(album.get("title").toString().replace("\"",""));
            realmImgurAlbum.setLink(album.get("link").toString().replace("\"",""));
            String albumDescription =album.get("description").toString().replace("\\n", "");
            realmImgurAlbum.setDescription(albumDescription.replace("\"",""));

            for (int i = 0; i < imgurAdapter.getCount(); i++) {
                ImgurImage realmImgurImage = realm.createObject(ImgurImage.class);
                realmImgurImage.setId(imgurAdapter.getItem(i).get("id").toString().replace("\"", ""));
                realmImgurImage.setTitle(imgurAdapter.getItem(i).get("title").toString().replace("\"", ""));
                String imageDescription = imgurAdapter.getItem(i).get("description").toString().replace("\\n", "");
                realmImgurImage.setDescription(imageDescription.replace("\"", ""));
                realmImgurImage.setLink(imgurAdapter.getItem(i).get("link").toString().replace("\"", ""));
                realmImgurImage.setAlbum(album.get("id").toString().replace("\"", "") + albumUnique);
                realmImgurAlbum.getImages().add(realmImgurImage);

                //Here we download the image and set the sysLink where the image has been downloaded.
                // Save the download as a local file, named the same as in the URL
               final String filename = imgurAdapter.getItem(i).get("link").toString().replace("\"", "").substring(imgurAdapter.getItem(i).get("link").toString().replace("\"", "").lastIndexOf('/') + 1);

                final int totalcount = imgurAdapter.getCount();

                downloading = Ion.with(getBaseContext())
                        .load(imgurAdapter.getItem(i).get("link").toString().replace("\"", ""))
                                // attach the percentage report to a progress bar.
                                // can also attach to a ProgressDialog with progressDialog.
                        .progressBar(progressBar)
                                // callbacks on progress can happen on the UI thread
                                // via progressHandler. This is useful if you need to update a TextView.
                                // Updates to TextViews MUST happen on the UI thread.
                        .progressHandler(new ProgressCallback() {
                            @Override
                            public void onProgress(long downloaded, long total) {downloadCount.setText("" + downloaded + " / " + total + "   ");
                              }
                        })
                                // write to a file
                        .write(getFileStreamPath(filename))
                                // run a callback on completion
                        .setCallback(new FutureCallback<File>() {

                            @Override
                            public void onCompleted(Exception e, File result) {
                                downloadInProgress.setText("" + count + " / " + totalcount + "   ");

                                if (e != null) {
                                    Toast.makeText(getBaseContext(), "Error downloading file " + count, Toast.LENGTH_LONG).show();
                                    String filepath =  getFileStreamPath(filename).toString();
                                    count++;
                                    resetDownload(filepath);
                                    return;
                                }
                                count++;

                            }
                        });

                //done Downloading now set the sysLink path:
                realmImgurImage.setSysLink(filePath);
            }

            //end realm transaction
            realm.commitTransaction();
            realm.close(); // Remember to close Realm when done.
            fab.setVisibility(View.INVISIBLE);
            goNext.setVisibility(View.VISIBLE);
            playlistSubmitButton.setVisibility(View.INVISIBLE);
           } else {
            downloadLayout.setVisibility(View.INVISIBLE);
            Toast.makeText(Imgur.this, "Error saving", Toast.LENGTH_LONG).show();
            // Either gone or invisible
        }

    }

    //Method used to generate a Unique ID connecting the created Realm Image objects to their
    //Respective parent Album Realm object
    public String nextSessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    //Go to main library activity
    public void goToLibrary(View view){
    Intent intent = new Intent(getBaseContext(),MainLibraryActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
    }

}