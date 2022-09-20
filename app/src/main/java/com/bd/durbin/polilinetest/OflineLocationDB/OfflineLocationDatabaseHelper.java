package com.bd.durbin.polilinetest.OflineLocationDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.bd.durbin.polilinetest.OflineLocationDB.model.OfflineLocationModel;

import java.util.ArrayList;
import java.util.List;

public class OfflineLocationDatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "oklijm";
    public static Integer VERSTION = 1;
    public static String TABLE_NAME = "oflinetable";

    private Context context;
    public static String COL_1 = "id";
    public static String COL_2 = "jsondatalocation";


    public OfflineLocationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSTION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_QUERY = "CREATE TABLE "
                + TABLE_NAME + "("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + COL_2 + " TEXT"
                 + ")";

        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertToOfflineData(OfflineLocationModel dataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_2, dataModel.getJsondata());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public List<OfflineLocationModel> getAllOflineLocationData() {
        List<OfflineLocationModel> modellist = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                OfflineLocationModel model = new OfflineLocationModel();
                model.setId(Integer.parseInt(cursor.getString(0)));
                model.setJsondata(cursor.getString(1));

                modellist.add(model);

            } while (cursor.moveToNext());
        }

        return modellist;
    }

    public void deleteAllOfflineLocation() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, null, null);

     /*
        db.delete(TABLE_NAME, COL_1 + " = ?",
                new String[]{String.valueOf(dataModel.getId())});*/

        db.close();
    }
}
