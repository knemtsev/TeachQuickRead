package com.nnsoft.teachquickread;

import android.app.Activity;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Nicholas Nemtsev on 13.02.2017.
 * knemtsev@gmail.com
 */

public class Cache {
    Activity mainActivity;
    private CachedFile cachedFile;
    Realm realm;

    public Cache(Activity _mainActivity)
    {
        mainActivity=_mainActivity;
        realm=Realm.getDefaultInstance();
    }

    public CachedFile getFile(String fileName)
    {
        cachedFile=realm.where(CachedFile.class).equalTo("fileName", fileName).findFirst();
        if(cachedFile==null){
            try {
                realm.beginTransaction();
                cachedFile = realm.createObject(CachedFile.class);
                cachedFile.setFileName(fileName);
                FB2 fb2 = new FB2(fileName, mainActivity, this);
                //cachedFile=new CachedFile();
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
        p.setId(num);
        p.setText(text);
        p.setNumWords(Util.countWords(text));
        cachedFile.getParList().add(p);
    }

    public void clear()
    {
        // clear files
        Realm realm;
        realm=Realm.getDefaultInstance();
        final RealmResults<CachedFile> resF = realm.where(CachedFile.class).findAll();
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
