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
                    zf.close();
                } else {
                    fileText = readText(new FileInputStream(filePath));
                }
            }
        } catch (Exception ex) {
            Log.d(TAG,ex.toString());
        }
    }

    public FB2(InputStream is)
    {
        try {
            fileText = readText(is);
        }catch (Exception ex)
        {
//            ex.printStackTrace();
            Log.d(TAG,ex.toString());
        }
    }

    private String readText(InputStream is) {
        String res = "";
        try {
            String utf8="UTF-8";
            is.mark(128);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(utf8)));
            String line;
            line = reader.readLine();
            if(line!=null)
            {
                Log.d(TAG,line);
                // определяем кодировку
                Pattern pEnc=Pattern.compile("encoding=\"(.*)\"");
                Matcher m = pEnc.matcher(line);
                if(m.find())
                {
                    String encoding=m.group(1);
                    Log.d(TAG,"encoding="+encoding);
                    if(!encoding.equalsIgnoreCase(utf8))
                    {
                        reader.close();
                        Log.d(TAG,"Charset.forName("+encoding+")="+Charset.forName(encoding));
                        is.reset();
                        reader = new BufferedReader(new InputStreamReader(is, Charset.forName(encoding)));
                    }
                }

            }

            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.d(TAG,line);
                sb.append("\n\r");
            }
            reader.close();
            res = sb.toString();
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            ex.printStackTrace();
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
        Pattern pRem= Pattern.compile("(<a.*</a>)|(<strong>)|(</strong>)|(<emphasys>)||(</emphasys>)", Pattern.CASE_INSENSITIVE);
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
            String s=m.group(1);
            Matcher m2 = pRem.matcher(s);
            paragraphs[i] = m2.replaceAll("");
//            Log.d(TAG+1,">"+s+"<");
//            Log.d(TAG+2,"<"+paragraphs[i]+">");
            i++;
        }
        return paragraphs;
    }

}
