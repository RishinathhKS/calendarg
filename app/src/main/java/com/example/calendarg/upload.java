package com.example.calendarg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class upload extends AppCompatActivity implements
        AdapterView.OnItemClickListener{
    ListView download;
    String parse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getSupportActionBar().hide();

        download=findViewById(R.id.downloadpage);
        String filePath = Environment.getExternalStorageDirectory() + "/" + "Download/" ;
        File dir = new File(filePath);
        File[] filelist = dir.listFiles((FileFilter) FileFileFilter.FILE);
        Arrays.sort(filelist, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        String[] theNamesOfFiles = new String[filelist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            theNamesOfFiles[i] = filelist[i].getName();
        }
        ArrayAdapter ada = new ArrayAdapter(this,
                R.layout.custom_simple_list_item_1,
                theNamesOfFiles);
        download.setAdapter(ada);
        download.setOnItemClickListener(this);


    }

    public void pdf2excl(View view) {
        Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("http://altoconvertpdftoexcel.com"));;
        startActivityForResult(i,1523);

    }
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1523) {

            String filePath = Environment.getExternalStorageDirectory() + "/" + "Download/" ;
            File dir = new File(filePath);
            File[] filelist = dir.listFiles((FileFilter) FileFileFilter.FILE);
            Arrays.sort(filelist, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            String[] theNamesOfFiles = new String[filelist.length];
            for (int i = 0; i < theNamesOfFiles.length; i++) {
                theNamesOfFiles[i] = filelist[i].getName();
            }
            ArrayAdapter ada = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1,
                    theNamesOfFiles);
            download.setAdapter(ada);
            download.setOnItemClickListener(this);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txt = (TextView) view;
        parse=txt.getText().toString();
        Toast.makeText(getApplicationContext(), "You have selected : " + parse,
               Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirmation...");
        alertDialog.setMessage("Are you sure you want parse "+parse);
        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "You clicked on YES :   "+which, Toast.LENGTH_SHORT).show();
                callparsing(parse);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // Setting Netural "Cancel" Button
        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // Showing Alert Message
        alertDialog.show();


    }

    public void callparsing(String pars){
        SharedPreferences sp = getSharedPreferences
                ("mycredentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("name",pars);
        edit.commit();
        Intent j = new Intent(this, parsed_calendar.class);
        startActivity(j);
    }
}
