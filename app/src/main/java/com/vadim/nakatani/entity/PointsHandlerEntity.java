package com.vadim.nakatani.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vadim on 12.11.14.
 */
public class PointsHandlerEntity {
    public List<String> namesList = new ArrayList<String>();
    public List<String> fullNamesList = new ArrayList<String>();
    public List<String> shortNamesList = new ArrayList<String>();
    public List<String> meridiansList = new ArrayList<String>();
    public List<String> signList = new ArrayList<String>();

    public PointsHandlerEntity(List<String> namesList, List<String> fullNamesList, List<String> shortNamesList, List<String> meridiansList, List<String> signList) {
        this.namesList = namesList;
        this.fullNamesList = fullNamesList;
        this.shortNamesList = shortNamesList;
        this.meridiansList = meridiansList;
        this.signList = signList;
    }
}
