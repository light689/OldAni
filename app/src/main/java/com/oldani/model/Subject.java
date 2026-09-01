package com.oldani.model;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    public int id;
    public String name;
    public String nameCn;
    public String image;
    public String airDate;
    public String summary;
    public double score;
    public List<Episode> episodes = new ArrayList<>();

    public String displayName() {
        if (nameCn != null && !nameCn.isEmpty()) return nameCn;
        return name;
    }
}