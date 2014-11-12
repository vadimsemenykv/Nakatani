package com.vadim.nakatani;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vadim.nakatani.entity.PatientEntity;
import com.vadim.nakatani.entity.ResultEntity;

import java.io.IOException;

/**
 * Created by Vadim on 11.11.2014.
 */
public class NakataniApplication extends Application {
    private static NakataniApplication singleton;
    private PatientEntity patientEntity;
    /*Selected result*/
    private ResultEntity resultEntity;


    public NakataniApplication getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public PatientEntity getPatientEntity() {
        return patientEntity;
    }

    public void setPatientEntity(PatientEntity patientEntity) {
        this.patientEntity = patientEntity;
    }

    public ResultEntity getResultEntity() {
        return resultEntity;
    }

    public void setResultEntity(ResultEntity resultEntity) {
        this.resultEntity = resultEntity;
    }
}
