package com.vadim.nakatani.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.vadim.nakatani.DatabaseHelper;
import com.vadim.nakatani.entity.PatientEntity;
import com.vadim.nakatani.entity.ResultEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vadim on 12.11.14.
 */
public class ResultDAO {
    private Context context;

    public ResultDAO(Context context) {
        this.context = context;
    }

    public ResultEntity getResult(String code) {
        throw new UnsupportedOperationException();
    }

    public void addResult(ResultEntity resultEntity) {
        throw new UnsupportedOperationException();
    }

    public List<ResultEntity> getAllResultsForPatient(PatientEntity patientEntity) {
        List<ResultEntity> resultEntityList = new ArrayList<ResultEntity>();
        SQLiteDatabase sqLiteDatabase = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.createDataBase();
            sqLiteDatabase = dbHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Result WHERE idPatient = " + patientEntity.get_id(), null);
            Log.d(this.getClass().getName(), String.valueOf(patientEntity.get_id()));
            if (cursor != null ) {
                Log.d(this.getClass().getName(), "Cursor != null");
                if  (cursor.moveToFirst()) {
                    Log.d(this.getClass().getName(), "Cursor move");
                    do {
                        ResultEntity res = new ResultEntity();

                        res.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                        res.setIdPatient(cursor.getInt(cursor.getColumnIndex("idPatient")));
                        res.setCode(cursor.getString(cursor.getColumnIndex("code")));
                        res.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        res.setTime(cursor.getString(cursor.getColumnIndex("time")));

                        List<Integer> pointsValueList = new ArrayList<Integer>();

                        List<String> pointsColumnNamesList = new ArrayList<String>();
                        pointsColumnNamesList.add("H1L");
                        pointsColumnNamesList.add("H2L");
                        pointsColumnNamesList.add("H3L");
                        pointsColumnNamesList.add("H4L");
                        pointsColumnNamesList.add("H5L");
                        pointsColumnNamesList.add("H6L");

                        pointsColumnNamesList.add("H1R");
                        pointsColumnNamesList.add("H2R");
                        pointsColumnNamesList.add("H3R");
                        pointsColumnNamesList.add("H4R");
                        pointsColumnNamesList.add("H5R");
                        pointsColumnNamesList.add("H6R");

                        pointsColumnNamesList.add("F1L");
                        pointsColumnNamesList.add("F2L");
                        pointsColumnNamesList.add("F3L");
                        pointsColumnNamesList.add("F4L");
                        pointsColumnNamesList.add("F5L");
                        pointsColumnNamesList.add("F6L");

                        pointsColumnNamesList.add("F1R");
                        pointsColumnNamesList.add("F2R");
                        pointsColumnNamesList.add("F3R");
                        pointsColumnNamesList.add("F4R");
                        pointsColumnNamesList.add("F5R");
                        pointsColumnNamesList.add("F6R");

                        for (String colName : pointsColumnNamesList) {
                            pointsValueList.add(cursor.getInt(cursor.getColumnIndex(colName)));
                        }
                        res.setPointsValue(pointsValueList);

                        Log.d(this.getClass().getName(), res.getCode());
                        resultEntityList.add(res);
                    }while (cursor.moveToNext());
                }
            }
        } catch (SQLiteException sqLiteException ) {
            Log.e(getClass().getSimpleName(), "Could not read from db");
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Exception in DB HELPER when create database");
        } finally {
            if (sqLiteDatabase != null) sqLiteDatabase.close();
        }
        return resultEntityList;
    }

    public List<ResultEntity> getAllResults() {
        List<ResultEntity> resultEntityList = new ArrayList<ResultEntity>();
        SQLiteDatabase sqLiteDatabase = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.createDataBase();
            sqLiteDatabase = dbHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Result", null);
            if (cursor != null ) {
                if  (cursor.moveToFirst()) {
                    do {
                        ResultEntity res = new ResultEntity();

                        res.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                        res.setIdPatient(cursor.getInt(cursor.getColumnIndex("idPatient")));
                        res.setCode(cursor.getString(cursor.getColumnIndex("code")));
                        res.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        res.setTime(cursor.getString(cursor.getColumnIndex("time")));

                        List<Integer> pointsValueList = new ArrayList<Integer>();

                        List<String> pointsColumnNamesList = new ArrayList<String>();
                        pointsColumnNamesList.add("H1L");
                        pointsColumnNamesList.add("H2L");
                        pointsColumnNamesList.add("H3L");
                        pointsColumnNamesList.add("H4L");
                        pointsColumnNamesList.add("H5L");
                        pointsColumnNamesList.add("H6L");

                        pointsColumnNamesList.add("H1R");
                        pointsColumnNamesList.add("H2R");
                        pointsColumnNamesList.add("H3R");
                        pointsColumnNamesList.add("H4R");
                        pointsColumnNamesList.add("H5R");
                        pointsColumnNamesList.add("H6R");

                        pointsColumnNamesList.add("F1L");
                        pointsColumnNamesList.add("F2L");
                        pointsColumnNamesList.add("F3L");
                        pointsColumnNamesList.add("F4L");
                        pointsColumnNamesList.add("F5L");
                        pointsColumnNamesList.add("F6L");

                        pointsColumnNamesList.add("F1R");
                        pointsColumnNamesList.add("F2R");
                        pointsColumnNamesList.add("F3R");
                        pointsColumnNamesList.add("F4R");
                        pointsColumnNamesList.add("F5R");
                        pointsColumnNamesList.add("F6R");

                        for (String colName : pointsColumnNamesList) {
                            pointsValueList.add(cursor.getInt(cursor.getColumnIndex(colName)));
                        }

                        resultEntityList.add(res);
                    }while (cursor.moveToNext());
                }
            }
        } catch (SQLiteException sqLiteException ) {
            Log.e(getClass().getSimpleName(), "Could not read from db");
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Exception in DB HELPER when create database");
        } finally {
            if (sqLiteDatabase != null) sqLiteDatabase.close();
        }
        return resultEntityList;
    }

    public void upgradeResult(ResultEntity resultEntity) {
        throw new UnsupportedOperationException();
    }
}
