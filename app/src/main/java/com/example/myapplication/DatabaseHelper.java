package com.example.myapplication;

import java.util.List;
import android.util.Log;
import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "playlist_db";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FULL_NAME = "full_name";

    private static final String TABLE_PLAYLIST = "playlist";
    private static final String COLUMN_LINK_ID = "link_id";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_USER_ID_FK = "user_id_fk";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the users table
        String createUsersTableQuery = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_FULL_NAME + " TEXT"
                + ")";
        db.execSQL(createUsersTableQuery);

        // Creating the playlist table
        String createPlaylistTableQuery = "CREATE TABLE " + TABLE_PLAYLIST + "("
                + COLUMN_LINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LINK + " TEXT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + "FOREIGN KEY (" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(createPlaylistTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long addUser(String username, String password, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a ContentValues object to store the values
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_FULL_NAME, fullName);

        // Insert the values into the users table
        long userId = db.insert(TABLE_USERS, null, values);

        // Close the database connection
        db.close();

        return userId;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);

        // Close database connection
        cursor.close();
        db.close();

        return exists;
    }

    public long validateUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        long userId = -1; // Invalid user ID

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_USER_ID);
            if (columnIndex != -1) {
                userId = cursor.getLong(columnIndex);
            }
        }

        // close database connection
        cursor.close();
        db.close();

        return userId;
    }

    public boolean addLinkToPlaylist(long userId, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId); // Use the foreign key column
        values.put(COLUMN_LINK, link);

        long result = db.insert(TABLE_PLAYLIST, null, values);
        db.close();

        return result != -1;
    }

    public List<String> getPlaylist(long userId) {
        List<String> playlist = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COLUMN_LINK +
                " FROM " + TABLE_PLAYLIST +
                " WHERE " + COLUMN_USER_ID_FK + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        // checking column name through cursor
        int columnIndex = cursor.getColumnIndex(COLUMN_LINK);
        if (columnIndex == -1) {
            // handler through log
            Log.e("DatabaseHelper", "Column name not found: " + COLUMN_LINK);
            cursor.close();
            db.close();
            return playlist;
        }

        if (cursor.moveToFirst()) {
            do {
                String link = cursor.getString(columnIndex);
                playlist.add(link);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playlist;
    }

    public List<String> getPlaylistLinks() {
        List<String> playlistLinks = new ArrayList<>();

        // get links from playlist table
        String selectQuery = "SELECT * FROM " + TABLE_PLAYLIST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // moves to first row
        if (cursor != null && cursor.moveToFirst()) {
            int linkIndex = cursor.getColumnIndex(COLUMN_LINK);
            do {
                String link = cursor.getString(linkIndex);
                playlistLinks.add(link);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return playlistLinks;
    }
}
