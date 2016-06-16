package rkr.binatestation.piclo.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RKR on 08-01-2016.
 * Categories.
 */
public class Categories {
    public static final String TABLE = "Categories";
    public String[] allColumns = {
            "_id",
            "categoryId",
            "categoryName"};
    public final String DATABASE_CREATE = "create table "
            + TABLE + "("
            + allColumns[0] + " integer primary key autoincrement , "
            + allColumns[1] + " text not null , "
            + allColumns[2] + " text  );";
    Long _id;
    String categoryId;
    String categoryName;

    Context context;
    private SQLiteDatabase database;
    private RKRsPicLoSQLiteHelper dbHelper;

    public Categories(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Categories(Context context) {
        this.context = context;
        try {
            dbHelper = new RKRsPicLoSQLiteHelper(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Categories insert(Categories obj) {
        ContentValues values = new ContentValues();
        values.put(allColumns[1], obj.getCategoryId());
        values.put(allColumns[2], obj.getCategoryName());

        long insertId;
        if (getRow(obj.getCategoryId()) == null) {
            insertId = database.insert(TABLE, null, values);

            Log.i("InsertID", "Categories : " + insertId);

            Cursor cursor = database.query(TABLE, allColumns, allColumns[0] + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();
            Categories newObj = cursorToCategories(cursor);
            cursor.close();
            return newObj;
        } else {
            return updateRow(obj);
        }
    }

    public Categories updateRow(Categories obj) {

        ContentValues values = new ContentValues();
        values.put(allColumns[1], obj.getCategoryId());
        values.put(allColumns[2], obj.getCategoryName());

        database.update(TABLE, values, allColumns[1] + " = '" + obj.getCategoryId() + "'", null);
        System.out.println("Categories Row Updated with id: " + obj.getCategoryId());
        Cursor cursor = database.query(TABLE, allColumns, allColumns[1] + " = '" + obj.getCategoryId() + "'", null,
                null, null, null);
        cursor.moveToFirst();
        Categories newObj = cursorToCategories(cursor);
        cursor.close();
        return newObj;
    }

    public void deleteRow(Categories obj) {
        database.delete(TABLE, allColumns[1] + " = " + obj.getCategoryId(), null);
        System.out.println("Categories Row deleted with id: " + obj.getCategoryId());
    }

    public void deleteAll() {
        database.delete(TABLE, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public List<Categories> getAllRows() {
        List<Categories> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE, allColumns, null, null, null, null, allColumns[1] + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Categories obj = cursorToCategories(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    public Categories getRow(String categoryId) {
        Cursor cursor = database.query(TABLE, allColumns, allColumns[1] + " = '" + categoryId + "'", null, null, null, null);
        if (cursor.moveToFirst()) {
            return cursorToCategories(cursor);
        }
        cursor.close();
        return null;
    }

    private Categories cursorToCategories(Cursor cursor) {
        Categories obj = new Categories(cursor.getString(1), cursor.getString(2));
        obj.set_id(cursor.getLong(0));
        return obj;
    }
}
