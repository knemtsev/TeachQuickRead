package com.nnsoft.teachquickread;

import java.util.Random;

/**
 * Created by knemt on 24.01.2017.
 */

public class Util {
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

}
