package com.nnsoft.teachquickread;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by knemt on 27.01.2017.
 */

// DB record
public class Record extends RealmObject {
    private Date date;
    private int lenTextInWords;
    private int timeInSecs;
    private int speed; // слов/минута
    private int mode; // режим: 1 - простой, 2 - с заданной скоростью, 3 - вслух
    private int errors; // количество ошибок
    private String fileName;
    private int parBegin; // номер первого параграфа
    private int parEnd; // номер последнего параграфа

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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getParBegin() {
        return parBegin;
    }

    public void setParBegin(int parBegin) {
        this.parBegin = parBegin;
    }

    public int getParEnd() {
        return parEnd;
    }

    public void setParEnd(int parEnd) {
        this.parEnd = parEnd;
    }
}
