package rkr.binatestation.piclo.adapters;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.activities.ImageFullScreenActivity;
import rkr.binatestation.piclo.fragments.MainContentFragment;
import rkr.binatestation.piclo.models.PictureModel;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.Util;

/**
 * Created by RKR on 29-01-2016.
 * PictureAdapter.
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ItemHolder> {
    private static final String tag = PictureAdapter.class.getName();
    List<PictureModel> pictureModelList = new ArrayList<>();
    MainContentFragment thisObject;

    public PictureAdapter(MainContentFragment thisObject) {
        this.thisObject = thisObject;
    }

    /**
     * Method to get single item from the list according to the position argument
     *
     * @param position list position which item need to get.
     * @return PictureModel object from the specified list position.
     */
    private PictureModel getItem(Integer position) {
        return pictureModelList.get(position);
    }

    /**
     * Method to set the pictureModelList
     *
     * @param pictureModelList list of images
     */
    public void setPictureModelList(List<PictureModel> pictureModelList) {
        this.pictureModelList = pictureModelList;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        holder.picture.setDefaultImageResId(R.drawable.ic_piclo_24dp);
        holder.picture.setErrorImageResId(R.drawable.ic_piclo_24dp);
        holder.picture.setAdjustViewBounds(true);
        holder.picture.setImageUrl(
                VolleySingleTon.getDomainUrlForImage() + getItem(position).getFile(),
                VolleySingleTon.getInstance(holder.picture.getContext()).getImageLoader()
        );
        holder.title.setText(getItem(position).getTitle());
        holder.courtesy.setText(getItem(position).getCourtesy());
        holder.uploadedBy.setText(getItem(position).getFullName());
        if (holder.like.isChecked()) {
            holder.likeCount.setAlpha(1);
        } else {
            holder.likeCount.setAlpha(0.4f);
        }
        holder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.likeCount.setAlpha(1);
                } else {
                    holder.likeCount.setAlpha(0.4f);
                }
            }
        });

        holder.fbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareToFacebook(getBitMapFromImageView(holder.picture));
            }
        });
        holder.whatsAppShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled("com.whatsapp", thisObject.getContext()) && isStoragePermissionGranted()) {
                    downloadFileFromURL(view.getContext(),
                            VolleySingleTon.getDomainUrlForImage() + getItem(holder.getAdapterPosition()).getFile(),
                            getItem(holder.getAdapterPosition()).getTitle(), 1);
                }
            }
        });
        holder.saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted()) {
                    downloadFileFromURL(view.getContext(),
                            VolleySingleTon.getDomainUrlForImage() + getItem(holder.getAdapterPosition()).getFile(),
                            getItem(holder.getAdapterPosition()).getTitle(), 2);
                }
            }
        });
        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ImageFullScreenActivity.class);
                intent.putExtra("IMAGE", getItem(holder.getAdapterPosition()).getFile());
                ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(holder.view, (int) holder.view.getX(), (int) holder.view.getY()
                        , holder.view.getWidth(), holder.view.getHeight());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    thisObject.startActivity(intent, options.toBundle());
                } else {
                    thisObject.startActivity(intent);
                }
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (thisObject.getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(tag, "Permission is granted");
                return true;
            } else {

                Log.v(tag, "Permission is revoked");
                ActivityCompat.requestPermissions(thisObject.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(tag, "Permission is granted");
            return true;
        }
    }

    public void shareToFacebook(Bitmap image) {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            ShareDialog.show(thisObject, content);
        }
    }

    private void share(Context context, Uri uri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/jpeg");
        sendIntent.setPackage("com.whatsapp");
        context.startActivity(sendIntent);
    }

    private boolean isPackageInstalled(String packageName, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private Bitmap getBitMapFromImageView(NetworkImageView networkImageView) {
        ImageLoader.ImageContainer container = (ImageLoader.ImageContainer) networkImageView.getTag();
        if (container != null) {
            final Bitmap bitmap = container.getBitmap();
            if (bitmap != null) {
                return bitmap;
            }
        }
        return ((BitmapDrawable) networkImageView.getDrawable()).getBitmap();
    }


    @Override
    public int getItemCount() {
        return pictureModelList.size();
    }

    private void downloadFileFromURL(final Context context, String url, final String fileName, final int type) {
        new AsyncTask<String, String, String>() {
            ProgressDialog mProgressDialog;

            /**
             * Before starting background thread
             * Show Progress Bar Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(context);
                if (type == 1) {
                    mProgressDialog.setMessage("Please wait until prepare your file for sharing..");
                } else {
                    mProgressDialog.setMessage("Downloading file. Please wait...");
                }
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
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
                    String filePath;
                    if (type == 1) {
                        filePath = Util.getCaptureImagePath() + "/whats_app_share.jpg";
                    } else {
                        filePath = Util.getCaptureImagePath() + "/" + fileName.replace(" ", "_") + ".jpg";
                    }
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    // this will be useful so that you can show a tipical 0-100% progress bar
                    int lengthOfFile = urlConnection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream
                    OutputStream output = new FileOutputStream(filePath);

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lengthOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    return filePath;

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
                return "";
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
                if (type == 1 && (!file_url.equalsIgnoreCase(""))) {
                    share(thisObject.getContext(), Uri.parse(file_url));
                }
            }
        }.execute(url);
    }


    /**
     * This ItemHolder class which extends RecyclerView.ViewHolder which have child view objects of an ItemHolder
     */
    class ItemHolder extends RecyclerView.ViewHolder {
        View view;
        NetworkImageView picture;
        TextView title, courtesy, uploadedBy, likeCount;
        ToggleButton like;
        ImageButton fbShare, whatsAppShare, saveImage;

        /**
         * Constructor to initialize the child views of the view holder
         */
        public ItemHolder(View itemView) {
            super(itemView);
            view = itemView;
            picture = (NetworkImageView) itemView.findViewById(R.id.PI_image);
            title = (TextView) itemView.findViewById(R.id.PI_title);
            courtesy = (TextView) itemView.findViewById(R.id.PI_courtesy);
            uploadedBy = (TextView) itemView.findViewById(R.id.PI_uploadedBy);
            like = (ToggleButton) itemView.findViewById(R.id.PI_like);
            likeCount = (TextView) itemView.findViewById(R.id.PI_likeCount);
            fbShare = (ImageButton) itemView.findViewById(R.id.PI_fbShare);
            whatsAppShare = (ImageButton) itemView.findViewById(R.id.PI_whatsAppShare);
            saveImage = (ImageButton) itemView.findViewById(R.id.PI_saveImage);
        }
    }
}
