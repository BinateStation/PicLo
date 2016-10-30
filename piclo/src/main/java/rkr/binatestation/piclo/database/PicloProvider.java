package rkr.binatestation.piclo.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by RKR on 30/10/2016.
 * PicloProvider.
 */

public class PicloProvider extends ContentProvider {
    // These codes are returned from sUriMatcher#match when the respective Uri matches.
    private static final int CATEGORY = 1;
    private static final int CATEGORY_ID = 2;
    private static final int PICTURE = 3;
    private static final int PICTURE_ID = 4;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RKRsPicLoSQLiteHelper mOpenHelper;
    private ContentResolver mContentResolver;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PicloContract.CONTENT_AUTHORITY;

        // For each type of URI to add, create a corresponding code.
        matcher.addURI(authority, PicloContract.PATH_CATEGORIES, CATEGORY);
        matcher.addURI(authority, PicloContract.PATH_CATEGORIES + "/*", CATEGORY_ID);

        // Search related URIs.
        matcher.addURI(authority, PicloContract.PATH_PICTURES, PICTURE);
        matcher.addURI(authority, PicloContract.PATH_PICTURES + "/*", PICTURE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context != null) {
            mContentResolver = context.getContentResolver();
            mOpenHelper = new RKRsPicLoSQLiteHelper(context);
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PicloContract.CategoriesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CATEGORY_ID: {
                long categoryId = ContentUris.parseId(uri);
                selection = PicloContract.CategoriesEntry._ID + " = ?";
                selectionArgs = new String[]{"" + categoryId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PicloContract.CategoriesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PICTURE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PicloContract.PicturesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PICTURE_ID: {
                long categoryId = ContentUris.parseId(uri);
                selection = PicloContract.CategoriesEntry._ID + " = ?";
                selectionArgs = new String[]{"" + categoryId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PicloContract.PicturesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        retCursor.setNotificationUri(mContentResolver, uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            // The application is querying the db for its own contents.
            case CATEGORY:
                return PicloContract.CategoriesEntry.CONTENT_TYPE;
            case CATEGORY_ID:
                return PicloContract.CategoriesEntry.CONTENT_TYPE;

            // The Android TV global search is querying our app for relevant content.
            case PICTURE:
                return PicloContract.PicturesEntry.CONTENT_TYPE;
            case PICTURE_ID:
                return PicloContract.PicturesEntry.CONTENT_TYPE;

            // We aren't sure what is being asked of us.
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final Uri returnUri;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CATEGORY: {
                long _id = mOpenHelper.getWritableDatabase().insert(
                        PicloContract.CategoriesEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PicloContract.CategoriesEntry.buildCategoryUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PICTURE: {
                long _id = mOpenHelper.getWritableDatabase().insert(
                        PicloContract.PicturesEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PicloContract.PicturesEntry.buildPictureUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        mContentResolver.notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case CATEGORY: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        PicloContract.CategoriesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case PICTURE: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        PicloContract.PicturesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsDeleted != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CATEGORY: {
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        PicloContract.CategoriesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case PICTURE: {
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        PicloContract.PicturesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsUpdated != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case CATEGORY: {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                int returnCount = 0;

                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(PicloContract.CategoriesEntry.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                mContentResolver.notifyChange(uri, null);
                return returnCount;
            }
            case PICTURE: {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                int returnCount = 0;

                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(PicloContract.PicturesEntry.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                mContentResolver.notifyChange(uri, null);
                return returnCount;
            }
            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }
}

