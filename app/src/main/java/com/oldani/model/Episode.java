package com.oldani.model;

public class Episode {
    public int id;
    public float sort;
    public String name;
    public String nameCn;

    public String displayName() {
        if (nameCn != null && !nameCn.isEmpty()) return nameCn;
        if (name != null && !name.isEmpty()) return name;
        return "第" + ((int) sort) + "集";
    }
}