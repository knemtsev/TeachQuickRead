package com.nnsoft.teachquickread;

import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.Exchanger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by knemt on 24.01.2017.
 */

public class FB2 {
    private static final String DEFAULT_ENCODING = "UTF-8";
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
                    String enc=seeEncoding(zf,ze);
                    fileText = readText(zf.getInputStream(ze),enc);
                    zf.close();
                } else {
                    String enc=seeEncoding(filePath);
                    fileText = readText(new FileInputStream(filePath),enc);
                }
            }
        } catch (Exception ex) {
            Log.d(TAG,ex.toString());
        }
    }

    public String seeEncoding(InputStream is)
    {
        String res=DEFAULT_ENCODING;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(DEFAULT_ENCODING)));
            String line = reader.readLine();
            if(line!=null)
            {
                Pattern pEnc=Pattern.compile("encoding=\"(.*)\"");
                Matcher m = pEnc.matcher(line);
                if(m.find())
                {
                    res=m.group(1);
                    Log.d(TAG,"encoding="+res);
                }
                is.close();
            }
        } catch (Exception ex)
        {
            Log.e(TAG+":seeEncoding", ex.toString());
        }

        return res;
    }

    public String seeEncoding(ZipFile zf, ZipEntry ze)
    {
        String res=DEFAULT_ENCODING;
        try {
            res= seeEncoding( zf.getInputStream(ze) );
        }
        catch (Exception ex)
        {
            Log.e(TAG+":seeEncoding2", ex.toString());
        }
        return res;
    }

    public String seeEncoding(String fileName)
    {
        String res=DEFAULT_ENCODING;
        try {
            FileInputStream fis=new FileInputStream(fileName);
            res= seeEncoding(fis);
            fis.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG+":seeEncoding3", ex.toString());
        }
        return res;
    }


    public FB2(InputStream is)
    {
        try {
            fileText = readText(is,DEFAULT_ENCODING);
        }catch (Exception ex)
        {
//            ex.printStackTrace();
            Log.d(TAG,ex.toString());
        }
    }

    private String readText(InputStream is, String encoding) {
        String res = "";
        if(encoding==null)
            encoding="UTF-8";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(encoding)));
            String line;

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
