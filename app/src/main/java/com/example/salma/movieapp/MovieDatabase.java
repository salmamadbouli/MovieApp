package com.example.salma.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;


public class MovieDatabase extends SQLiteOpenHelper {
    public Context context;

    public static final int DATABASEVERSION =15;
    public static final String MovieDBName = "Favourite";
    public static final String Table = "favourite_table";

    public static final String id = "id";
    public static final String poster= "poster";
    public static final String title= "title";
    public static final String released_date= "date";
    public static final String rate= "rate";
    public static final String synopsis= "synopsis";
    public final static String CREATE_TABLE = "CREATE TABLE " + Table + "("
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + poster + " VARCHAR(250) ,"
            + title + " CHAR(250) ,"
            + released_date + " VARCHAR(250) ,"
            + rate + " VARCHAR(250) ,"
            + synopsis +");";


    String DropTable = "DROP TABLE IF EXISTS " + Table;


    public MovieDatabase(Context context) {
        super(context,MovieDBName, null, DATABASEVERSION);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Toast.makeText(context,"DB Created",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DropTable);
        Toast.makeText(context,"DB to Update",Toast.LENGTH_LONG).show();

        onCreate(db);
    }

    public long insetInDb(ContentValues values){
        SQLiteDatabase db=this.getWritableDatabase();
        long n=   db.insert(Table,null,values);
        Toast.makeText(context,getFavsMovies().size()+"",Toast.LENGTH_LONG).show();

        return  n;


    }

    public ArrayList<Movie> getFavsMovies(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {id,poster,title,released_date,rate,synopsis};
        Cursor cursor = db.query(Table,columns, null, null, null, null, null);
        ArrayList<Movie> movies_data = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Movie movie =new Movie();
                movie.setId(String.valueOf(cursor.getInt(0)));
                movie.setId(cursor.getString(cursor.getColumnIndex(id)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(poster)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(title)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(released_date)));
                movie.setRating( cursor.getString(cursor.getColumnIndex(rate)));
                movie.setSynopsis(cursor.getString(cursor.getColumnIndex(synopsis)));
                movies_data.add(movie);
            } while (cursor.moveToNext());
        }

        return movies_data;

    }

    public void delete_movie(String Mid){
        SQLiteDatabase db = this.getWritableDatabase();
        int n= db.delete(Table,Mid +"="+id,null);
        if (n==0){
            Toast.makeText(context,"not deleted",Toast.LENGTH_LONG).show();

        }
        else         Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show();

        db.close();
    }

    public boolean IfExist(String Mid){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {id};
        String []args =new String[] { Mid };
        Cursor cursor = db.query(Table, columns, id + "=?",args, null, null, null, null);
        if (cursor.getCount()== 0)
            return false;
        else
            return true;
    }



}
