package com.vadim.nakatani.entity;

/**
 * Created by vadim on 23.10.14.
 */
public final class PatientEntity {
    private static PatientEntity patientEntity;

    private String userID;
    private String userName;
//    private String userFirName;
//    private String userSerName;
    private String userAge;

    public static synchronized PatientEntity getUserInstance(){
        if(patientEntity == null) patientEntity = new PatientEntity();
        return patientEntity;
    }
    private PatientEntity(){}

    public synchronized void initializeUserData(String userID, String userName, String userAge){
        this.userID = userID;
        this.userName = userName;
        this.userAge = userAge;
    }
}
