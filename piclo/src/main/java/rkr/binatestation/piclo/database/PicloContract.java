package rkr.binatestation.piclo.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by RKR on 30/10/2016.
 * PicloContract.
 */

public final class PicloContract {

    // The name for the entire content provider.
    public static final String CONTENT_AUTHORITY = "rkr.binatestation.piclo";

    // Base of all URIs that will be used to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // The content paths.
    public static final String PATH_CATEGORIES = "categories";
    public static final String PATH_PICTURES = "pictures";

    public static final class CategoriesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_CATEGORIES;

        // Name of the category table.
        public static final String TABLE_NAME = "categories";

        // Column with the foreign key into the picture table.
        public static final String COLUMN_CATEGORY_ID = "category_id";

        // Name of the category.
        public static final String COLUMN_CATEGORY_NAME = "category_name";

        // Create a table to hold categories.
        public static final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_CATEGORY_ID + " TEXT UNIQUE NOT NULL, " +
                COLUMN_CATEGORY_NAME + " TEXT NOT NULL " +
                " );";

        // Returns the Uri referencing a category with the specified id.
        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PicturesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PICTURES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_PICTURES;

        // Name of the Picture table.
        public static final String TABLE_NAME = "pictures";

        // Title of picture
        public static final String COLUMN_TITLE = "p_title";

        // file url of the Picture.
        public static final String COLUMN_FILE = "p_file";
        // like count of the Picture.
        public static final String COLUMN_LIKE_COUNT = "p_like_count";
        // is it liked by the user.
        public static final String COLUMN_IS_LIKED = "p_is_liked";
        // updated date of the Picture.
        public static final String COLUMN_UPDATED_DATE = "p_updated_date";
        // Image id of the Picture.
        public static final String COLUMN_IMAGE_ID = "p_image_id";
        // User id who uploaded the Picture.
        public static final String COLUMN_USER_ID = "p_user_id";
        // The category id of the Picture.
        public static final String COLUMN_CATEGORY_ID = "p_category_id";
        // Courtesy of the Picture.
        public static final String COLUMN_COURTESY = "p_courtesy";
        // Category name of the Picture.
        public static final String COLUMN_CATEGORY_NAME = "p_category_name";
        // The full name of the Picture.
        public static final String COLUMN_FULL_NAME = "p_full_name";

        // Create a table to hold categories.
        public static final String SQL_CREATE_PICTURES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT , " +
                COLUMN_FILE + " TEXT , " +
                COLUMN_LIKE_COUNT + " TEXT , " +
                COLUMN_IS_LIKED + " TEXT , " +
                COLUMN_UPDATED_DATE + " TEXT , " +
                COLUMN_IMAGE_ID + " TEXT , " +
                COLUMN_USER_ID + " TEXT , " +
                COLUMN_CATEGORY_ID + " TEXT , " +
                COLUMN_COURTESY + " TEXT , " +
                COLUMN_CATEGORY_NAME + " TEXT , " +
                COLUMN_FULL_NAME + " TEXT " +
                " );";

        // Returns the Uri referencing a Picture with the specified id.
        public static Uri buildPictureUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
