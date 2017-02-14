package com.nnsoft.teachquickread;

import android.app.Activity;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Nicholas Nemtsev on 13.02.2017.
 * knemtsev@gmail.com
 */

public class Cache {
    private Realm realm;
    Activity mainActivity;
    private int curFileCRC32;
    public Cache(Activity _mainActivity)
    {
        mainActivity=_mainActivity;
        realm=Realm.getDefaultInstance();
    }

    public CachedFile getFile(String fileName)
    {
        CachedFile cachedFile=realm.where(CachedFile.class).equalTo("fileName", fileName).findFirst();
        if(cachedFile==null){
            try {
                realm.beginTransaction();
                curFileCRC32=Util.SCRC32(fileName);
                FB2 fb2 = new FB2(fileName, mainActivity, this);
                cachedFile = realm.createObject(CachedFile.class);
                //cachedFile=new CachedFile();
                cachedFile.setFileName(fileName);
                cachedFile.setFileNameCRC32();
                cachedFile.setNumberOfParagraphs(fb2.getNumParagraphs());
                realm.commitTransaction();
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return cachedFile;
    }

    public void putParagraph(int num, String text)
    {
        Paragraph p=realm.createObject(Paragraph.class);
        p.setFileNameCRC32(curFileCRC32);
        p.setId(num);
        p.setParagraph(text);
        p.setNumWords(Util.CountWords(text));
    }

    public void clear()
    {
        // clear paragraphs
        final RealmResults<Paragraph> resP = realm.where(Paragraph.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Delete all matches
                resP.deleteAllFromRealm();
            }
        });
        // clear files
        final RealmResults<Paragraph> resF = realm.where(Paragraph.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Delete all matches
                resF.deleteAllFromRealm();
            }
        });

        Options.setCachedFile(null);
    }
}
