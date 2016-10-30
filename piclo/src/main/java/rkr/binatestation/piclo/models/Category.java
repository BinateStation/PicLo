package rkr.binatestation.piclo.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import rkr.binatestation.piclo.database.PicloContract;

/**
 * Created by RKR on 08-01-2016.
 * Category.
 */
public class Category {
    private Long _id;
    private String categoryId;
    private String categoryName;

    public Category(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public static List<Category> getCategories(Cursor data) {
        List<Category> categoryList = new ArrayList<>();
        if (data != null && data.moveToFirst()) {
            do {
                categoryList.add(new Category(
                        data.getString(data.getColumnIndex(PicloContract.CategoriesEntry.COLUMN_CATEGORY_ID)),
                        data.getString(data.getColumnIndex(PicloContract.CategoriesEntry.COLUMN_CATEGORY_NAME))
                ));
            } while (data.moveToNext());
        }
        return categoryList;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return getCategoryName();
    }
}
