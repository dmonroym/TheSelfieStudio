package com.cs371m.theselfiestudio;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;


public class MainActivity extends ActionBarActivity {
    CallbackManager callbackManager;
    private TextView greeting;
    private Profile profile;

    public final static String EXTRA_MESSAGE = "com.cs371m.theselfiestudio.MESSAGE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        //profile = Profile.getCurrentProfile();
    }

    /*Method called when Camera button is clicked, opens a new Activity called Picture, and allows user to
    take a new photo of themselves view the devices camera application.
    */
    public void takePicture(View v) {
        Intent camera = new Intent(this, Picture.class);
        String message = "camera";
        camera.putExtra(EXTRA_MESSAGE, message);
        startActivity(camera);
    }

    /*Method called when Gallery button is clicked, opens a new Activity called Picture, and allows user to select
    The photo they want to upload from gallery.
    */
    public void uploadPicture(View v) {
        Intent gallery = new Intent(this, Picture.class);
        String message = "gallery";
        gallery.putExtra(EXTRA_MESSAGE, message);
        startActivity(gallery);
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
//        right_btn.setBackgroundResource(R.drawable.sharegray);
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
