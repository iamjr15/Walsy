package com.wizy.wallpaper.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * to record favorites we created DatabaseAdapter
 */
public class DatabaseAdapter extends SQLiteOpenHelper {

    private static DatabaseAdapter sSingleton;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "favs";
    private static final int SCHEMA_VERSION = 4;
    private static final String ITEM_KEY_ROWID = "_id";
    private static final String ITEM_TABLE = "favorite";
    private static final String IMGID = "img_id";  //unique id for db to prevent duplicate records
    private static final String URL = "url"; // image url

    // String to create the initial favs database table
    private static final String DATABASE_CREATE_ITEMS =
            "CREATE TABLE favorite (_id INTEGER PRIMARY KEY AUTOINCREMENT, img_id TEXT, url TEXT);";

    // Methods to setup database singleton and connections
    public synchronized static DatabaseAdapter getInstance(Context ctxt) {
        if (sSingleton == null) {
            sSingleton = new DatabaseAdapter(ctxt);
        }
        return sSingleton;
    }

    private DatabaseAdapter(Context ctxt) {
        super(ctxt, DATABASE_NAME, null, SCHEMA_VERSION);
        //sSingleton = this;
    }

    public void openConnection() throws SQLException {
        if (mDb == null) {
            mDb = sSingleton.getWritableDatabase();
        }
    }

    public synchronized void closeConnection() {
        if (sSingleton != null) {
            sSingleton.close();
            mDb.close();
            sSingleton = null;
            mDb = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase mDb) {
        try { //TODO sil
            mDb.beginTransaction();
            mDb.execSQL(DATABASE_CREATE_ITEMS);
            mDb.setTransactionSuccessful();

        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase mDb, int oldVersion, int newVersion) {}

    /**
     * @return return all images from db with {@link #IMGID} and {@link #URL}
     */
    public Cursor getAllItemRecords() {
        return mDb.query(ITEM_TABLE, new String[]{ITEM_KEY_ROWID, IMGID,
                        URL}, null, null, null, null,
                null);

    }

    /**
     * @param name image id, to check prevention
     * @return if image exist return true
     */
    public boolean checkItemInDb(CharSequence name) {
         Cursor cursor = mDb.query(ITEM_TABLE, new String[]{ITEM_KEY_ROWID, IMGID,
                        URL}, IMGID + "=?", new String[]{String.valueOf(name)}, null, null,
                null);
         int count =cursor.getCount();
         cursor.close();
         return count!= 0;
    }

    void deleteItemRecord(String itemName) {
        String[] whereArgs = new String[]{String.valueOf(itemName)};
        mDb.delete(ITEM_TABLE, IMGID + "=?", whereArgs);
    }

    public long insertItemRecord(String img_id, String url) {
        ContentValues initialItemValues = new ContentValues();
        initialItemValues.put(IMGID,img_id);
        initialItemValues.put(URL, url);
        return mDb.insert(ITEM_TABLE, null, initialItemValues);
    }

}
