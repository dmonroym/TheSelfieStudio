package com.cs371m.theselfiestudio;


import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {
    CallbackManager callbackManager;
    private TextView greeting;
    private Profile profile;
    int rating = 2;

    //final Intent intent_picture = new Intent(this, Picture.class);
    ///used for the camera
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int SELECT_PICTURE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        //profile = Profile.getCurrentProfile();


        Button camera = (Button) findViewById(R.id.cameraButton); //R.id.button is the button that opens the camera
        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(intent_picture);
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    //Method called when Camera button is clicked, opens

    /*Method called when Gallery button is clicked, opens a new Activity called Picture, and allows user to select
    The photo they want to upload from gallery.
    */
    public void uploadPicture(View v) {
        Intent myIntent = new Intent(MainActivity.this, Picture.class);
        MainActivity.this.startActivity(myIntent);
    }


    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            Log.d("MyCameraApp", "returns NULL");
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onResume() {
        super.onResume();
        profile = Profile.getCurrentProfile();
        greeting = (TextView) findViewById(R.id.greeting);
        greeting.setText(getString(R.string.hello_user) + " " + profile.getFirstName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final Intent intent_logIn = new Intent(this, LoginActivity.class);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logOut) {
            AccessToken.setCurrentAccessToken(null);
            startActivity(intent_logIn);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

///* METHODS FOR NEW ACTIVITY */
//
//public void assignRating()
//{
//    // Get the buttons and image from the layout file
//    Button left_btn = (Button) findViewById(R.id.left_button);
//    Button right_btn = (Button) findViewById(R.id.right_button);
//    ImageView image = (ImageView) findViewById(R.id.rating_label);
//
//    // hashmap for the images we will be using for our rating label
//    Map<String, Integer> map = new HashMap<String, Integer>();
//    map.put("ratchet", R.drawable.ratchet);
//    map.put("basic", R.drawable.basic);
//    map.put("bae", R.drawable.bae);
//    map.put("transparent", R.drawable.transparent);
//
//    // NOTE: For alpha release, we are simply assigning a random rating to the image
//    // as our computer vision algorithm won't be completed until our Beta version.
//    Random r = new Random();
//    rating =  r.nextInt(4 - 1) + 1;
//
//    if (rating == 1) {
//        // Rating is RATCHET
//        // For now, we will discourage our users to upload a ratchet selfie by graying out the
//        // upload button
//
//        left_btn.setBackgroundResource(R.drawable.cancel);
//        right_btn.setBackgroundResource(R.drawable.shareGray);
//        image.setImageResource(map.get("ratchet"));
//    } else if (rating == 2) {
//        // Rating is BASIC
//        // User is allowed to upload the photo
//
//        left_btn.setBackgroundResource(R.drawable.cancel);
//        right_btn.setBackgroundResource(R.drawable.share);
//        image.setImageResource(map.get("basic"));
//    } else {
//        // Rating is BAE
//        // We will encourage the user to upload the photo
//
//        left_btn.setBackgroundResource(R.drawable.cancel);
//        right_btn.setBackgroundResource(R.drawable.share);
//        image.setImageResource(map.get("bae"));
//    }
//}
//
//public void leftButtonClicked()
//{
//    if (rating == 3) {
//        // Rating is BAE
//        // We want to ask our user if they are absolutely sure they want to trash such an
//        // incredible looking picture
//
//        new AlertDialog.Builder(this)
//                .setTitle("Whoa!")
//                .setMessage(R.string.are_you_sure_bae)
//                .setPositiveButton(R.string.upload_it, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        uploadImageToFacebook(image); // feed this function the bitmap of the image the user just took
//                    }
//                })
//                .setNegativeButton(R.string.dont_upload_it, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        /* NEED TO GO BACK TO TAKING A NEW PICTURE */
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//    } else {
//        /* NEED TO GO BACK TO TAKING A NEW PICTURE */
//    }
//}
//
//public void rightButtonClicked()
//{
//    if (rating == 1) {
//        // Rating is BAE
//        // We want to ask our user if they are absolutely sure they want to trash such an
//        // incredible looking picture
//
//        new AlertDialog.Builder(this)
//                .setTitle("Whoa!")
//                .setMessage(R.string.sorry_too_ratchet)
//                .setPositiveButton(R.string.dont_upload_it, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                            /* NEED TO GO BACK TO TAKING A NEW PICTURE */
//                    }
//                })
//                .setNegativeButton(R.string.upload_anyway, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        uploadImageToFacebook(image); // feed this function the bitmap of the image the user just took
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//    } else {
//        uploadImageToFacebook(image); // feed this function the bitmap of the image the user just took
//    }
//}
//
//public void uploadImageToFacebook(Bitmap image)
//{
//    SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
//    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
//}
//
///* GLOBAL STUFF */
//int rating = 2;
//
///* STUFF THAT GOES IN THE LAYOUT XML */
//<Button
//android:id="@+id/left_button"
//        android:layout_width="wrap_content"
//        android:layout_height="wrap_content"
//        android:onClick="leftButtonClicked">
//</Button>
//<Button
//android:id="@+id/right_button"
//        android:layout_width="wrap_content"
//        android:layout_height="wrap_content"
//        android:onClick="rightButtonClicked">
//</Button>
//<ImageView android:id="@+id/rating_label"
//        android:layout_width="wrap_content"
//        android:layout_height="wrap_content"
//        />
