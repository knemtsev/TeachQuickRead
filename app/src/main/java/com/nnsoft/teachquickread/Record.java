package com.nnsoft.teachquickread;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by knemt on 27.01.2017.
 */

public class Record extends RealmObject {
    private Date date;
    private int lenTextInWords;
    private int timeInSecs;
    private int speed;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getLenTextInWords() {
        return lenTextInWords;
    }

    public void setLenTextInWords(int lenTextInWords) {
        this.lenTextInWords = lenTextInWords;
    }

    public int getTimeInSecs() {
        return timeInSecs;
    }

    public void setTimeInSecs(int timeInSecs) {
        this.timeInSecs = timeInSecs;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
