package rkr.binatestation.piclo.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by RKR on 03-06-2016.
 * PictureModel.
 */
public class PictureModel implements Serializable {
    String title;
    String file;
    long likeCount;
    boolean isLiked;
    Date updatedDate;
    String imageId;
    String userId;
    String categoryId;
    String courtesy;
    String categoryName;
    String fullName;

    public PictureModel(String title, String file, Date updatedDate, String imageId, String userId, String categoryId, String courtesy, String categoryName, String fullName) {
        this.title = title;
        this.file = file;
        this.updatedDate = updatedDate;
        this.imageId = imageId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.courtesy = courtesy;
        this.categoryName = categoryName;
        this.fullName = fullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCourtesy() {
        return courtesy;
    }

    public void setCourtesy(String courtesy) {
        this.courtesy = courtesy;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
