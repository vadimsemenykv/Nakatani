package com.vadim.nakatani.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vadim on 23.10.14.
 */
public final class PatientEntity {

    int _id;
    String code;
    int idDoctor;
    String lastName;
    String firstName;
    String middleName;
    int idSex;
    String birthday;
    String address;
    String email;
    String work;
    String position;
    String profession;
    int children;
    String couple;
    String notes;
    String fillDate;
    String lastVisit;

    List<ResultEntity> resultEntityList = new ArrayList<ResultEntity>();
    List<PhoneEntity> phoneEntityList = new ArrayList<PhoneEntity>();

    public PatientEntity() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public int getIdSex() {
        return idSex;
    }

    public void setIdSex(int idSex) {
        this.idSex = idSex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public String getCouple() {
        return couple;
    }

    public void setCouple(String couple) {
        this.couple = couple;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFillDate() {
        return fillDate;
    }

    public void setFillDate(String fillDate) {
        this.fillDate = fillDate;
    }

    public String getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(String lastVisit) {
        this.lastVisit = lastVisit;
    }

    public List<ResultEntity> getResultEntityList() {
        return resultEntityList;
    }

    public void setResultEntityList(List<ResultEntity> resultEntityList) {
        this.resultEntityList = resultEntityList;
    }

    public List<PhoneEntity> getPhoneEntityList() {
        return phoneEntityList;
    }

    public void setPhoneEntityList(List<PhoneEntity> phoneEntityList) {
        this.phoneEntityList = phoneEntityList;
    }

    static String generateCode(String lastName) {
        if (lastName != null) {
            String code = lastName.trim();
            if (code.length() >= 3) {
                code = (code.substring(0, 3)).toUpperCase();
            } else {
                code += "ААА";
                code = (code.substring(0, 3)).toUpperCase();
            }
            return code;
        }
        throw new IllegalArgumentException("lastName was null");
    }
}
