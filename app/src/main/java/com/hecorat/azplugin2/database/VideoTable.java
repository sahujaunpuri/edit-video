package com.hecorat.azplugin2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class VideoTable {
    private String TABLE_NAME = "Video";
    private String ID = "Id";
    private String PROJECT_ID = "ProjectId";
    private String PATH = "Path";
    private String START_TIME = "StartTime";
    private String END_TIME = "EndTime";
    private String LEFT = "Left";
    private String ORDER = "_Order";
    private String VOLUME = "Volume";
    private String VOLUME_PREVIEW = "VolumePreview";
    private DBHelper mDbHelper;

    public VideoTable(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public void createTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "create table if not exists " + TABLE_NAME + " ("+
                ID + " integer primary key, " +
                PROJECT_ID + " integer, " +
                PATH + " text, " +
                START_TIME + " text, " +
                END_TIME + " text, " +
                LEFT + " text, " +
                ORDER + " text, " +
                VOLUME + " text, " +
                VOLUME_PREVIEW + " text)";
        sqLiteDatabase.execSQL(sql);
    }

    public void dropTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "drop table if exists " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }

    public void insertValue(VideoObject video){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROJECT_ID, video.projectId);
        contentValues.put(PATH, video.path);
        contentValues.put(START_TIME, video.startTime);
        contentValues.put(END_TIME, video.endTime);
        contentValues.put(LEFT, video.left);
        contentValues.put(ORDER, video.orderInList);
        contentValues.put(VOLUME, video.volume);
        contentValues.put(VOLUME_PREVIEW, video.volumePreview);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void deleteVideoOf(int projectId) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "delete from " + TABLE_NAME +
                " where " + PROJECT_ID + " = " + projectId;
        sqLiteDatabase.execSQL(sql);
    }

    public ArrayList<VideoObject> getData(int projectId){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        ArrayList<VideoObject> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME + " where " + PROJECT_ID + " = " + projectId;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                VideoObject video = new VideoObject();
                video.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                video.projectId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PROJECT_ID)));
                video.path = cursor.getString(cursor.getColumnIndex(PATH));
                video.startTime = cursor.getString(cursor.getColumnIndex(START_TIME));
                video.endTime = cursor.getString(cursor.getColumnIndex(END_TIME));
                video.left = cursor.getString(cursor.getColumnIndex(LEFT));
                video.orderInList = cursor.getString(cursor.getColumnIndex(ORDER));
                video.volume = cursor.getString(cursor.getColumnIndex(VOLUME));
                video.volumePreview = cursor.getString(cursor.getColumnIndex(VOLUME_PREVIEW));
                list.add(video);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

}