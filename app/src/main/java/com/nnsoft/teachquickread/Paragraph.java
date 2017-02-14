package com.nnsoft.teachquickread;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nicholas on 07.02.2017.
 */

// DB record
public class Paragraph extends RealmObject {
    private long id; // номер параграфа
    private int numWords; // количество слов в параграфе
    private String text; // строка

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumWords() {
        return numWords;
    }

    public void setNumWords(int numWords) {
        this.numWords = numWords;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
