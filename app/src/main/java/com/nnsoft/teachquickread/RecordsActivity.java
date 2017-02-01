package com.nnsoft.teachquickread;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RecordsActivity extends AppCompatActivity {

    private TableLayout tlRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        tlRecords = (TableLayout) findViewById(R.id.tlRecordsTable);
        showTable();
    }

    private void showTable() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Record> result = realm.where(Record.class).findAll();
        result = result.sort("date", Sort.DESCENDING);

        int i = 0;
        for (Record rec : result) {
            int bkgColor = (i % 2 == 0) ? Color.parseColor("#D7D7D7") : Color.parseColor("#B7B7B7");
            //Создаём экземпляр инфлейтера, который понадобится для создания строки таблицы из шаблона. В качестве контекста у нас используется сама активити
            LayoutInflater inflater = LayoutInflater.from(this);
            //Создаем строку таблицы, используя шаблон из файла /res/layout/table_row.xml
            TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, null);
            //Находим ячейку для номера дня по идентификатору
            TextView tv = (TextView) tr.findViewById(R.id.colDate);
            DateFormat df = new SimpleDateFormat(Options.getDateFormat());
            tv.setText(df.format(rec.getDate()));
            tv.setBackgroundColor(bkgColor);

            tv = (TextView) tr.findViewById(R.id.colWords);
            tv.setText(Integer.toString(rec.getLenTextInWords()));
            tv.setBackgroundColor(bkgColor);

            tv = (TextView) tr.findViewById(R.id.colTime);
            tv.setText(Integer.toString(rec.getTimeInSecs()));
            tv.setBackgroundColor(bkgColor);

            tv = (TextView) tr.findViewById(R.id.colSpeed);
            tv.setText(Integer.toString(rec.getSpeed()));
            tv.setBackgroundColor(bkgColor);

            tlRecords.addView(tr);
            i++;
        }

    }

}
