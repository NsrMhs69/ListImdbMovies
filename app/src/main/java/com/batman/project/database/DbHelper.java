package com.batman.project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.batman.project.models.Details;
import com.batman.project.models.Movie;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {

    public static String path = "";
    public SQLiteDatabase sqLiteDatabase;
    public static final String DB_NAME = "movie.db";
    public static final String TBL_MOVIES = "data";
    public static final String TBL_DETAIL = "detail";
    public static final String IDColumn = "Id";
    public static final String IMDBColumn = "idImdb";
    public static final String titleColumn = "title";
    public static final String yearColumn = "year";
    public static final String ageColumn = "age";
    public static final String releaseColumn = "released";
    public static final String runtimeColumn = "time";
    public static final String genreColumn = "genre";
    public static final String writerColumn = "writer";
    public static final String directorColumn = "director";
    public static final String actorsColumn = "actors";
    public static final String posterColumn = "poster";
    public static final String ratingimdbColumn = "ratingimdb";
    public static final String typeColumn = "Type";
    public static final String voteColumn = "vote";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 3);
        path = "data/data/" + context.getPackageName() + "/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query, query2;
        query = (" CREATE TABLE " + TBL_MOVIES + "(Id INTEGER PRIMARY KEY AutoIncrement ,idImdb TEXT ,title TEXT,year TEXT , Type TEXT , poster TEXT)");
        query2 = (" CREATE TABLE " + TBL_DETAIL + "(Id INTEGER PRIMARY KEY AutoIncrement , idImdb TEXT, age TEXT, released TEXT,time TEXT,genre TEXT, director TEXT, writer TEXT,actors TEXT, ratingimdb TEXT,vote TEXT)");
        db.execSQL(query);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query, query2;
        query = (" DROP TABLE IF EXISTS " + TBL_MOVIES);
        query2 = (" DROP TABLE IF EXISTS " + TBL_DETAIL);
        db.execSQL(query);
        db.execSQL(query2);
        onCreate(db);

    }

    public boolean insertDataDetail(int id, String idImdb, String age, String release, String runtime, String genre, String directors, String writers, String actors, String rate, String vote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IDColumn, id);
        cv.put(IMDBColumn, idImdb);
        cv.put(ageColumn, age);
        cv.put(releaseColumn, release);
        cv.put(runtimeColumn, runtime);
        cv.put(genreColumn, genre);
        cv.put(directorColumn, directors);
        cv.put(writerColumn, writers);
        cv.put(actorsColumn, actors);
        cv.put(ratingimdbColumn, rate);
        cv.put(voteColumn, vote);
        long result = db.insert(TBL_DETAIL, null, cv);
        if (result == -1)
            return false;
        else
            return true;
    }




    public boolean insertData(int id, String idImdb, String title, String year, String type, String poster) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IDColumn, id);
        cv.put(IMDBColumn, idImdb);
        cv.put(titleColumn, title);
        cv.put(yearColumn, year);
        cv.put(typeColumn, type);
        cv.put(posterColumn, poster);
        long result = db.insert(TBL_MOVIES, null, cv);
        if (result == -1)
            return false;
        else
            return true;
    }

    public void open() {
        sqLiteDatabase = SQLiteDatabase.openDatabase(path + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public List<Movie> getMovieList() {
        List<Movie> movieList = new ArrayList<>();
        movieList.clear();
        open();
        Cursor cityCursor = sqLiteDatabase.rawQuery("SELECT * FROM data", null);
        while (cityCursor.moveToNext()) {
            movieList.add(new Movie(cityCursor.getInt(cityCursor.getColumnIndex(IDColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(IMDBColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(titleColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(yearColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(typeColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(posterColumn))

            ));
        }
        cityCursor.close();
        sqLiteDatabase.close();
        return movieList;
    }
    public List<Details> getDetail(String id) {
        List<Details> movieList = new ArrayList<>();
        movieList.clear();
        open();
        Cursor cityCursor = sqLiteDatabase.rawQuery("select * from " + TBL_DETAIL + " where idImdb='" + id + "' group by id", null);
        while (cityCursor.moveToNext()) {
            movieList.add(new Details(cityCursor.getInt(cityCursor.getColumnIndex(IDColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(IMDBColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(ageColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(releaseColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(runtimeColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(genreColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(directorColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(writerColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(actorsColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(ratingimdbColumn)),
                    cityCursor.getString(cityCursor.getColumnIndex(voteColumn))

            ));
        }
        cityCursor.close();
        sqLiteDatabase.close();
        return movieList;
    }

    public boolean rowIdExists(String StrId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select Id from " + TBL_DETAIL
                + " where idImdb=?", new String[]{StrId});
        boolean exists = (cursor.getCount() > 0);
        return exists;
    }

}
