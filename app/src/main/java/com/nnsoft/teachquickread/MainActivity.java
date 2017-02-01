package com.nnsoft.teachquickread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.travijuu.numberpicker.library.NumberPicker;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private final static boolean isPro = false;

    private LinearLayout llFixedSpeed;
    private RadioGroup rgMode;
    private RadioButton rbSimpleMode, rbSpeedMode, rbVoiceMode;
    private Button btnSettings, btnStart;
    private NumberPicker npFixedSpeed;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    private Button btnRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Realm.init(this);
        Options.rest(this);

        btnSettings = (Button) findViewById(R.id.btnOptions);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        llFixedSpeed = (LinearLayout) findViewById(R.id.linLayFixedSpeed);
        rbSimpleMode = (RadioButton) findViewById(R.id.radioButton1);
        rbSpeedMode = (RadioButton) findViewById(R.id.radioButton2);
        rbVoiceMode = (RadioButton) findViewById(R.id.radioButton3);
        rgMode = (RadioGroup) findViewById(R.id.rgMode);


        llFixedSpeed.setVisibility(View.GONE);

        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                              @Override
                                              public void onCheckedChanged(RadioGroup rg, int checkedId) {
                                                  llFixedSpeed.setVisibility(checkedId == R.id.radioButton2 ? View.VISIBLE : View.GONE);
                                              }
                                          }
        );

        npFixedSpeed = (NumberPicker) findViewById(R.id.npFixedSpeed);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setEnabled(Options.getFileNameToRead().length() > 0);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                btnStart.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
//                btnStart.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
//                try { sleep(500); } catch (Exception ex) {  }
//                btnStart.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
//                btnStart.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
//                try { sleep(500); } catch (Exception ex) {  }

                Intent intent = new Intent(v.getContext(), ReadActivity.class);
                Options.setTextToRead(Util.GetRandomText(Options.getParagraphs(), Options.getWordsNum()));
                startActivity(intent);

            }
        });

        btnRecords = (Button) findViewById(R.id.btnRecords);
        btnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecordsActivity.class);
                startActivity(intent);
            }
        });

        if (!isPro) {
            rbVoiceMode.setEnabled(false);
            rbVoiceMode.setText(getText(R.string.voice_mode) + " (Pro)");
        }

        restState();
    }


    private void saveState() {
        int mode = 1;
        switch (rgMode.getCheckedRadioButtonId()) {
            case R.id.radioButton1:
                mode = 1;
                break;
            case R.id.radioButton2:
                mode = 2;
                break;
            case R.id.radioButton3:
                mode = 3;
                break;
        }
        Options.setMode(mode);
        Options.setReadSpeed(npFixedSpeed.getValue());
    }

    private void restState() {
        switch (Options.getMode()) {
            case 1:
                rbSimpleMode.setChecked(true);
                break;
            case 2:
                rbSpeedMode.setChecked(true);
                break;
            case 3:
                rbVoiceMode.setChecked(true);
                break;
        }
        npFixedSpeed.setValue(Options.getReadSpeed());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveState();
        Options.save(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
        Options.save(this);
    }
}
