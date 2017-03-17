package rkr.binatestation.piclo.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.database.PicloContract;
import rkr.binatestation.piclo.models.Category;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.Constants;
import rkr.binatestation.piclo.utils.Util;

import static rkr.binatestation.piclo.models.Category.getCategories;
import static rkr.binatestation.piclo.utils.Constants.CONTENT_LOADER_CATEGORIES;

public class UploadPicture extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "UploadPicture";
    FloatingActionButton upload;
    TextInputEditText chooseFile, title, courtesy;
    Spinner categorySpinner;
    Uri uri;
    ImageView uploadPicture;
    Integer REQUEST_CAMERA = 1;
    Integer SELECT_FILE = 2;
    ArrayAdapter<Category> categoriesArrayAdapter;
    private ProgressDialog mProgressDialog;
    private final AbstractUploadServiceReceiver uploadReceiver = new AbstractUploadServiceReceiver() {

        @Override
        public void onProgress(String uploadId, int progress) {
            Log.i(TAG, "The progress of the upload with ID " + uploadId + " is: " + progress);
            try {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.setProgress(progress);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProgress(final String uploadId, final long uploadedBytes, final long totalBytes) {
            Log.i(TAG, "Upload with ID " + uploadId + " uploaded bytes: " + uploadedBytes + ", total: " + totalBytes);
        }

        @Override
        public void onError(String uploadId, Exception exception) {
            try {
                showProgressDialog(false, "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, "Error in upload with ID: " + uploadId + ". "
                    + exception.getLocalizedMessage(), exception);
        }

        @Override
        public void onCompleted(String uploadId, int serverResponseCode, String serverResponseMessage) {
            Log.i(TAG, "Upload with ID " + uploadId
                    + " has been completed with HTTP " + serverResponseCode
                    + ". Response from server: " + serverResponseMessage);
            try {
                showProgressDialog(false, "", "");
                JSONObject response = new JSONObject(serverResponseMessage);
                if (response.has("status") && response.optBoolean("status")) {
                    Log.i(TAG, response.optString("message"));
                    Util.alert(getContext(), "Alert", response.optString("message"), true);
                } else {
                    Log.e(TAG, response.optString("message"));
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
        categorySpinner = (Spinner) findViewById(R.id.AUP_category);
        courtesy = (TextInputEditText) findViewById(R.id.AUP_courtesy);

        categoriesArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        categoriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoriesArrayAdapter);
        initCategoriesLoader();

        if (savedInstanceState != null) {
            setImageView((Uri) savedInstanceState.getParcelable("image_uri"));
        }
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            setImageView(imageUri);
        }
    }

    private void initCategoriesLoader() {
        Log.d(TAG, "initCategoriesLoader() called");
        getSupportLoaderManager().initLoader(CONTENT_LOADER_CATEGORIES, null, this);
    }

    private void alertForLoggingIn() {
        Log.d(TAG, "alertForLoggingIn() called");
        try {
            new AlertDialog.Builder(getContext())
                    .setTitle("Alert")
                    .setMessage("Need sign in to proceed...!")
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getBaseContext(), LoginActivity.class));
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateInputs() {
        if (!getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
            alertForLoggingIn();
        } else if (TextUtils.isEmpty(title.getText().toString())) {
            title.setError("Please provide a title..!");
            title.requestFocus();
        } else if (categorySpinner.getSelectedItemPosition() == 0) {
            Util.showAlert(getContext(), "Alert", "Please select a category..!", false);
            categorySpinner.requestFocus();
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

    private void setCategorySpinner(List<Category> categories) {
        categoriesArrayAdapter.addAll(categories);
        if (categoriesArrayAdapter.getCount() > 0) {
            Category category = categoriesArrayAdapter.getItem(0);
            if (category != null) {
                category.setCategoryName("Select a category..!");
            }
        }
        categoriesArrayAdapter.notifyDataSetChanged();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
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
                        mProgressDialog.setCancelable(true);
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
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
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
            uploadPicture.setImageURI(uri);
            File file = new File(result.getPath());
            chooseFile.setText(file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
                    Uri source = Uri.fromFile(destination);
                    setImageView(source);
                } else if (requestCode == SELECT_FILE) {
                    Uri selectedImageUri = data.getData();
                    setImageView(selectedImageUri);
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
        if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
            menu.getItem(0).setTitle("Logout");
        } else {
            menu.getItem(0).setTitle("Login");
        }
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

    private File getFileForUpload() {
        File file = new File(uri.getPath());
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            file = new File(Util.getRealPathFromURI(this, uri));
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    public void uploadImage(final Context context) {
        File file = getFileForUpload();
        if (file != null) {
            showProgressDialog(true, file.getName(), "Image Uploading...");
            MultipartUploadRequest request = new MultipartUploadRequest(context, file.getName(), VolleySingleTon.getDomainUrl() + Constants.GALLERY_UPLOAD);
            request.addFileToUpload(file.getAbsolutePath(), "file", file.getName(), ContentType.IMAGE_JPEG);
            request.addParameter("userId", getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).getString(Constants.KEY_USER_ID, ""));
            request.addParameter("title", title.getText().toString().trim());
            request.addParameter("courtesy", courtesy.getText().toString().trim());
            Category category = categoriesArrayAdapter.getItem(categorySpinner.getSelectedItemPosition());
            if (category != null) {
                request.addParameter("category", "" + category.getCategoryId());
            }

            //configure the notification
            request.setNotificationConfig(R.mipmap.ic_launcher,
                    getString(R.string.app_name),
                    " Piclo update in progress ",
                    " Piclo update completed successfully",
                    " Piclo update Intercepted",
                    true);

            // if you comment the following line, the system default user-agent will be used
            request.setCustomUserAgent("UploadServiceDemo/1.0");


            // set the maximum number of automatic upload retries on error
            request.setMaxRetries(2);

            try {
                Log.i(TAG, "Request :- " + request.toString());
                request.startUpload();
            } catch (Exception exc) {
                exc.printStackTrace();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CONTENT_LOADER_CATEGORIES:
                return new CursorLoader(
                        getContext(),
                        PicloContract.CategoriesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader != null) {
            switch (loader.getId()) {
                case CONTENT_LOADER_CATEGORIES:
                    setCategorySpinner(getCategories(data));
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
