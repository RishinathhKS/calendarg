package com.example.calendarg;

//import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class synccal extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DatePicker stdate,enddate;
    TextView txt;
    String sdate,edate;
    int smon,emon;
    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        stdate=findViewById(R.id.startDat);
        enddate=findViewById(R.id.endDat);
        sdate=Integer.toString(stdate.getYear())+"-"+Integer.toString(stdate.getMonth())+"-"+Integer.toString(stdate.getDayOfMonth());
        edate=Integer.toString(stdate.getYear())+"-"+Integer.toString(enddate.getMonth())+"-"+Integer.toString(enddate.getDayOfMonth());
//        stdate.setOnDateChangedListener(this);
//        enddate.setOnDateChangedListener(this);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        edate=Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(dayOfMonth);
    }

    public void syncnow(View view) {
        smon=stdate.getMonth()+1;
        emon=enddate.getMonth()+1;
        String smon2,emon2;
        smon2=Integer.toString(smon);
        emon2=Integer.toString(emon);
        if(smon<10)
            smon2="0"+Integer.toString(smon);
        if(emon<10)
            emon2="0"+Integer.toString(emon);

        sdate=Integer.toString(stdate.getYear())+"-"+smon2+"-"+Integer.toString(stdate.getDayOfMonth());
        edate=Integer.toString(enddate.getYear())+"-"+emon2+"-"+Integer.toString(enddate.getDayOfMonth());
        SharedPreferences sp = getSharedPreferences
                ("mycredentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("startdate",sdate);
        edit.putString("enddate",edate);
        edit.putBoolean("sync",true);
        edit.commit();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);

    }
}
