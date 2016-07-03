package rkr.binatestation.piclo.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.alexbbb.uploadservice.AbstractUploadServiceReceiver;
import com.alexbbb.uploadservice.ContentType;
import com.alexbbb.uploadservice.MultipartUploadRequest;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.models.Categories;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.Constants;
import rkr.binatestation.piclo.utils.Util;

public class UploadPicture extends AppCompatActivity {
    private static final String tag = UploadPicture.class.getSimpleName();
    FloatingActionButton upload;
    TextInputEditText chooseFile, title, courtesy;
    Spinner category;
    String selectedImagePath = "";
    Uri uri;
    ImageView uploadPicture;
    Integer REQUEST_CAMERA = 1;
    Integer SELECT_FILE = 2;
    ArrayAdapter<Categories> categoriesArrayAdapter;
    private ProgressDialog mProgressDialog;
    private final AbstractUploadServiceReceiver uploadReceiver = new AbstractUploadServiceReceiver() {

        @Override
        public void onProgress(String uploadId, int progress) {
            Log.i(tag, "The progress of the upload with ID " + uploadId + " is: " + progress);
            try {
                if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                    showProgressDialog(true, uploadId, "Image Uploading...");
                    mProgressDialog.setProgress(progress);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProgress(final String uploadId, final long uploadedBytes, final long totalBytes) {
            Log.i(tag, "Upload with ID " + uploadId + " uploaded bytes: " + uploadedBytes
                    + ", total: " + totalBytes);
        }

        @Override
        public void onError(String uploadId, Exception exception) {
            try {
                showProgressDialog(false, "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(tag, "Error in upload with ID: " + uploadId + ". "
                    + exception.getLocalizedMessage(), exception);
        }

        @Override
        public void onCompleted(String uploadId, int serverResponseCode, String serverResponseMessage) {
            Log.i(tag, "Upload with ID " + uploadId
                    + " has been completed with HTTP " + serverResponseCode
                    + ". Response from server: " + serverResponseMessage);
            try {
                showProgressDialog(false, "", "");
                JSONObject response = new JSONObject(serverResponseMessage);
                if (response.has("status") && response.optBoolean("status")) {
                    Log.i(tag, response.optString("message"));
                    Util.alert(getContext(), "Alert", response.optString("message"), true);
                } else {
                    Log.e(tag, response.optString("message"));
                    Util.showProgressOrError(getSupportFragmentManager(), R.id.AUP_contentLayout, 2, "UPLOAD_ACTIVITY_ERROR");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image_uri", uri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null || mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
        }

        uploadPicture = (ImageView) findViewById(R.id.AUP_uploadPicture);
        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    selectImage();
                }
            }
        });

        upload = (FloatingActionButton) findViewById(R.id.AUP_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        chooseFile = (TextInputEditText) findViewById(R.id.AUP_chooseFile);
        chooseFile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isStoragePermissionGranted()) {
                    selectImage();
                }
            }
        });
        chooseFile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isStoragePermissionGranted()) {
                        selectImage();
                    }
                }
                return true;
            }
        });

        title = (TextInputEditText) findViewById(R.id.AUP_title);
        category = (Spinner) findViewById(R.id.AUP_category);
        courtesy = (TextInputEditText) findViewById(R.id.AUP_courtesy);

        categoriesArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        categoriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoriesArrayAdapter);
        setCategory();

        if (savedInstanceState != null) {
            setImageView((Uri) savedInstanceState.getParcelable("image_uri"));
        }
    }

    private void validateInputs() {
        if (TextUtils.isEmpty(title.getText().toString())) {
            title.setError("Please provide a title..!");
            title.requestFocus();
        } else if (TextUtils.isEmpty(courtesy.getText().toString())) {
            courtesy.setError("Please specify the courtesy of this image..!");
            courtesy.requestFocus();
        } else if (TextUtils.isEmpty(chooseFile.getText().toString())) {
            chooseFile.setError("Please choose a file to upload..!");
            chooseFile.requestFocus();
        } else {
            uploadImage(getContext());
        }
    }

    private void setCategory() {
        Categories categoriesDB = new Categories(getContext());
        categoriesDB.open();
        categoriesArrayAdapter.addAll(categoriesDB.getAllRows());
        categoriesDB.close();
        categoriesArrayAdapter.notifyDataSetChanged();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(tag, "Permission is granted");
                return true;
            } else {

                Log.v(tag, "Permission is revoked");
                ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(tag, "Permission is granted");
            return true;
        }
    }

    public void showProgressDialog(Boolean aBoolean, String message, String title) {
        try {
            if (mProgressDialog != null) {
                if (aBoolean) {
                    if (TextUtils.isEmpty(message)) {
                        mProgressDialog.setMessage("Please wait ...");
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                    } else {
                        mProgressDialog.setTitle(title);
                        mProgressDialog.setMessage(message);
                        mProgressDialog.setMax(100);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialog.show();
                    }
                } else {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(tag, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            if (isStoragePermissionGranted()) {
                selectImage();
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void setImageView(Uri result) {
        try {
            uri = result;
            uploadPicture.setImageBitmap(getThumbnailFromUri(result));
            File file = new File(result.getPath());
            chooseFile.setText(file.getName());
            selectedImagePath = Util.getRealPathFromURI(getContext(), result);
            Log.i(tag, selectedImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getThumbnailFromUri(Uri uri) {
        try {
            return MediaStore.Images.Thumbnails.getThumbnail(
                    getContentResolver(), Util.getOriginIdFromURI(getContext(), uri),
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
                Uri result = Crop.getOutput(data);
                setImageView(result);
            }
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CAMERA) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    assert thumbnail != null;
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    File destination = new File(Util.getCaptureImagePath(),
                            "IMG_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    try {
                        if (destination.createNewFile()) {
                            fo = new FileOutputStream(destination);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Uri destinationURI = Uri.fromFile(new File(Util.getCaptureImagePath(), "IMG_CROPPED" + System.currentTimeMillis() + ".jpg"));
                    Uri source = Uri.fromFile(destination);
//                    Crop.of(source, destinationURI).withAspect(5, 3).start(this);
                    setImageView(source);
                } else if (requestCode == SELECT_FILE) {
                    Uri selectedImageUri = data.getData();
                    selectedImagePath = Util.getRealPathFromURI(getContext(), selectedImageUri);
                    if (selectedImagePath != null && selectedImagePath.contains("http")) {
                        // starting new Async Task
                        downloadFileFromURL(selectedImagePath);
                    } else if (selectedImageUri.toString().startsWith("content://com.google.android.apps.photos.content")) {
                        InputStream is = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        File destination = new File(Util.getCaptureImagePath(), "IMG_" + System.currentTimeMillis() + ".jpg");
                        FileOutputStream fo;
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                        try {
                            if (destination.createNewFile()) {
                                fo = new FileOutputStream(destination);
                                fo.write(bytes.toByteArray());
                                fo.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        Uri destinationURI = Uri.fromFile(new File(Util.getCaptureImagePath(), "IMG_CROPPED" + System.currentTimeMillis() + ".jpg"));
                        Uri source = Uri.fromFile(destination);
//                        Crop.of(source, destinationURI).withAspect(5, 3).start(this);
                        setImageView(source);
                    } else {
//                        Uri destinationURI = Uri.fromFile(new File(Util.getCaptureImagePath(), "IMG_CROPPED" + System.currentTimeMillis() + ".jpg"));
//                        Crop.of(selectedImageUri, destinationURI).withAspect(5, 3).start(this);
                        setImageView(selectedImageUri);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UploadPicture getContext() {
        return UploadPicture.this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_login_logout) {
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadFileFromURL(String url) {
        new AsyncTask<String, String, String>() {
            /**
             * Before starting background thread
             * Show Progress Bar Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setMessage("Downloading file. Please wait...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();

            }

            /**
             * Downloading file in background thread
             */
            @Override
            protected String doInBackground(String... f_url) {
                int count;
                try {
                    URL url = new URL(f_url[0]);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // this will be useful so that you can show a tipical 0-100% progress bar
                    int lenghtOfFile = conection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream
                    OutputStream output = new FileOutputStream(Util.getCaptureImagePath() + "/profile.jpg");

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }

                return null;
            }

            /**
             * Updating progress bar
             */
            protected void onProgressUpdate(String... progress) {
                // setting progress percentage
                mProgressDialog.setProgress(Integer.parseInt(progress[0]));
            }

            /**
             * After completing background task
             * Dismiss the progress dialog
             **/
            @Override
            protected void onPostExecute(String file_url) {
                // dismiss the dialog after the file was downloaded
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                String imagePath = Util.getCaptureImagePath() + "/profile.jpg";
//                Uri destinationURI = Uri.fromFile(new File(Util.getCaptureImagePath(), "IMG_CROPPED" + System.currentTimeMillis() + ".jpg"));
                File destination = new File(imagePath);
                if (destination.exists()) {
                    Uri source = Uri.fromFile(destination);
//                    Crop.of(source, destinationURI).withAspect(5, 3).start(getContext());
                    setImageView(source);
                }
            }
        }.execute(url);
    }

    public void uploadImage(final Context context) {
        if (selectedImagePath != null) {
            File file = new File(selectedImagePath);
            if (file.exists()) {
                showProgressDialog(true, file.getName(), "Image Uploading...");
                MultipartUploadRequest request = new MultipartUploadRequest(context, file.getName(), VolleySingleTon.getDomainUrl() + Constants.GALLERY_UPLOAD);
                request.addFileToUpload(selectedImagePath, "file", file.getName(), ContentType.IMAGE_JPEG);
                request.addParameter("userId", getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).getString(Constants.KEY_USER_ID, ""));
                request.addParameter("title", title.getText().toString().trim());
                request.addParameter("courtesy", courtesy.getText().toString().trim());
                request.addParameter("category", categoriesArrayAdapter.getItem(category.getSelectedItemPosition()).getCategoryId());

                //configure the notification
                request.setNotificationConfig(R.mipmap.ic_launcher,
                        getString(R.string.app_name),
                        " Profile update in progress ",
                        " Profile update completed successfully",
                        "Profile update Intercepted",
                        true);

                // if you comment the following line, the system default user-agent will be used
                request.setCustomUserAgent("UploadServiceDemo/1.0");


                // set the maximum number of automatic upload retries on error
                request.setMaxRetries(2);

                try {
                    Log.i(tag, "Request :- " + request.toString());
                    request.startUpload();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            } else {
                Util.alert(getContext(), "Alert", "File doesn't exits..!", false);
                showProgressDialog(false, "", "");
            }
        } else {
            Util.alert(getContext(), "Alert", "Please select a file first...!", false);
            showProgressDialog(false, "", "");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        uploadReceiver.register(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uploadReceiver.unregister(getContext());
    }

}
