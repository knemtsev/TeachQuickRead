package com.nnsoft.teachquickread;

import io.realm.RealmObject;

/**
 * Created by Nicholas Nemtsev on 13.02.2017.
 * knemtsev@gmail.com
 */

// DB record
public class CachedFile extends RealmObject {
    private String fileName;
    private int fileNameCRC32;
    private int numberOfParagraphs; // количество параграфов

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileNameCRC32() {
        return fileNameCRC32;
    }

    public void setFileNameCRC32(int fileNameCRC32) {
        this.fileNameCRC32 = fileNameCRC32;
    }

    public int getNumberOfParagraphs() {
        return numberOfParagraphs;
    }

    public void setNumberOfParagraphs(int numberOfParagraphs) {
        this.numberOfParagraphs = numberOfParagraphs;
    }
}
