package com.cs371m.theselfiestudio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Picture extends ActionBarActivity {

    ///used for the camera
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int SELECT_PICTURE = 2;
    public int rating;

    public ImageView imageView;
    public String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        } else {
            if (requestCode == SELECT_PICTURE) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();


                    this.imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    Log.d("SelfieStudio", picturePath);
                    /*Uri selectedImage = data.getData();
                    //String imgFile = selectedImage.getPath();
                    Log.d("SelfieStudio", selectedImage.getPath());
                    Bitmap bitmap = null;
                    try {

                        String imgFile = selectedImage.getPath();
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        ImageView myImage = new ImageView(this);
                        myImage.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.d("SelfieStudio", data.getDataString());*/
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Jeremy's code, still need to fix it up
//    public void assignRating()
//    {
//        // Get the buttons and image from the layout file
//        Button left_btn = (Button) findViewById(R.id.left_button);
//        Button right_btn = (Button) findViewById(R.id.right_button);
//        ImageView image = (ImageView) findViewById(R.id.rating_label);
//
//        // hashmap for the images we will be using for our rating label
//        Map<String, Integer> map = new HashMap<String, Integer>();
//        map.put("ratchet", R.drawable.ratchet);
//        map.put("basic", R.drawable.basic);
//        map.put("bae", R.drawable.bae);
//        map.put("transparent", R.drawable.transparent);
//
//        // NOTE: For alpha release, we are simply assigning a random rating to the image
//        // as our computer vision algorithm won't be completed until our Beta version.
//        Random r = new Random();
//        rating =  r.nextInt(4 - 1) + 1;
//
//        if (rating == 1) {
//            // Rating is RATCHET
//            // For now, we will discourage our users to upload a ratchet selfie by graying out the
//            // upload button
//
//            left_btn.setBackgroundResource(R.drawable.cancel);
//            right_btn.setBackgroundResource(R.drawable.sharegray);
//            image.setImageResource(map.get("ratchet"));
//        } else if (rating == 2) {
//            // Rating is BASIC
//            // User is allowed to upload the photo
//
//            left_btn.setBackgroundResource(R.drawable.cancel);
//            right_btn.setBackgroundResource(R.drawable.share);
//            image.setImageResource(map.get("basic"));
//        } else {
//            // Rating is BAE
//            // We will encourage the user to upload the photo
//
//            left_btn.setBackgroundResource(R.drawable.cancel);
//            right_btn.setBackgroundResource(R.drawable.share);
//            image.setImageResource(map.get("bae"));
//        }
//    }
//
//    public void leftButtonClicked()
//    {
//        if (rating == 3) {
//            // Rating is BAE
//            // We want to ask our user if they are absolutely sure they want to trash such an
//            // incredible looking picture
//
//            new AlertDialog.Builder(this)
//                    .setTitle("Whoa!")
//                    .setMessage(R.string.are_you_sure_bae)
//                    .setPositiveButton(R.string.upload_it, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            uploadImageToFacebook(image); // feed this function the bitmap of the image the user just took
//                        }
//                    })
//                    .setNegativeButton(R.string.dont_upload_it, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                        /* NEED TO GO BACK TO TAKING A NEW PICTURE */
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
//        } else {
//        /* NEED TO GO BACK TO TAKING A NEW PICTURE */
//        }
//    }
//
//    public void rightButtonClicked()
//    {
//        if (rating == 1) {
//            // Rating is BAE
//            // We want to ask our user if they are absolutely sure they want to trash such an
//            // incredible looking picture
//
//            new AlertDialog.Builder(this)
//                    .setTitle("Whoa!")
//                    .setMessage(R.string.sorry_too_ratchet)
//                    .setPositiveButton(R.string.dont_upload_it, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            /* NEED TO GO BACK TO TAKING A NEW PICTURE */
//                        }
//                    })
//                    .setNegativeButton(R.string.upload_anyway, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            uploadImageToFacebook(image); // feed this function the bitmap of the image the user just took
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
//        } else {
//            uploadImageToFacebook(image); // feed this function the bitmap of the image the user just took
//        }
//    }
//
//    public void uploadImageToFacebook(Bitmap image)
//    {
//        SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
//        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
//    }
}
