package com.vadim.nakatani;

import android.app.Application;

import com.vadim.nakatani.entity.PatientEntity;

/**
 * Created by Vadim on 11.11.2014.
 */
public class NakataniApplication extends Application {
    private static NakataniApplication singleton;
    private PatientEntity patientEntity;

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
}
