package com.vadim.nakatani.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.vadim.nakatani.DatabaseHelper;
import com.vadim.nakatani.NakataniApplication;
import com.vadim.nakatani.entity.PatientEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vadim on 12.11.14.
 */
public class PatientDAO {
    private Context context;

    public PatientDAO(Context context) {
        this.context = context;
    }

    public PatientEntity getPatient(String code) {
        throw new UnsupportedOperationException();
    }

    public void addPatient(PatientEntity patientEntity) {
        throw new UnsupportedOperationException();
    }

    public List<PatientEntity> getAllPatients() {
        List<PatientEntity> patientEntities = new ArrayList<PatientEntity>();
        SQLiteDatabase sqLiteDatabase = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.createDataBase();
            sqLiteDatabase = dbHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Patient", null);
            if (cursor != null ) {
                if  (cursor.moveToFirst()) {
                    do {
                        PatientEntity pat = new PatientEntity();

                        pat.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                        pat.setCode(cursor.getString(cursor.getColumnIndex("code")));
                        pat.setIdDoctor(cursor.getInt(cursor.getColumnIndex("idDoctor")));
                        pat.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
                        pat.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
                        pat.setMiddleName(cursor.getString(cursor.getColumnIndex("middleName")));
                        pat.setIdSex(cursor.getInt(cursor.getColumnIndex("idSex")));
                        pat.setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
                        pat.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                        pat.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                        pat.setWork(cursor.getString(cursor.getColumnIndex("work")));
                        pat.setPosition(cursor.getString(cursor.getColumnIndex("position")));
                        pat.setProfession(cursor.getString(cursor.getColumnIndex("profession")));
                        pat.setChildren(cursor.getInt(cursor.getColumnIndex("children")));
                        pat.setCouple(cursor.getString(cursor.getColumnIndex("couple")));
                        pat.setNotes(cursor.getString(cursor.getColumnIndex("notes")));
                        pat.setFillDate(cursor.getString(cursor.getColumnIndex("fillDate")));
                        pat.setLastVisit(cursor.getString(cursor.getColumnIndex("lastVisit")));

                        patientEntities.add(pat);
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
        return patientEntities;
    }

    public void upgradePatient(PatientEntity patientEntity) {
        throw new UnsupportedOperationException();
    }
}
