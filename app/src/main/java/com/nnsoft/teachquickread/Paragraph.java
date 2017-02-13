package com.nnsoft.teachquickread;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Nicholas on 07.02.2017.
 */

// DB record
public class Paragraph extends RealmObject {
    private int id; // primary key и он же номер параграфа
    private int fileNameCRC32;
    private int numWords; // количество слов в параграфе
    private String paragraph; // строка


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileNameCRC32() {
        return fileNameCRC32;
    }

    public void setFileNameCRC32(int fileNameCRC32) {
        this.fileNameCRC32 = fileNameCRC32;
    }

    public int getNumWords() {
        return numWords;
    }

    public void setNumWords(int numWords) {
        this.numWords = numWords;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public Paragraph getByPrimaryKey(Realm realm, int id) {
        return realm.where(getClass()).equalTo("id", id).findFirst();
    }
}
