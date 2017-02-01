package com.nnsoft.teachquickread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {

    TableLayout tl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        int words = intent.getIntExtra("words", -1);
        int speed = intent.getIntExtra("speed", -1);
        int time = intent.getIntExtra("time", -1);

        tl = (TableLayout) findViewById(R.id.tlRecord);

        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow tr = (TableRow) inflater.inflate(R.layout.record_data_row, null);

        TextView tv = (TextView) tr.findViewById(R.id.recordCol1);
        tv.setText(R.string.Date);

        tv = (TextView) tr.findViewById(R.id.recordCol2);
        tv.setText(date);

        tl.addView(tr);

        tr = (TableRow) inflater.inflate(R.layout.record_data_row, null);
        tv = (TextView) tr.findViewById(R.id.recordCol1);
        tv.setText(R.string.words);

        tv = (TextView) tr.findViewById(R.id.recordCol2);
        tv.setText("" + words);

        tl.addView(tr);

        tr = (TableRow) inflater.inflate(R.layout.record_data_row, null);
        tv = (TextView) tr.findViewById(R.id.recordCol1);
        tv.setText(R.string.time);

        tv = (TextView) tr.findViewById(R.id.recordCol2);
        tv.setText("" + time);

        tl.addView(tr);

        tr = (TableRow) inflater.inflate(R.layout.record_data_row, null);
        tv = (TextView) tr.findViewById(R.id.recordCol1);
        tv.setText(R.string.speed);

        tv = (TextView) tr.findViewById(R.id.recordCol2);
        tv.setText("" + speed);

        tl.addView(tr);

    }
}
