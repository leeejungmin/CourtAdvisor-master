//package com.edu.pc.courtadvisor;
//
//import android.os.AsyncTask;
//
//public class InsertDatabaseTask extends AsyncTask<PlayGround, Void, Void> {
//
//    private final AppDatabase database;
//
//    public InsertDatabaseTask(AppDatabase database) {
//        this.database = database;
//    }
//
//    @Override
//    protected Void doInBackground(PlayGround... playgrounds) {
//        PlayGround playGround[] = playgrounds;
//
//        database.PlaygroundDao().insertPlayGround(playGround);
//        return null;
//    }
//}
//
