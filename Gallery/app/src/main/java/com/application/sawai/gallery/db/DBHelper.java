package com.application.sawai.gallery.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gallery_db";
    private static final String FILE_PATH = "file_path";
    private static final String USER_TABLE = "user";
    private static final String KEY_ID = "id";
    private static final String KEY_PATH = "path";
    public static final String CREATE_FILE_PATH_TABLE = "CREATE TABLE " + FILE_PATH + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PATH + " TEXT"
            + ")";
    private static final String USER_ID = "uuid";
    public static final String CREATE_USER_ID_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + USER_ID + " TEXT"
            + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FILE_PATH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FILE_PATH);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        // Creating tables again
        onCreate(db);
    }

    public void addPath(String filepath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PATH, filepath); // Shop Name
        db.insert(FILE_PATH, null, values);
        db.close(); // Closing database connection
    }

    public void addUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PATH, SystemClock.currentThreadTimeMillis()); // Shop Name
        db.insert(USER_TABLE, null, values);
        db.close();
    }

    public boolean fileExistInDb(String filePath) {
        boolean result = true;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FILE_PATH, new String[]{KEY_ID,  KEY_PATH}, KEY_PATH + "=?",  new String[]{String.valueOf(filePath)}, null, null, null, null);
        if (cursor == null || cursor.getCount() == 0){
            result = false;
        }
        return result;
    }

    public String getUserId() {
        /*SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FILE_PATH, new String[]{KEY_ID,
                        KEY_PATH}, KEY_ID + "=?",
                new String[]{String.valueOf(0)}, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            return cursor.getString(1);
        }
        else {
            addUser();
        }*/
        return "common";
    }
}
