package com.cs371m.theselfiestudio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Picture extends ActionBarActivity {

    ///used for the camera
    private static final int CAMERA_REQUEST = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int SELECT_PICTURE = 2;
    public static final int NUM_TOP_IMAGES = 3;
    public static final int BAE = 3;
    public static final int BASIC = 2;
    public static final int RATCHET = 1;

    private SoundPool mSounds;
    private HashMap<Integer, Integer> mSoundIDMap;
    private boolean mSoundOn;

    private SharedPreferences mPrefs;

    private Uri fileUri;
    public String picturePath;
    public int rating = 2;
    Bitmap newImage;
    public ImageButton cancel_btn;
    public ImageButton upload_btn;
    public ImageView ratingLabel;
    public Map<String, Integer> map;
    public ArrayList<String> imageUrls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        setContentView(R.layout.activity_picture);
        Intent messagePassed = getIntent();
        String message = messagePassed.getStringExtra(MainActivity.EXTRA_MESSAGE);
        imageUrls = new ArrayList<String>();

        if(message.equals("gallery")) {
            Log.d("SelfieStudio", "uploading picture from gallery... ");
            Intent upload = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(upload, SELECT_PICTURE);
            Log.d("SelfieStudio", "activity starting");
        }
        else{
            Log.d("SelfieStudio", "opening the camera app...");
            // create Intent to take a picture and return control to the calling application
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camera.putExtra("android.intent.extras.CAMERA_FACING", 1);
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
            camera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
            // start the image capture Intent
            startActivityForResult(camera, CAMERA_REQUEST);
        }
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
        Log.d("SelfieStudio", Environment.getExternalStorageState());
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "SelfieStudio");
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("SelfieStudio", "failed to create directory");
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
                Log.d("SelfieStudio", "returns NULL");
                return null;
            }

            Log.d("SelfieStudio", mediaFile.getAbsolutePath());
            return mediaFile;
        }
        return null;
    }

    public void uploadPicture(View v) {
        Log.d("SelfieStudio", "User wants to upload photo");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Log.d("SelfieStudio", fileUri.getPath());
            newImage = BitmapFactory.decodeFile(fileUri.getPath());
            imageView.setImageBitmap(newImage);
            assignRating();
            Log.d("SelfieStudio", "activity result: getting picture from camera");
        } else if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Log.d("SelfieStudio", "activity result: uploading picture from gallery");
            //loads the picture user uploaded from the gallery into an image view
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            //this.imageView = (ImageView) findViewById(R.id.imageView);
            newImage = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(newImage);
            assignRating();

            Log.d("SelfieStudio", picturePath);

        } else {
            Log.d("SelfieStudio", "activity result: else");
            //finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        createSoundPool();
    }//end onResume override method

    private void createSoundPool() {
        mSoundOn = mPrefs.getBoolean("sound", true);
        //mSoundOn = true;
        int[] soundIds = {R.raw.whistle, R.raw.bell, R.raw.ratchet};
        mSoundIDMap = new HashMap<>();
        mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        for(int id : soundIds)
            mSoundIDMap.put(id, mSounds.load(this, id, 1));
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
        Log.d("SelfieStudio", "on options item ");
        final Intent intent_logIn = new Intent(this, LoginActivity.class);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logOutPicture) {
            Log.d("SelfieStudio", "logout selected ");
            AccessToken.setCurrentAccessToken(null);
            startActivity(intent_logIn);
            finish();
            return true;
        }else if (id == R.id.settings) {
            startActivityForResult(new Intent(this, Settings.class), 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void assignRating()
    {
        // Get the buttons and image from the layout file
        cancel_btn = (ImageButton) findViewById(R.id.cancel);
        upload_btn = (ImageButton) findViewById(R.id.upload);
        ratingLabel = (ImageView) findViewById(R.id.rating_label);

        // ImageView image = (ImageView) findViewById(R.id.rating_label);

        // hashmap for the images we will be using for our rating label
        map = new HashMap<String, Integer>();
        map.put("ratchet", R.drawable.ratchet);
        map.put("basic", R.drawable.basic);
        map.put("bae", R.drawable.bae);
        map.put("transparent", R.drawable.transparent);

        final ArrayList<ImageFromFacebook> currentDataSet = new ArrayList<ImageFromFacebook>();

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        if (object == null) {
                            Log.d("Selfie Studio", "response is null");
                        } else {
                            try {
                                Log.d("Selfie Studio", "getting JSON from Facebook");
                                JSONArray data = object.getJSONObject("photos").getJSONArray("data");
                                ArrayList<String> image_urls = new ArrayList<String>();
                                ArrayList<Integer> like_counts = new ArrayList<Integer>();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject objAtIndexI = data.getJSONObject(i);
                                    JSONArray images = objAtIndexI.getJSONArray("images");
                                    JSONArray likes = objAtIndexI.getJSONObject("likes").getJSONArray("data");
                                    Integer numLikes = likes.length();
                                    //Log.d("Selfie Studio", "NUM LIKES: " + numLikes));
                                    JSONObject medRes = images.getJSONObject(images.length() / 2);
                                    String imageUrlStr = medRes.getString("source");
                                    //Log.d("Selfie Studio", "IMAGE URL: " + imageUrl);
                                    image_urls.add(imageUrlStr);
                                    like_counts.add(numLikes);
                                }
                                new DownloadImageTask(image_urls, like_counts).execute();
                                Log.d("Selfie Studio", "***** HERE *****");
                            } catch (JSONException e) {
                                Log.d("Selfie Studio", "fail");
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onPostExecute(JSONObject jsonData) {
                        Log.d("Selfie Studio", "POST EXECUTE");
                        if (rating == RATCHET) {
                            // Rating is RATCHET
                            // For now, we will discourage our users to upload a ratchet selfie by graying out the
                            // upload button

                            // cancel_btn.setBackgroundResource(R.drawable.cancel);
                            upload_btn.setImageResource(R.drawable.sharegray);
                            ratingLabel.setImageResource(map.get("ratchet"));
                            ratingLabel.bringToFront();
                            if(mSoundOn)
                                mSounds.play(mSoundIDMap.get(R.raw.ratchet), 1, 1, 1, 0, 1);
                        } else if (rating == BASIC) {
                            // Rating is BASIC
                            // User is allowed to upload the photo
                            ratingLabel.setImageResource(map.get("basic"));
                            ratingLabel.bringToFront();
                            if(mSoundOn)
                                mSounds.play(mSoundIDMap.get(R.raw.bell), 1, 1, 1, 0, 1);
                        } else {
                            // Rating is BAE
                            // We will encourage the user to upload the photo
                            ratingLabel.setImageResource(map.get("bae"));
                            ratingLabel.bringToFront();
                            if(mSoundOn)
                                mSounds.play(mSoundIDMap.get(R.raw.whistle), 1, 1, 1, 0, 1);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "photos{images,likes.limit(1000).summary(true)}");
        parameters.putString("limit", "10");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void cancelButtonClicked(View v)
    {
        if (rating == BAE) {
            // Rating is BAE
            // We want to ask our user if they are absolutely sure they want to trash such an
            // incredible looking picture

            new AlertDialog.Builder(this)
                    .setTitle("Whoa!")
                    .setMessage(R.string.are_you_sure_bae)
                    .setPositiveButton(R.string.upload_it, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(newImage != null) {
                                uploadImageToFacebook(newImage);
                            } else {
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dont_upload_it, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            finish();
        }
    }

    public void uploadButtonClicked(View v)
    {
        if (rating == RATCHET) {
            // Rating is RATCHET
            // We want to ask our user if they are absolutely sure they want to upload such an
            // ugly looking picture

            new AlertDialog.Builder(this)
                    .setTitle("Whoa!")
                    .setMessage(R.string.sorry_too_ratchet)
                    .setPositiveButton(R.string.dont_upload_it, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.upload_anyway, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (newImage != null) {
                                uploadImageToFacebook(newImage);
                            } else {
                                finish();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            uploadImageToFacebook(newImage);
        }
    }

    public void uploadImageToFacebook(Bitmap image)
    {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
            SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
            ShareDialog.show(Picture.this, content);
        }
    }

    /* Function for calculating the signature vector of an image using 25 uniformly
     * distributed points on a grid on the image
     * For reference see equivalent C++ code in OpenCV
     * http://docs.opencv.org/2.3/modules/features2d/doc/feature_detection_and_description.html
     */
    private int[][] calcSignatureVector(Bitmap image)
    {
        int[][] signature = new int[5][5];

        // our grid will want to use points distributed across the image, but not too close to edges
        float[] prop = new float[]{(float) 0.1, (float) 0.3, (float) 0.5, (float) 0.7, (float) 0.9};

        for (int x = 0; x < 5; x++)
            for (int y = 0; y < 5; y++)
              signature[x][y] = averageAround(image, prop[x], prop[y]);
        return signature;
    }

    /* Function for getting an average of the colors around a given pixel
     * This will help us avoid outlier pixels that might throw off our signature vectors
     */
    private int averageAround(Bitmap image, double px, double py)
    {
        int sampleSize = 15;
        int numPixels = 0;
        double[] accum = new double[3];
        int baseSize = (int) (image.getWidth() * 0.025f);

        // As we loop through the block of pixels surrounding our originally chosen pixel,
        // add the RGB values to our accum vector to be averaged later on
        for (double x = px * baseSize - sampleSize; x < px * baseSize + sampleSize; x++)
        {
            for (double y = py * baseSize - sampleSize; y < py * baseSize + sampleSize; y++)
            {
                x = Math.abs(x);
                y = Math.abs(y);
                int thisPixel = image.getPixel((int) x, (int) y);
                accum[0] += Color.red(thisPixel);
                accum[1] += Color.green(thisPixel);
                accum[2] += Color.blue(thisPixel);
                numPixels++;
            }
        }

        // Calculate the average
        accum[0] /= numPixels;
        accum[1] /= numPixels;
        accum[2] /= numPixels;

        return Color.rgb((int) accum[0], (int) accum[1], (int) accum[2]);
    }

    /* Simple function for calculating the distance between our two vectors
     * Fails if the vectors are not of the same length (which should never happen due to the
     * contrived way in which we call the function)
     * Source: http://en.wikipedia.org/wiki/Euclidean_distance
     */
    public double calcVectorDistance(int[][] a, int[][] b)
    {
        // Make sure we are dealing with vectors of equal length
        if (a.length != b.length || a[0].length != b[0].length)
            return (double) -1.0;

        // If so, calculate the distance between them
        double dist = 0;
        int outerLength = a.length;
        int innerLength = a[0].length;
        for (int x = 0; x < outerLength; x++)
             for (int y = 0; y < innerLength; y++)
             {
                int rA = Color.red(a[x][y]);
                int gA = Color.green(a[x][y]);
                int bA = Color.blue(a[x][y]);
                int rB = Color.red(b[x][y]);
                int gB = Color.green(b[x][y]);
                int bB = Color.blue(b[x][y]);
                double tempDist = Math.sqrt((rA-rB)*(rA-rB)+(gA-gB)*(gA-gB)+(bA-bB)*(bA-bB));
                dist += tempDist;
             }

        return dist;
    }

    class DownloadImageTask extends AsyncTask<String, Void, ArrayList<Bitmap>> {

        ArrayList<Integer> likeCounts;
        ArrayList<String> urls;

        public DownloadImageTask(ArrayList<String> imgUrls, ArrayList<Integer> likeCounts) {
            this.likeCounts = likeCounts;
            this.urls = imgUrls;
        }

        protected ArrayList<Bitmap> doInBackground(String... strings) {
            Log.d("Selfie Studio", "IN THE ASYNC TASK");
            int count = urls.size();
            ArrayList<Bitmap> images = new ArrayList<Bitmap>();
            try {
                for (int i = 0; i < count; i++) {
                    Log.d("Selfie Studio", "***** GETTING BITMAP FROM " + urls.get(i));
                    InputStream in = new java.net.URL(urls.get(i)).openStream();
                    Bitmap img = BitmapFactory.decodeStream(in);
                    images.add(img);
                    Log.d("Selfie Studio", "***** GOT BITMAP FROM " + urls.get(i));
                    // Escape early if cancel() is called
                    if (isCancelled()) break;
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> result) {
            ArrayList<ImageFromFacebook> currentDataSet = new ArrayList<ImageFromFacebook>();
            for (int k = 0; k < result.size(); k++)
                currentDataSet.add(new ImageFromFacebook(result.get(k), likeCounts.get(k)));

            // get the signature vector for our original image
            int[][] signatureVector = calcSignatureVector(newImage);

            double[] distances = new double[currentDataSet.size()];

            for (int i = 0; i < currentDataSet.size(); i++)
            {
                int[][] comparisonVector = calcSignatureVector(currentDataSet.get(i).img);
                double distance = calcVectorDistance(signatureVector, comparisonVector);
                if (distance < 0)
                    Log.e("ERROR", "There is an error in the signature vector algorithm");
                distances[i] = distance;
            }

            // Sort the vectors
            for (int p1 = 0; p1 < currentDataSet.size() - 1; p1++)
            {
                for (int p2 = p1 + 1; p2 < currentDataSet.size(); p2++)
                {
                    if (distances[p1] > distances[p2])
                    {
                        double tempDist = distances[p1];
                        distances[p1] = distances[p2];
                        distances[p2] = tempDist;

                        ImageFromFacebook temp = currentDataSet.get(p1);
                        currentDataSet.set(p1, currentDataSet.get(p2));
                        currentDataSet.set(p2, temp);
                    }
                }
            }

            // Get average number of likes for the entire set of images
            int sum = 0;
            int similarImgSum = 0;
            for (int i = 0; i < currentDataSet.size(); i++)
            {
                if (i < NUM_TOP_IMAGES) {
                    similarImgSum += currentDataSet.get(i).numLikes;
                    Log.d("SelfieStudio", "Photo Name: " + currentDataSet.get(i).name);
                }
                sum += currentDataSet.get(i).numLikes;
            }

            float similarImagesAvgLikes = (float) similarImgSum / NUM_TOP_IMAGES;
            float averageLikes = (float) sum / currentDataSet.size();

            // Compare the top three images' average likes to the overall data set's average likes
            float threshold = 0.3f;
            if (similarImagesAvgLikes > (averageLikes + averageLikes * threshold))
                rating = BAE;
            else if (similarImagesAvgLikes < (averageLikes - averageLikes * threshold))
                rating = RATCHET;
            else
                rating = BASIC;

            if (rating == RATCHET) {
                // Rating is RATCHET
                // For now, we will discourage our users to upload a ratchet selfie by graying out the
                // upload button
                // cancel_btn.setBackgroundResource(R.drawable.cancel);
                upload_btn.setImageResource(R.drawable.sharegray);
                ratingLabel.setImageResource(map.get("ratchet"));
                ratingLabel.bringToFront();
                if(mSoundOn)
                    mSounds.play(mSoundIDMap.get(R.raw.ratchet), 1, 1, 1, 0, 1);
            } else if (rating == BASIC) {
                // Rating is BASIC
                // User is allowed to upload the photo
                ratingLabel.setImageResource(map.get("basic"));
                ratingLabel.bringToFront();
                if(mSoundOn)
                    mSounds.play(mSoundIDMap.get(R.raw.bell), 1, 1, 1, 0, 1);
            } else {
                // Rating is BAE
                // We will encourage the user to upload the photo
                ratingLabel.setImageResource(map.get("bae"));
                ratingLabel.bringToFront();
                if(mSoundOn)
                    mSounds.play(mSoundIDMap.get(R.raw.whistle), 1, 1, 1, 0, 1);
            }
        }
    }
}

