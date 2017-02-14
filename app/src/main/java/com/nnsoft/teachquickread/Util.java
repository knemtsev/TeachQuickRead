package com.nnsoft.teachquickread;

import java.util.Random;
import java.util.zip.CRC32;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by knemt on 24.01.2017.
 */

public class Util {
    public static int CountWords(char[] text, int pos, int len)
    {
        return CountWords(new String(text,pos,len));
    }

    public static int CountWords(String line) {
        String[] words = line.split("[ ,.!?;\\-:\"\t\r\n]+");
        return words.length;
    }

    public static char[] GetRandomText(String[] pars, int words) {
        if (pars != null) {
            int[] lens = new int[pars.length];
            for (int i = 0; i < pars.length; i++) {
                lens[i] = CountWords(pars[i]);
            }

            Random rnd = new Random(System.currentTimeMillis());
            int startNumLine = rnd.nextInt(pars.length);
            int numLines = 0;
            int lenW = 0;
            for (int i = startNumLine; i < pars.length && lenW < words; i++) {
                lenW += lens[i];
                numLines++;
            }
            while (lenW < words && startNumLine > 0) {
                startNumLine--;
                numLines++;
                lenW += lens[startNumLine];
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numLines; i++) {
                sb.append(pars[startNumLine + i]);
                if (i != numLines - 1)
                    sb.append('\n');
            }

            return sb.toString().toCharArray();
        } else
            return "No data".toCharArray();
    }

    public static char[] getRandomText(int words)
    {
        StringBuilder sb=new StringBuilder();
        CachedFile cachedFile=Options.getCachedFile();
        Realm realm=Realm.getDefaultInstance();
        if(cachedFile!=null)
        {
            Random rnd = new Random(System.currentTimeMillis());
            int maxNumLines=cachedFile.getNumberOfParagraphs();
            int startNumLine = rnd.nextInt(maxNumLines);
            int numLines = 0;
            int lenW = 0;
            for (int i = startNumLine; i < maxNumLines && lenW < words; i++) {
                Paragraph p=cachedFile.getParList().get(i);
                lenW += p.getNumWords();
                sb.append(p.getText()+"\n");
                numLines++;
            }

            while (lenW < words && startNumLine > 0) {
                startNumLine--;
                Paragraph p=cachedFile.getParList().get(startNumLine);
                numLines++;
                lenW += p.getNumWords();
                sb.insert(0,p.getText()+"\n");
            }

        }
        return sb.toString().toCharArray();
    }

    public static int SCRC32(String s)
    {
        CRC32 crc = new CRC32();
        crc.update(s.getBytes());
        return (int)( crc.getValue() );
    }
}
