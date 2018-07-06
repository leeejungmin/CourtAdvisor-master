package com.edu.pc.courtadvisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "my.db";
    
    //Database Version
    private static final int DATABASE_VERSION = 2;
    
    // Tables Name
    private static final String TABLE_MATCH= "matchs";
    private static final String TABLE_PLAYGROUND= "playgrounds";

    // Common column names
    private static final String KEY_ID = "id";

    // MATCH Table - column names
    private static final String KEY_TEAM_ONE_NAME = "teamOneName";
    private static final String KEY_TEAM_TWO_NAME = "teamTwoName";
    private static final String KEY_TEAM_ONE_SCORE = "teamOneScore";
    private static final String KEY_TEAM_TWO_SCORE = "teamTwoScore";
//    private static final String KEY_CREATED_AT = "created_at";
    private static final String[] COLUMNS_MATCH = {KEY_ID, KEY_TEAM_ONE_NAME, KEY_TEAM_TWO_NAME, KEY_TEAM_ONE_SCORE, KEY_TEAM_TWO_SCORE};

    // PlAYGROUND Table - column names
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LNG = "longitude";
    private static final String KEY_NBR_OF_HOOPS = "number_of_hoops";
    private static final String KEY_IMAGE = "image_data";
    private static final String[] COLUMNS_PLAYGROUND = {KEY_ID, KEY_ADDRESS, KEY_LAT, KEY_LNG, KEY_NBR_OF_HOOPS, KEY_IMAGE};

    // Table Create Statements
    // MATCH Table - create statement
    private static final String CREATE_TABLE_MATCH =
            "CREATE TABLE " + TABLE_MATCH + " (" + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TEAM_ONE_NAME + " TEXT NOT NULL, " + KEY_TEAM_TWO_NAME + " TEXT NOT NULL, " + KEY_TEAM_ONE_SCORE + " INTEGER NOT NULL, " + KEY_TEAM_TWO_SCORE + " INTEGER NOT NULL);";
    private static final String CREATE_TABLE_PLAYGROUND =
            "CREATE TABLE " + TABLE_PLAYGROUND + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_ADDRESS + " TEXT NOT NULL, " + KEY_LAT + " DOUBLE NOT NULL, " + KEY_LNG + " DOUBLE NOT NULL, " + KEY_NBR_OF_HOOPS + " INTEGER NOT NULL, " + KEY_IMAGE + " BLOB);";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MATCH);
        db.execSQL(CREATE_TABLE_PLAYGROUND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w("UpgradeDB", "Upgrading database, this will drop " + TABLE_MATCH + "tables and recreate.");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYGROUND);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /*
     * MATCH CRUD
     */

    public void deleteOne(Match match) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MATCH, "id = ?",
                new String[] { String.valueOf(match.getId()) });
        db.close();

    }

    public Match getMatch(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MATCH, // a. table
                COLUMNS_MATCH, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Match match = new Match();
        match.setId(Integer.parseInt(cursor.getString(0)));
        match.setTeamOneName(cursor.getString(1));
        match.setTeamTwoName(cursor.getString(2));
        match.setTeamOneScore(Integer.parseInt(cursor.getString(3)));
        match.setTeamTwoScore(Integer.parseInt(cursor.getString(4)));
//        match.setCreatedAt(cursor.getString(5));

        return match;
    }

    public List<Match> allMatchs() {

        List<Match> matchList = new LinkedList<Match>();
        String query = "SELECT  * FROM " + TABLE_MATCH;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Match match = null;

        if (cursor.moveToFirst()) {
            do {
                match = new Match();
                match.setId(Integer.parseInt(cursor.getString(0)));
                match.setTeamOneName(cursor.getString(1));
                match.setTeamTwoName(cursor.getString(2));
                match.setTeamOneScore(Integer.parseInt(cursor.getString(3)));
                match.setTeamTwoScore(Integer.parseInt(cursor.getString(4)));
//                match.setCreatedAt(cursor.getString(5));
                matchList.add(match);
            } while (cursor.moveToNext());
        }

        return matchList;
    }

    public void addMatch(Match match) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TEAM_ONE_NAME, match.getTeamOneName());
        values.put(KEY_TEAM_TWO_NAME, match.getTeamTwoName());
        values.put(KEY_TEAM_ONE_SCORE, match.getTeamOneScore());
        values.put(KEY_TEAM_TWO_SCORE, match.getTeamTwoScore());
        // insert
        db.insert(TABLE_MATCH,null, values);
        db.close();
    }

    public int updateMatch(Match match) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TEAM_ONE_NAME, match.getTeamOneName());
        values.put(KEY_TEAM_TWO_NAME, match.getTeamTwoName());
        values.put(KEY_TEAM_ONE_SCORE, match.getTeamOneScore());
        values.put(KEY_TEAM_TWO_SCORE, match.getTeamTwoScore());

        int i = db.update(TABLE_MATCH, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(match.getId()) });

        db.close();

        return i;
    }

     /*
     * PLAYGROUND CRUD
     */

    public void deleteOne(Playground playGround) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYGROUND, "id = ?",
                new String[] { String.valueOf(playGround.getId()) });
        db.close();
    }

    public Playground getPlayground(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
        Bitmap img;
        Cursor cursor = db.query(TABLE_PLAYGROUND, // a. table
                COLUMNS_PLAYGROUND, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Playground playground = new Playground();
        playground.setId(Integer.parseInt(cursor.getString(0)));
        playground.setAddress(cursor.getString(1));
        playground.setLat(Double.parseDouble(cursor.getString(2)));
        playground.setLng(Double.parseDouble(cursor.getString(3)));

//        img = dbBitmapUtility.getImage(cursor.getBlob(4));
//        playground.setImage(img);

        return playground;
    }

    public void addPlayground(Playground playground) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
        byte[] img;

        values.put(KEY_ADDRESS, playground.getAddress());
        values.put(KEY_LAT, playground.getLat());
        values.put(KEY_LNG, playground.getLng());
        values.put(KEY_NBR_OF_HOOPS, playground.getNumberOfHoops());

//        img = dbBitmapUtility.getBytes(playground.getImage());
//        values.put(KEY_IMAGE, img);

        // insert
        db.insert(TABLE_PLAYGROUND,null, values);
        db.close();
    }

    public int updateMPlayground(Playground playground) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
        byte[] img;

        values.put(KEY_ADDRESS, playground.getAddress());
        values.put(KEY_LAT, playground.getLat());
        values.put(KEY_LNG, playground.getLng());
        values.put(KEY_NBR_OF_HOOPS, playground.getNumberOfHoops());

//        img = dbBitmapUtility.getBytes(playground.getImage());
//        values.put(KEY_IMAGE, img);

        int i = db.update(TABLE_PLAYGROUND, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(playground.getId()) });

        db.close();

        return i;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
