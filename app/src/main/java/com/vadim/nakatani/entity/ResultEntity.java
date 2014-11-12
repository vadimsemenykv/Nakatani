package com.vadim.nakatani.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vadim on 12.11.14.
 */
public class ResultEntity {
    private int _id;
    private int idPatient;
    private String code;
    /*h1_left h2_left ... h6_left h1_right...h6_right f1_left...f6_right*/
    private List<Integer> pointsValue = new ArrayList<Integer>();
    private String date;
    private String time;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Integer> getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(List<Integer> pointsValue) {
        this.pointsValue = pointsValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
