package com.nnsoft.teachquickread;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import io.realm.Realm;

public class ReadActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout llMain;

    // TODO: сделать задержку после клика по тексту для предотвращения случайного пролистывания
    // DEBUG
    TextView tvFontSize;
    //    Button btnAdd,btnSub;
    int fontSize;
    long startTime, stopTime, startTimeOfPage;
    Updater u;
    int shadowLine;
    int maxNumLines;// сколько всего строк помещается на экран
    int numLines; // сколько реально выведено
    int[] numWords; // количество строк по строкам
    int wordsOnPage; // количество строк на текущем экране
    int fixedSpeedOfReading; // заданная скорость чтения слов/минуту
    int durationForWordMS;  // время на одно слово в мс
    Map<Integer, Integer> linesNumByFont = new HashMap<Integer, Integer>() {{
        put(12, 34);
        put(13, 31);
        put(14, 29);
        put(15, 27);
        put(16, 25);
        put(17, 24);
        put(18, 23);
        put(19, 21);
        put(20, 20);
        put(21, 19);
        put(22, 18);
        put(23, 18);
        put(24, 17);
        put(25, 16);
        put(26, 16);
        put(27, 15);
        put(28, 14);
        put(29, 14);
        put(30, 14);
        put(31, 13);
        put(32, 13);
        put(33, 12);
        put(34, 12);
        put(35, 12);
        put(36, 11);
        put(37, 11);
        put(38, 11);
        put(39, 10);
        put(40, 10);
        put(41, 10);
    }};
    private String TAG = "ReadActivity";
    private int activity_horizontal_margin;
    private int widthPx, heightPx;
    private int maxLenLine = 100;
    private char[] textToRead;
    private int curPos;
    private Record rec;

    public static int getBackgroundColor(View view) {
        Drawable drawable = view.getBackground();
        if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            if (Build.VERSION.SDK_INT >= 11) {
                return colorDrawable.getColor();
            }
            try {
                Field field = colorDrawable.getClass().getDeclaredField("mState");
                field.setAccessible(true);
                Object object = field.get(colorDrawable);
                field = object.getClass().getDeclaredField("mUseColor");
                field.setAccessible(true);
                return field.getInt(object);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_read);

        //llMain = (LinearLayout) findViewById(R.id.activity_read);
        llMain = (LinearLayout) findViewById(R.id.activity_read);
        // DEBUG
        fontSize = Options.getFontSize();
        widthPx = getResources().getDisplayMetrics().widthPixels - (int) getResources().getDimension(R.dimen.activity_horizontal_margin) * 2;
        heightPx = getResources().getDisplayMetrics().heightPixels - (int) getResources().getDimension(R.dimen.activity_vertical_margin) * 2;

        textToRead = Options.getTextToRead();

        llMain.setOnClickListener(this);

        maxNumLines = linesNumByFont.get(Integer.valueOf(fontSize));
        Log.d(TAG, "fontSize=" + fontSize + " maxNumLines=" + maxNumLines + " widthPx=" + widthPx + " heightPx=" + heightPx);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Log.d(TAG, "" + dm.density + " " + dm.densityDpi + " " + dm.widthPixels + " " + dm.heightPixels + " " + dm.xdpi + " " + dm.ydpi + " " + dm.scaledDensity);

        fixedSpeedOfReading = Options.getReadSpeed();
        durationForWordMS = (int) (60 * 1000 / fixedSpeedOfReading);
        Log.d(TAG, "fixedSpeedOfReading=" + fixedSpeedOfReading + "w/m durationForWordMS=" + durationForWordMS + "ms");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (u != null) u.stopped = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (u != null) u.stopped = true;
    }

    void startReading() {
        startTime = System.currentTimeMillis();
    }

    void stopReading() {
        stopTime = System.currentTimeMillis();
        if (Options.getMode() == 2) u.stopped = true;

        // вычисляем и записываем в таблицу рекордов
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                rec = realm.createObject(Record.class);
                rec.setDate(new Date());
                int numW = Util.CountWords(new String(textToRead));
                rec.setLenTextInWords(numW);
                int timeInSec = (int) (stopTime - startTime) / 1000;
                rec.setTimeInSecs(timeInSec);
                rec.setSpeed(numW * 60 / timeInSec);
            }
        });
    }

    private void showRecord(Context context) {
        Intent intent = new Intent(context, RecordActivity.class);

        DateFormat df = new SimpleDateFormat(Options.getDateFormat());

        intent.putExtra("date", df.format(rec.getDate()));
        intent.putExtra("words", rec.getLenTextInWords());
        intent.putExtra("time", rec.getTimeInSecs());
        intent.putExtra("speed", rec.getSpeed());
        startActivity(intent);
    }

    private void nextPage(View v) {

        if (!isEot(textToRead, curPos))
            curPos = showText(textToRead, curPos);
        else {
            stopReading();
            showRecord(v.getContext());
            super.finish();
        }
    }

    @Override
    public void onClick(View v) {
        // предотвращение слишком быстрого нажатия
        int timeInSec = (int) (System.currentTimeMillis() - startTimeOfPage) / 1000;
        if (timeInSec > 0) {
            int speedOfPage = wordsOnPage * 60 / timeInSec;
            if (speedOfPage <= Options.getMaxSpeed())
                nextPage(v);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        curPos = showText(textToRead, 0);
        startReading();
    }

    private boolean isBlank(char c) {
        return (c == ' ') || (c == '\r') || (c == '\n') || (c == '\t') || (c == ' ');
    }

    private boolean isPunct(char c) {
        return (c == ',') || (c == '.') || (c == ':') || (c == ';') || (c == '-') || (c == '?') || (c == '!') || (c == '"');
    }

    private boolean isEol(char[] text, int pos) {
        return ((text.length - pos) > 0) && ((text[pos] == '\r') || (text[pos] == '\n'));
    }

    private boolean isEot(char[] text, int pos) {
        return pos >= text.length;
    }

    private int skipBlank(char[] text, int pos) {
        while (pos < text.length && isBlank(text[pos])) pos++;
        return pos;
    }

    private int getNextWord(char[] text, int pos) {
        // возвращает позицию за словом
        pos = skipBlank(text, pos);
        while (pos < text.length && !isBlank(text[pos])) pos++;
        return pos;
    }

    private boolean isVowel(char c) {
        return "уеыаоэяиюУЕЫАОЭЯИЮ".indexOf(c) >= 0;
    }

    String skipVowels(char[] text, int pos, int lenLine) {
        String res = "";
        if (Options.isSkipVowels()) {
            StringBuilder sb = new StringBuilder();
            int endPos = pos + lenLine;
            Random rnd = new Random(System.currentTimeMillis());
            while (pos < endPos) {
                int begW = pos;
                int endW = pos;
                int numVowels = 0;
                while (pos < endPos) {
                    endW = pos;
                    char c = text[pos];
                    pos++;
                    if (isBlank(c)) {
                        pos = skipBlank(text, pos);
                        break;
                    } else {
                        if (isVowel(c)) numVowels++;
                    }
                }
                int lenW = endW - begW + 1;
                if (numVowels > 1) {
                    // делаем 1 пропуск в случайной позиции, кроме первой
                    int nV = 0;
                    for (int i = begW; i <= endW; i++) {
                        char c = text[i];
                        if (isVowel(c) && i>begW) {
                            if ((nV%2)==0) {
                                //Log.d(TAG,""+new String(text,begW,lenW)+" c="+c+" posW="+(i-begW));
                                sb.append('.');
                            }
                            else
                                sb.append(c);
                            nV++;
                        } else
                            sb.append(c);
                    }

                } else {
                    // копируем как есть
                    sb.append(text, begW, lenW);
                }
                if (pos < endPos)
                    sb.append(' ');
            }
            res = sb.toString();
        } else
            res = new String(text, pos, lenLine);
        return res;
    }

    private int showText(char[] text, int pos) {
        // вывести на экран по строке текст с указанной позиции сколько влезет
        // возвращает позицию

        llMain.removeAllViews();

        pos = skipBlank(text, pos);
        int numLine = 0;
        numWords = new int[maxNumLines];
        wordsOnPage = 0;
        while (!isEot(text, pos)) {
            TextView vt = new TextView(this);
            vt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            vt.setTextSize(fontSize);
            vt.setClickable(true);
            vt.setOnClickListener(this);
            vt.setMaxLines(1);

            Paint p = vt.getPaint();
            int lenLine = p.breakText(text, pos, Math.min(text.length - pos, maxLenLine), widthPx, null);
            boolean isNewLine = false;
            // поиск перевода на новую строку
            for (int i = 0; i < lenLine; i++)
                if (isEol(text, pos + i)) {
                    lenLine = i;
                    isNewLine = true;
                    break;
                }

            boolean addHyphen=false;
            if(!isNewLine) {
                if (lenLine > 0 && isBlank(text[pos + lenLine - 1])) {
                    // последним символом выводимой строки оказался пробел
                    lenLine--;
                    // и больше ничего не делаем, строка полностью вошла
                } else if (lenLine > 0 && isBlank(text[pos + lenLine])) {
                    // за последним символом строки оказался пробел
                    ;
                    // тоже ничего не делаем
                } else {
                    // конец выводимой строки рвёт слово
                    if (Options.isUseHyphenation()) {
                        // пытаемся разбить слово по слогам и вывести с переносом
                        // находим начало слова и конец слова
                        int begW=pos+lenLine;
                        int endW=pos+lenLine;
                        while(begW>pos && !isBlank(text[begW-1]) && !isPunct(text[begW-1])) begW--;
                        while(!isEot(text, endW) && !isBlank(text[begW+1]) && !isPunct(text[begW+1])) endW++;
                        Vector<String> syllables=new Hyphenator().hyphenateWord(new String(text,begW,endW-begW+1));
                        int lenLineWW=begW-pos;
                        int lenFirstHalf=0;
                        for(String s : syllables)
                        {
                            if(lenLineWW+lenFirstHalf+s.length()+1<lenLine)
                                lenFirstHalf+=s.length();
                            else
                                break;
                        }
                        lenLine=lenLineWW+lenFirstHalf;
                        addHyphen=lenFirstHalf>0;
                    } else {
                        // рвём по пробелу или, если слово слишком длинное и занимает всю строку, рвём слово
                        // поиск пробела от конца строки
                        for (int i = lenLine - 1; i > 0; i--) {
                            if (isBlank(text[pos + i])) {
                                lenLine = i;
                                break;
                            }
                        }
                    }
                }
            }


            String outText=skipVowels(text, pos, lenLine);

            numWords[numLine] = Util.CountWords(text, pos, lenLine);

            vt.setText(""+outText+(addHyphen?"-":""));
            llMain.addView(vt);

            wordsOnPage += numWords[numLine];

            pos = skipBlank(text, pos + lenLine);
            numLine++;
            if (numLine >= maxNumLines)
                break;
        }

        numLines = numLine;
        if (isEot(text, pos) && numLine != maxNumLines) {
            Button btn = new Button(this);
            btn.setText(getResources().getText(R.string.it_s_ready));
            btn.setOnClickListener(this);
            llMain.addView(btn);
        }

        if (Options.getMode() == 2) {
            shadowLine = 0;
            if (u != null) u.stopped = true;
            u = new Updater();
            u.start();
            Log.i("Read" + u.getId(), "*********** Start *************");
        }

        startTimeOfPage = System.currentTimeMillis();

        return pos;
    }

    private void crossfade(final View mLoadingView) {
        int duration = numWords[shadowLine] * durationForWordMS;
        Log.d(TAG, "Before fade " + duration + " ms, words[" + shadowLine + "]=" + numWords[shadowLine]);
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setAlpha(0f);
                    }
                });
        Log.d(TAG, "After fade " + duration + " ms, words[" + shadowLine + "]=" + numWords[shadowLine]);
    }

    // DEBUG
    private void setFontSize(int size) {
        tvFontSize.setText(Integer.toString(size));
    }

    private void showTest() {
        llMain.removeAllViews();
//        Rect rect=new Rect();
//        llMain.getDrawingRect(rect);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float dens = dm.density;
        try {
            int num_lines = 5;
            String[] pars = Options.getParagraphs();
            TextView tv[] = new TextView[num_lines];
            LayoutParams tvLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            TextView tv2 = new TextView(this);
            tv2.setText("" + dm.density + " " + dm.densityDpi + " " + dm.widthPixels + " " + dm.heightPixels + " " + dm.xdpi + " " + dm.ydpi + " " + dm.scaledDensity +
                    " " + activity_horizontal_margin);
            llMain.addView(tv2);
            for (int i = 0; i < num_lines; i++) {
                TextView tvi = new TextView(this);
                tv[i] = new TextView(this);
                tv[i].setTextSize(fontSize);
                int w = llMain.getWidth();
                tv[i].setLayoutParams(tvLayoutParam);
                tvi.setLayoutParams(tvLayoutParam);
                float len = tv[i].getPaint().measureText(pars[i]);
                float[] mesW = new float[1];
                int chars = tv[i].getPaint().breakText(pars[i], true, w, mesW);
                tvi.setText("" + len + " " + w + " " + w * dens + " " + chars + " " + mesW[0]);
                tv[i].setText(pars[i]);
                llMain.addView(tvi);
                llMain.addView(tv[i]);
            }
        } catch (Exception ex) {
            Log.e("", ex.toString());
        }
    }

    private class Updater extends Thread {
        public boolean stopped = false;
        private boolean isShadowed = false;
        private long id = this.getId();

        public void run() {

            try {
                while (!stopped) {
                    int duration = numWords[shadowLine] * durationForWordMS;
                    try {
                        Log.d(TAG, "Sleep Before [" + id + "]=" + duration + " ms, words[" + shadowLine + "]=" + numWords[shadowLine]);
                        Thread.sleep(duration);
                        Log.d(TAG, "Sleep After [" + id + "]=" + duration + " ms, words[" + shadowLine + "]=" + numWords[shadowLine]);
                    } catch (Exception e) {
                        Log.i("Read" + id, e.toString());
                    }
                    if (stopped)
                        break;
                    // Активность списка
                    Log.d("Read" + id, "Shadow1");
                    isShadowed = false;
                    ReadActivity.this.runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (!stopped) {
                                        Log.d("Read" + id, "Shadow2 [" + shadowLine + "]");
                                        try {
                                            TextView v = (TextView) ((ViewGroup) ReadActivity.this.llMain).getChildAt(shadowLine);
                                            //v.setTextColor(ReadActivity.getBackgroundColor(v));
                                            isShadowed = true;
                                            if (shadowLine < numLines - 1) {
                                                crossfade(v);
                                                shadowLine++;
                                            } else {
                                                stopped = true;
                                                nextPage(v);
                                            }
                                        } catch (Exception e) {
                                            Log.e("Read" + id, e.toString());
                                        }
                                    }
                                }
                            }
                    );
                }
            } catch (Exception e) {
            }
        }
    }

}

