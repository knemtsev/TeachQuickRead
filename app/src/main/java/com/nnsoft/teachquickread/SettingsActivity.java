package com.nnsoft.teachquickread;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.travijuu.numberpicker.library.NumberPicker;

import java.io.File;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private TextView edFileToRead;
    private Spinner spFileToRead;
    private NumberPicker npFontSize;
    private NumberPicker npWordsNumber;
    private NumberPicker npMaxSpeed;
    private CheckBox cbSkipVowels;
    private CheckBox cbUseHyphen;
    Button btnChooseFile;
    Button btnClearCache;

    private static final int READ_REQUEST_CODE = 42;
    private static final int FILE_CODE = 43;
    private static final String TAG = "Settings";
    FilePickerDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        edFileToRead = (TextView) findViewById(R.id.edChooseFileToRead);
//        edFileToRead.setText(Options.getFileNameToRead());

        spFileToRead = (Spinner) findViewById(R.id.spChooseFileToRead);

        btnChooseFile = (Button) findViewById(R.id.btnChooseFile);
        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(Options.getLastFolder());
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = null;

                dialog = new FilePickerDialog(SettingsActivity.this, properties);
                dialog.setTitle(getResources().getString(R.string.choose_file_for_reading));
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        //files is the array of the paths of files selected by the Application User.
                        //Log.i(TAG,files[0]);
                        //edFileToRead.setText(files[0]);
                        if(files.length>0) {
                            int li = files[0].lastIndexOf("/");
                            if (li > 0) Options.setLastFolder(files[0].substring(0, li));
                            Options.addFileNameToList(files[0]);
                            Options.setFileNameToRead(files[0]);
                            Options.setFileLoaded(false);
                            setFileList();
                        }
                    }
                });
                dialog.show();

            }
        });

        btnClearCache=(Button)findViewById(R.id.btnClearCache);
        btnClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.getCache().clear();
            }
        });

        npWordsNumber = (NumberPicker) findViewById(R.id.npNumWords);
        npWordsNumber.setValue(Options.getWordsNum());

        npFontSize = (NumberPicker) findViewById(R.id.npFontSize);
        npFontSize.setValue(Options.getFontSize());

        npMaxSpeed = (NumberPicker) findViewById(R.id.npMaxSpeed);
        npMaxSpeed.setValue(Options.getMaxSpeed());

        cbSkipVowels = (CheckBox) findViewById(R.id.chSkipVowels);
        cbSkipVowels.setChecked(Options.isSkipVowels());

        cbUseHyphen =(CheckBox) findViewById(R.id.cbUseHyphenation);
        cbUseHyphen.setChecked(Options.isUseHyphenation());

        spFileToRead.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                Set<String> fileList=Options.getFileNameList();
                if(fileList!=null) {
                    String[] list = (String[]) fileList.toArray(new String[fileList.size()]);
                    Options.setFileNameToRead(list[selectedItemPosition]);
                    Options.setFileLoaded(false);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        setFileList();
    }

    private void setFileList() {
        Set<String> fileList=Options.getFileNameList();
        if(fileList!=null) {
            String[] list = (String[]) fileList.toArray(new String[fileList.size()]);
            String[] listShort=new String[list.length];
            for(int i=0; i<list.length; i++)
            {
                listShort[i]=list[i].substring(list[i].lastIndexOf("/")+1);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listShort);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFileToRead.setAdapter(adapter);
            int num=0;
            String curName=Options.getFileNameToRead();
            for(int i=0; i<list.length; i++)
            {
                if(curName.equalsIgnoreCase(list[i])) {
                    num = i;
                    break;
                }
            }
            spFileToRead.setSelection(num);
        }

    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(SettingsActivity.this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
//        Options.setFileNameToRead(edFileToRead.getText().toString());
        Options.setFontSize(npFontSize.getValue());
        Options.setWordsNum(npWordsNumber.getValue());
        Options.setMaxSpeed(npMaxSpeed.getValue());
        Options.setSkipVowels(cbSkipVowels.isChecked());
        Options.setUseHyphenation(cbUseHyphen.isChecked());
        Options.save(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.


        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.getPath());
//                edFileToRead.setText(uri.toString());
//                Options.fileNameToRead=uri.toString();
                edFileToRead.setText(uri.getPath());
            }
        }
    }
}
