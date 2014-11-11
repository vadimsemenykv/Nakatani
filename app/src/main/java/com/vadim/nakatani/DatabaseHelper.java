package com.vadim.nakatani;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Vadim on 26.10.2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns{

    private static final String DB_NAME = "Nakatani.sqlite";
    private static final int DB_VERSION = 1;

    /* */
    private static final String DB_FOLDER = "/data/data/" + /*App.getInstance().getPackageName()*/ "com.vadim.nakatani" + "/databases/";
    private static final String DB_PATH = DB_FOLDER + DB_NAME;
    private static final String DB_ASSETS_PATH = "db/" + DB_NAME;
    private static final int DB_FILES_COPY_BUFFER_SIZE = 8192;

    public static final String DB_TABLE = "cats";
    public static final String CAT_NAME_COLUMN = "cat_name";
    public static final String PHONE_COLUMN = "phone";
    public static final String AGE_COLUMN = "age";

    public static class ActionTable {
        public static String TABLE_NAME = "Action";
    }

    public static class PatientTable {
        public static final String TABLE_NAME = "Patient";
        public static final String CODE_COLUMN = "code";
        public static final String ID_DOCTOR_COLUMN = "idDoctor";
        public static final String LAST_NAME_COLUMN = "lastName";
        public static final String FIRST_NAME_COLUMN = "firstName";
        public static final String MIDDLE_NAME_COLUMN = "middleName";
        public static final String ID_SEX_COLUMN = "idSex";
//        public static final String LAST_NAME_COLUMN = "lastName";

    }

    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        mContext = context;
    }

    //TODO переписать на английском
    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();

        if(dbExist){
            //ничего не делать - база уже есть
        }else{
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
//            String myPath = DB_PATH;
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //база еще не существует
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
    private void copyDataBase() throws IOException{
        //Открываем локальную БД как входящий поток
        InputStream myInput = mContext.getAssets().open(DB_ASSETS_PATH);

        //Путь ко вновь созданной БД
        String outFileName = DB_PATH;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(DATABASE_CREATE_SCRIPT);
//
//        ContentValues newValues = new ContentValues();
//        // Задайте значения для каждой строки.
//        newValues.put(CAT_NAME_COLUMN, "SAsfg");
//        newValues.put(PHONE_COLUMN, "4954553443");
//        newValues.put(AGE_COLUMN, "5");
//        // Вставляем данные в базу
//        db.insert("cats", null, newValues);
//
//        newValues.put(CAT_NAME_COLUMN, "vjhjvj");
//        newValues.put(PHONE_COLUMN, "4954553443");
//        newValues.put(AGE_COLUMN, "5");
//        // Вставляем данные в базу
//        db.insert("cats", null, newValues);
//
//        newValues.put(CAT_NAME_COLUMN, "viof");
//        newValues.put(PHONE_COLUMN, "4954553443");
//        newValues.put(AGE_COLUMN, "5");
//        // Вставляем данные в базу
//        db.insert("cats", null, newValues);
//
//        newValues.put(CAT_NAME_COLUMN, "fguou");
//        newValues.put(PHONE_COLUMN, "4954553443");
//        newValues.put(AGE_COLUMN, "5");
//        // Вставляем данные в базу
//        db.insert("cats", null, newValues);
//
//        newValues.put(CAT_NAME_COLUMN, "kghyugo");
//        newValues.put(PHONE_COLUMN, "4954553443");
//        newValues.put(AGE_COLUMN, "5");
//        // Вставляем данные в базу
//        db.insert("cats", null, newValues);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // TODO Auto-generated method stub
//        // Запишем в журнал
//        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
//
//        // Удаляем старую таблицу и создаём новую
//        db.execSQL("DROP TABLE IF IT EXIST " + DB_TABLE);
//        // Создаём новую таблицу
//        onCreate(db);
//    }
}
