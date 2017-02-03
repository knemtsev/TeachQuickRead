package com.nnsoft.teachquickread;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by knemt on 24.01.2017.
 */

public class Options {
    private static final String TAG = "Options";
    private static final String assetFB2File="vig.fb2";
    private static Options ourInstance = new Options();

    // properties
    private static int mode; // режим работы 1-3
    private static String fileNameToRead;
    private static int readSpeed; // фиксированная скорость чтения для 2-го режима
    private static String[] paragraphs = null;
    private static int fontSize;
    private static int wordsNum; // длина текста в словах (не менее)
    private static char[] textToRead;
    private static String dateFormat;
    private static boolean skipVowels;
    private static Date lastDateSpeedMode;
    private static int maxSpeed;
    private static boolean useHyphenation;
    private static boolean fileLoaded;
    private static boolean randomText;  // случайный фрагмент
    private static int endOfLastText;

    private static FB2 fb2;

    public static Options getInstance() {

        return ourInstance;
    }

    public static void save(Activity act) {
        try {
            SharedPreferences pref = act.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor p = pref.edit();
            p.putString("fileNameToRead", fileNameToRead);
            p.putInt("readSpeed", readSpeed);
            p.putInt("fontSize", fontSize);
            p.putInt("mode", mode);
            p.putInt("wordsNum", wordsNum);
            p.putString("dateFormat", dateFormat);
            p.putBoolean("skipVowels", skipVowels);
            p.putString("lastDateSpeedMode", formatDate(lastDateSpeedMode));
            p.putInt("maxSpeed", maxSpeed);
            p.putBoolean("useHyphenation", useHyphenation);
            p.commit();
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    public static void rest(Activity act) {
        try {
            SharedPreferences pref = act.getPreferences(Context.MODE_PRIVATE);
            setFileNameToRead(pref.getString("fileNameToRead", ""));
            readSpeed = pref.getInt("readSpeed", 75);
            fontSize = pref.getInt("fontSize", 30);
            mode = pref.getInt("mode", 1);
            wordsNum = pref.getInt("wordsNum", 60);
            dateFormat = pref.getString("dateFormat", "yyyy-MM-dd HH:mm");
            skipVowels = pref.getBoolean("skipVowels", false);
            SimpleDateFormat parser = new SimpleDateFormat(dateFormat);
            lastDateSpeedMode = parser.parse(pref.getString("lastDateSpeedMode", formatDate(new Date())));
            maxSpeed = pref.getInt("maxSpeed", 300);
            useHyphenation=pref.getBoolean("useHyphenation",true);
            fileLoaded=false;
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    public static String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat(getDateFormat());
        return df.format(date);
    }

    public static void setFileNameToRead(String _fileNameToRead) {
//        if (_fileNameToRead.length() > 0 && (paragraphs == null || !_fileNameToRead.equalsIgnoreCase(fileNameToRead))) {
//            fb2 = new FB2(_fileNameToRead);
//            paragraphs = fb2.GetParagraphs();
//        }
        fileNameToRead = _fileNameToRead;
    }

    public static String getFileNameToRead() {
        return fileNameToRead;
    }


    public static int getReadSpeed() {
        return readSpeed;
    }

    public static void setReadSpeed(int readSpeed) {
        Options.readSpeed = readSpeed;
    }

    public static void asyncSetParagraphs(Activity act)
    {
        paragraphs=null;
        if(fileNameToRead.length()>0) {
            try {
                fb2 = new FB2(fileNameToRead);
                paragraphs = fb2.GetParagraphs();
            } catch (Exception ex) {
                Log.d(TAG,ex.toString());
            }
        }
        if(paragraphs==null)
        {
            try {
                AssetManager assetManager = act.getAssets();
                fb2=new FB2(assetManager.open(assetFB2File));
                paragraphs = fb2.GetParagraphs();
            } catch (Exception ex) {
                Log.d(TAG,ex.toString());
            }
        }
        setFileLoaded(paragraphs!=null);
    }

    public static String[] getParagraphs() {

        return paragraphs;
    }

    public static void setParagraphs(String[] paragraphs) {
        Options.paragraphs = paragraphs;
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static void setFontSize(int fontSize) {
        Options.fontSize = fontSize;
    }

    public static int getMode() {
        return mode;
    }

    public static void setMode(int mode) {
        Options.mode = mode;
    }

    public static int getWordsNum() {
        return wordsNum;
    }

    public static void setWordsNum(int wordsNum) {
        Options.wordsNum = wordsNum;
    }

    public static char[] getTextToRead() {
        return textToRead;
    }

    public static void setTextToRead(char[] textToRead) {
        Options.textToRead = textToRead;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static void setDateFormat(String dateFormat) {
        Options.dateFormat = dateFormat;
    }

    public static boolean isSkipVowels() {
        return skipVowels;
    }

    public static void setSkipVowels(boolean skipVowels) {
        Options.skipVowels = skipVowels;
    }

    public static Date getLastDateSpeedMode() {
        return lastDateSpeedMode;
    }

    public static void setLastDateSpeedMode(Date lastDateSpeedMode) {
        Options.lastDateSpeedMode = lastDateSpeedMode;
    }

    public static int getMaxSpeed() {
        return maxSpeed;
    }

    public static void setMaxSpeed(int maxSpeed) {
        Options.maxSpeed = maxSpeed;
    }

    public static boolean isUseHyphenation() {
        return useHyphenation;
    }

    public static void setUseHyphenation(boolean useHyphenation) {
        Options.useHyphenation = useHyphenation;
    }

    public static boolean isFileLoaded() {
        return fileLoaded;
    }

    public static void setFileLoaded(boolean fileLoaded) {
        Options.fileLoaded = fileLoaded;
    }
}
