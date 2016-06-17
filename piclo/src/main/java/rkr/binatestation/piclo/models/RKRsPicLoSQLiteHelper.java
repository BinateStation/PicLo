package rkr.binatestation.piclo.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RKRsPicLoSQLiteHelper extends SQLiteOpenHelper {

    Context context;

    public RKRsPicLoSQLiteHelper(Context context) throws Exception {
        super(context, context.getPackageName(), null, context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0).versionCode);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(new Categories(context).DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RKRsPicLoSQLiteHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Categories.TABLE);
        onCreate(db);
    }
}