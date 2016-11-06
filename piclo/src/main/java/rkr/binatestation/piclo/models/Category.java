package rkr.binatestation.piclo.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import static rkr.binatestation.piclo.database.PicloContract.CategoriesEntry.COLUMN_CATEGORY_ID;
import static rkr.binatestation.piclo.database.PicloContract.CategoriesEntry.COLUMN_CATEGORY_NAME;

/**
 * Created by RKR on 08-01-2016.
 * Category.
 */
public class Category {
    private int categoryId;
    private String categoryName;

    private Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public static List<Category> getCategories(Cursor data) {
        List<Category> categoryList = new ArrayList<>();
        if (data != null && data.moveToFirst()) {
            do {
                try {
                    categoryList.add(new Category(
                            data.getInt(data.getColumnIndex(COLUMN_CATEGORY_ID)),
                            data.getString(data.getColumnIndex(COLUMN_CATEGORY_NAME))
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (data.moveToNext());
        }
        return categoryList;
    }

    public int getCategoryId() {
        return categoryId;
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
