package com.uwika.traveljournal;

public class LastJournalModel {
    String title, date, month_year;
    int cover;

    public LastJournalModel(String title, String date, String month_year, int cover){
        this.title = title;
        this.date = date;
        this.month_year = month_year;
        this.cover = cover;
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
}
