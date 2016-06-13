package rkr.binatestation.piclo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
     * @param pictureModelList list of bids
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
        holder.picture.setImageUrl(
                "https://s-media-cache-ak0.pinimg.com/736x/b6/a9/ca/b6a9cad04bbe8f17a669c9beabc1e63d.jpg",
                VolleySingleTon.getInstance(holder.picture.getContext()).getImageLoader()
        );
    }

    @Override
    public int getItemCount() {
        return 2;//pictureModelList.size();
    }


    /**
     * This ItemHolder class which extends RecyclerView.ViewHolder which have child view objects of an ItemHolder
     */
    class ItemHolder extends RecyclerView.ViewHolder {
        View view;
        NetworkImageView picture;

        /**
         * Constructor to initialize the child views of the view holder
         */
        public ItemHolder(View itemView) {
            super(itemView);
            view = itemView;
            picture = (NetworkImageView) itemView.findViewById(R.id.PI_image);
        }
    }
}
