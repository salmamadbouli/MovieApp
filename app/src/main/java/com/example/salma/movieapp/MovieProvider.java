package com.example.salma.movieapp;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.example.salma.movieapp.MovieDatabase.Table;

public class MovieProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.example.salma.movieapp.MovieProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/Movie";
    static final Uri CONTENT_URI = Uri.parse(URL);
    private SQLiteDatabase db;
    //private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDatabase mOpenHelper;
    static final int MOVIE = 100;
    static final int MOVIE_ID = 101;


    static final UriMatcher uriMatcher;
    static {

        uriMatcher  = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "Movies", MOVIE);
        uriMatcher.addURI(PROVIDER_NAME, "Movies/id", MOVIE_ID);
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        MovieDatabase dbHelper = new MovieDatabase(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Table);
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all Movies records (dir)
             */
            case MOVIE:
                return "vnd.android.cursor.dir/vnd.example.movies";

            /**
             * Get a particular movie(item)
             */
            case MOVIE_ID:
                return "vnd.android.cursor.item/vnd.example.movies";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new Movie record
         */
        long rowID = db.insert(	Table, "", values);

        /**
         * If record is added successfully
         */

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        count = db.delete(Table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        count = db.update(Table,  values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;    }
}
