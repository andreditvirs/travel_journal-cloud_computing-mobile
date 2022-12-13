package com.uwika.traveljournal;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LastJournalModel {
    String title, date, month_year, real_cover, uuid, type;
    int cover;

    public LastJournalModel(String title, String date, String month_year, int cover, String real_cover, String uuid, String type){
        this.title = title;
        this.date = date;
        this.month_year = month_year;
        this.cover = cover;
        this.real_cover = real_cover;
        this.uuid = uuid;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getMonth_year() {
        return month_year;
    }

    public int getCover() {
        return cover;
    }

    public String getReal_cover() {
        return real_cover;
    }

    public String getUuid(){ return uuid; }

    public String getType(){ return type; }
}
