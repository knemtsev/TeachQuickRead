package com.nnsoft.teachquickread;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Nicholas Nemtsev on 13.02.2017.
 * knemtsev@gmail.com
 */

// DB record
public class CachedFile extends RealmObject {
    private String fileName;
    private int numberOfParagraphs; // количество параграфов
    private RealmList<Paragraph> parList=new RealmList<Paragraph>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getNumberOfParagraphs() {
        return numberOfParagraphs;
    }

    public void setNumberOfParagraphs(int numberOfParagraphs) {
        this.numberOfParagraphs = numberOfParagraphs;
    }

    public RealmList<Paragraph> getParList() {
        return parList;
    }
}
