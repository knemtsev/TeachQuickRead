package com.nnsoft.teachquickread;

import android.util.Log;

import java.util.Random;
import java.util.zip.CRC32;

import io.realm.Realm;

/**
 * Created by knemt on 24.01.2017.
 */

public class Util {
    private static final String TAG="Util";

    public static int countWords(char[] text, int pos, int len)
    {
        return countWords(new String(text,pos,len));
    }

    public static int countWords(String line) {
        String[] words = line.split("[ ,.!?;\\-:\"\t\r\n]+");
        return words.length;
    }

    public static char[] getRandomText(String[] pars, int words) {
        if (pars != null) {
            int[] lens = new int[pars.length];
            for (int i = 0; i < pars.length; i++) {
                lens[i] = countWords(pars[i]);
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

            if(lenW>(words*1.25))
            {
                Log.d(TAG,"lenW="+lenW+" words="+words);
                // пытаемся укоротить
                String[] ss=sb.toString().split("(?<=[.!?])\\s");
                if(ss.length>1)
                {
                    int lenW2=0;
                    StringBuilder sb2=new StringBuilder();
                    for(int i=0; i<ss.length; i++)
                    {
                        sb2.append(ss[i]+" ");
                        lenW2+=countWords(ss[i]);
                        if(lenW2>words)
                            break;
                    }
                    sb=sb2;
                    Log.d(TAG,"lenW2="+lenW2);
                }
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
