package com.nnsoft.teachquickread;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Nicholas on 07.02.2017.
 */

public class Paragraph extends RealmObject {
    private int id; // primary key и он же номер параграфа
    private int numWords; // количество слов в параграфе
    private String paragraph; // строка


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
