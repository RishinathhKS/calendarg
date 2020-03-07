package com.example.calendarg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class upload extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


    }

    public void pdf2excl(View view) {
        Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("http://altoconvertpdftoexcel.com"));;
        startActivity(i);

    }

    public void excl2csv(View view) {
        Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://onlineconvertfree.com/convert-format/xls-to-csv/"));;
        startActivity(i);
    }

    public void parse(View view) {
        Intent i = new Intent(this,
                parsed_calendar.class);
        startActivity(i);
    }

}
