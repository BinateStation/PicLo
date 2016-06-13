package rkr.binatestation.piclo.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by RKR on 03-06-2016.
 * PictureModel.
 */
public class PictureModel implements Serializable {
    String Name;
    String url;
    long likeCount;
    boolean isLiked;
    Date updatedDate;

    public PictureModel(String name, String url, long likeCount, boolean isLiked, Date updatedDate) {
        Name = name;
        this.url = url;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.updatedDate = updatedDate;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
