package com.vadim.nakatani.entity;

/**
 * Created by vadim on 23.10.14.
 */
public final class PatientEntity {
    private static PatientEntity patientEntity;

    private String patientID;
    private String patientName;
//    private String userFirName;
//    private String userSerName;
    private String patientAge;

    public static synchronized PatientEntity getPatientInstance(){
        if(patientEntity == null) patientEntity = new PatientEntity();
        return patientEntity;
    }
    private PatientEntity(){}

    public synchronized void initializePatientData(String userID, String userName, String userAge){
        this.patientID = userID;
        this.patientName = userName;
        this.patientAge = userAge;
    }

    public static boolean isExist() {
        return patientEntity != null ? true : false;
    }

    public static void invalidatePatient() {
        patientEntity = null;
    }
}
