package rkr.binatestation.piclo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.models.PictureModel;
import rkr.binatestation.piclo.network.VolleySingleTon;

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
    }

    @Override
    public int getItemCount() {
        return pictureModelList.size();
    }


    /**
     * This ItemHolder class which extends RecyclerView.ViewHolder which have child view objects of an ItemHolder
     */
    class ItemHolder extends RecyclerView.ViewHolder {
        View view;
        NetworkImageView picture;
        TextView title, courtesy, uploadedBy, likeCount;
        ToggleButton like;

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
        }
    }
}
