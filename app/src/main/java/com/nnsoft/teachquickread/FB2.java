package com.nnsoft.teachquickread;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by knemt on 24.01.2017.
 */

public class FB2 {
    String filePath;
    String fileText = null;
    String[] paragraphs = null;
    static String TAG = "FB2";

    public FB2(String _filePath) {

        filePath = _filePath;
        try {

            File f = new File(filePath);
//            Log.i("FB2",filePath);
            if (f.exists()) {
//                Log.i("F2","exist "+f.getName());
                if (filePath.toLowerCase().endsWith(".zip")) {
                    ZipFile zf = new ZipFile(filePath);
                    ZipEntry ze = (ZipEntry) zf.entries().nextElement();
                    fileText = readText(zf.getInputStream(ze));
                } else {
                    fileText = readText(new FileInputStream(filePath));
                }
            }
        } catch (Exception ex) {

        }

    }

    private String readText(InputStream is) {
        String res = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n\r");
            }
            reader.close();
            res = sb.toString();
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return res;
    }

    public String[] GetParagraphs() {
        if (paragraphs != null)
            return paragraphs;

        if (fileText == null)
            return null;

        //Regex re0=new Regex(@"<body>(.*)</body>",RegexOptions.Compiled | RegexOptions.IgnoreCase);
        //Match match0 = Regex.Match(fileText, @".*<body>(.*)</body>",RegexOptions.IgnoreCase);
        int skip = fileText.indexOf("<body>");
        Pattern p = Pattern.compile("<p>(.*)</p>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(fileText.substring(skip));

        int pars = 0;
        while (m.find()) {
            pars++;
        }
        paragraphs = new String[pars];
        m.reset();
        int i = 0;
        while (m.find()) {
//            Log.i(TAG,">>>"+m.group(1)+"<<<");
            paragraphs[i] = m.group(1);
            i++;
        }
        return paragraphs;
    }

}
