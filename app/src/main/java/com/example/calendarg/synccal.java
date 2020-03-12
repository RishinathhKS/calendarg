package com.example.calendarg;

//import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class synccal extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DatePicker stdate,enddate;
    TextView txt;
    String sdate,edate;
    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stdate=findViewById(R.id.startDat);
        enddate=findViewById(R.id.endDat);
        sdate=Integer.toString(stdate.getYear())+"-"+Integer.toString(stdate.getMonth())+"-"+Integer.toString(stdate.getDayOfMonth());
        edate=Integer.toString(stdate.getYear())+"-"+Integer.toString(stdate.getMonth())+"-"+Integer.toString(stdate.getDayOfMonth());
        txt=findViewById(R.id.txt);
        txt.setText(sdate+edate);
//        stdate.setOnDateChangedListener(this);
//        enddate.setOnDateChangedListener(this);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        edate=Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(dayOfMonth);
        txt.setText(edate);
    }

    public void syncnow(View view) {
        sdate=Integer.toString(stdate.getYear())+"-"+Integer.toString(stdate.getMonth())+"-"+Integer.toString(stdate.getDayOfMonth());
        edate=Integer.toString(enddate.getYear())+"-"+Integer.toString(stdate.getMonth())+"-"+Integer.toString(stdate.getDayOfMonth());
        txt.setText(sdate+edate);
    }
}
