package rkr.binatestation.piclo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.NetworkImageView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.models.PictureModel;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.Util;

/**
 * Created by RKR on 29-01-2016.
 * PictureAdapter.
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ItemHolder> {
    List<PictureModel> pictureModelList = new ArrayList<>();

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
                share(view.getContext(), VolleySingleTon.getDomainUrlForImage() + getItem(holder.getAdapterPosition()).getFile());
            }
        });
        holder.whatsAppShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(view.getContext(), VolleySingleTon.getDomainUrlForImage() + getItem(holder.getAdapterPosition()).getFile());
            }
        });
        holder.saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFileFromURL(view.getContext(),
                        VolleySingleTon.getDomainUrlForImage() + getItem(holder.getAdapterPosition()).getFile(),
                        getItem(holder.getAdapterPosition()).getTitle());
            }
        });
    }

    private void share(Context context, String url) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    @Override
    public int getItemCount() {
        return pictureModelList.size();
    }

    private void downloadFileFromURL(final Context context, String url, final String fileName) {
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
                mProgressDialog.setMessage("Downloading file. Please wait...");
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
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    // this will be useful so that you can show a tipical 0-100% progress bar
                    int lengthOfFile = urlConnection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream
                    OutputStream output = new FileOutputStream(Util.getCaptureImagePath() + "/" + fileName.replace(" ", "_") + ".jpg");

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
