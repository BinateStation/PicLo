package rkr.binatestation.piclo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static rkr.binatestation.piclo.database.PicloContract.CategoriesEntry.SQL_CREATE_CATEGORY_TABLE;
import static rkr.binatestation.piclo.database.PicloContract.PicturesEntry.SQL_CREATE_PICTURES_TABLE;

class RKRsPicLoSQLiteHelper extends SQLiteOpenHelper {

    // The name of our database.
    private static final String DATABASE_NAME = "piclo.db";
    private static int DB_VERSION = 3;

    RKRsPicLoSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // Do the creating of the databases.
        database.execSQL(SQL_CREATE_CATEGORY_TABLE);
        database.execSQL(SQL_CREATE_PICTURES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simply discard all old data and start over when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + PicloContract.CategoriesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PicloContract.PicturesEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do the same thing as upgrading...
        onUpgrade(db, oldVersion, newVersion);
    }
}