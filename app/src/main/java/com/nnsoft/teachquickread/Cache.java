package com.nnsoft.teachquickread;

import io.realm.Realm;

/**
 * Created by Nicholas Nemtsev on 13.02.2017.
 * knemtsev@gmail.com
 */

public class Cache {
    private Realm realm;
    public Cache()
    {
        realm=Realm.getDefaultInstance();
    }

    public CachedFile getFile(String fileName)
    {
        CachedFile cachedFile=realm.where(CachedFile.class).equalTo("fileName", fileName).findFirst();
        return cachedFile;
    }

    public void storeFB2File()
    {

    }

    public void storeParagraph(Paragraph par)
    {

    }

    public String getParagraph(CachedFile f, int n)
    {

    }
}
