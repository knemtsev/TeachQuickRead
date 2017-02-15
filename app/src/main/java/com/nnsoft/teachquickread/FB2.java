package com.nnsoft.teachquickread;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Nicholas Nemtsev on 24.01.2017.
 * knemtsev@gmail.com
 */

public class FB2 {
    private static final String DEFAULT_ENCODING = "UTF-8";
    String filePath;
    String fileText = null;
    List<String> listParagraphs;
    String[] paragraphs = null;
    static String TAG = "FB2";
    private Cache cache;
    private int numParagraphs=0;

    public FB2(String filePath, Activity act, Cache cache) {

        this.cache=cache;
        this.filePath = filePath;
        try {
            if(filePath.startsWith("/")) {
                File f = new File(filePath);
//            Log.i("FB2",filePath);
                if (f.exists()) {
//                Log.i("F2","exist "+f.getName());
                    if (filePath.toLowerCase().endsWith(".zip")) {
                        ZipFile zf = new ZipFile(filePath);
                        ZipEntry ze = (ZipEntry) zf.entries().nextElement();
                        String enc = seeEncoding(zf, ze);
                        readTextXML(zf.getInputStream(ze), enc);
                        zf.close();
                    } else {
                        String enc = seeEncoding(filePath);
                        FileInputStream is=new FileInputStream(filePath);
                        readTextXML(is, enc);
                        is.close();
                    }
                }
            }
            else
            {
                AssetManager assetManager = act.getAssets();
                InputStream is=assetManager.open(filePath);
                readTextXML(is, DEFAULT_ENCODING);
                is.close();
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


    private String readText(InputStream is, String encoding) {
        String res = "";
        if(encoding==null)
            encoding="UTF-8";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(encoding)));
            String line;

            StringBuilder sb = new StringBuilder();

            StringBuilder paragraph= null;
            int numParagraph=0;
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

    private static String TAG_P="p";
    private static String TAG_SECTION="section";
    private static String TAG_TITLE="title";
    private static String TAG_STRONG="strong";

    private void readTextXML(InputStream is, String encoding) {
        numParagraphs=0;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(is, encoding);

            String tag="";
            Stack<String> tagStack=new Stack<String>();
            StringBuilder paragraph=null;
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    // начало документа
                    case XmlPullParser.START_DOCUMENT:
                        Log.d(TAG, "START_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG:
                        tag=xpp.getName().toLowerCase();
                        tagStack.push(tag);
                        Log.d(TAG, "START_TAG: name = " + tag);
                        if(tag.equals(TAG_P))
                            paragraph=new StringBuilder();
                        break;
                    // конец тэга
                    case XmlPullParser.END_TAG:
                        tag=tagStack.pop();
                        Log.d(TAG, "END_TAG: name = " + xpp.getName());
                        if(tag.equalsIgnoreCase(xpp.getName()))
                        {
                            if(tag.equals(TAG_P) && !tagStack.empty() && tagStack.peek().equals(TAG_SECTION) && paragraph!=null)
                            {
                                String parS=paragraph.toString();
                                cache.putParagraph(numParagraphs,parS);
                                Log.d(TAG, "END_TAG: p = [" + parS +"]");
                                numParagraphs++;
                                paragraph=null;
                            }
                        }
                        break;
                    // содержимое тэга
                    case XmlPullParser.TEXT:
                        String text=xpp.getText();
                        Log.d(TAG, "TEXT[" + tagStack.peek()+"]=<"+ text+">");
                        String lastTag=tagStack.peek();
                        if(lastTag.equals(TAG_P)) {
                            if(paragraph!=null)
                                paragraph.append(text);
                        }
                        else if(lastTag.equals(TAG_STRONG)) // или другие подобные теги
                        {
                            if(paragraph!=null)
                                paragraph.append(text);
                        }
                        break;

                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (Exception ex)
        {
            Log.e(TAG,ex.toString());
            ex.printStackTrace();
        }

    }


    public String[] GetParagraphsOld() {
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

    public String[] GetParagraphs() {
        if (paragraphs != null)
            return paragraphs;

        if (listParagraphs == null)
            return null;

        paragraphs=listParagraphs.toArray(new String[listParagraphs.size()]);

        return paragraphs;
    }

    public int getNumParagraphs() {
        return numParagraphs;
    }
}
