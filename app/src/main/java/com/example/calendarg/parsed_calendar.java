package com.example.calendarg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class parsed_calendar extends AppCompatActivity {
    String TAG ="main";
    ListView lv;
    public String[][] val;
    SQLiteDatabase db;
    public ArrayList categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsed_calendar);
        lv=findViewById(R.id.lv);
        db = openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS partable");
        db.execSQL("CREATE TABLE IF NOT EXISTS partable(dat VARCHAR,day VARCHAR,HW VARCHAR,hrsemugpg VARCHAR,fug VARCHAR,integrated VARCHAR,fpg VARCHAR,specifications VARCHAR);");

        readExcelFileFromAssets();
    }
    public void readExcelFileFromAssets() {
        String next[] = {};
        List<String[]> list = new ArrayList<String[]>();
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("table1.csv")));//Specify asset file name
            //in open();
            for(;;) {
                next = reader.readNext();
                if(next != null) {
                    list.add(next);
                } else {
                    break;
                }
            }
            categoryList = new ArrayList();
//            ArrayList<String[]> arlst=new ArrayList<String[]>();
            val=new String[1000][100];
            String curmon=" ",curval=" ";
            for (int i = 0; i < list.size(); i++) {
                String s="";
                for (int j=0;j<list.get(i).length;j++) {
                    String cell = list.get(i)[j];
                    if(cell==""){
                        list.get(i)[j]=" ";
                    }
                    if(ismon(cell) && j==0) {
                        curmon = cell;
                        curval = monthval(cell);
                    }
                    if(j==0){
                        if(isnum(list.get(i)[j])){
                            String year="";
                            for(int a=curmon.length()-4;a<curmon.length();a++){
                                year+=curmon.charAt(a);
                            }
                            String concat="";
                            concat=concat+year;
                            concat+=curval;
                            if(cell.length()==1)
                            concat=concat+"0"+cell;
                            else
                            concat+=cell;
                            cell=concat;
                        }
                    }
                    s += cell+" ";
                    val[i][j] =cell;
                }
                categoryList.add(s);
            }
            addtodatabase(val);
            ArrayAdapter ada=new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1,
                    categoryList);
            lv.setAdapter(ada);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    public void addtodatabase(String[][] val) {
        for(int i=1;i<val.length;i++){
            try{
                if (val[i][7].contains("'")) {
                    val[i][7] = val[i][7].replace("'", "");
                }
            }catch (Exception e){

            }
            db.execSQL("INSERT INTO partable VALUES('" + val[i][0] + "','" + val[i][1] +
                    "','" + val[i][2]+
                    "','" + val[i][3]+
                    "','" + val[i][4]+
                    "','" + val[i][5]+
                    "','" + val[i][6]+
                    "','" + val[i][7]+"');");
            //Toast.makeText(this,"Success Record added",Toast.LENGTH_SHORT).show();

        }

    }

    public String monthval(String cell) {
        if(cell.toLowerCase().contains("january"))
            return "-01-";
        if(cell.toLowerCase().contains("february"))
            return "-02-";
        if(cell.toLowerCase().contains("march"))
            return "-03-";
        if(cell.toLowerCase().contains("april"))
            return "-04-";
        if(cell.toLowerCase().contains("may"))
            return "-05-";
        if(cell.toLowerCase().contains("june"))
            return "-06-";
        if(cell.toLowerCase().contains("july"))
            return "-07-";
        if(cell.toLowerCase().contains("august"))
            return "-08-";
        if(cell.toLowerCase().contains("september"))
            return "-09-";
        if(cell.toLowerCase().contains("october"))
            return "-10-";
        if(cell.toLowerCase().contains("november"))
            return "-11-";
        if(cell.toLowerCase().contains("december"))
            return "-12-";
        return " ";
    }

    public boolean ismon(String cell) {
        String[] month={"january","february","march","april","may","june","july","august","september","october","november","december"};
        for(int i=0;i<month.length;i++) {
            if(cell.toLowerCase().contains(month[i])){
                return true;
            }
        }

        return false;
    }
    public boolean isnum(String cell) {
        Integer d;
        if (cell == null) {
            return false;
        }
        try {
            d = Integer.parseInt(cell);
        } catch (NumberFormatException nfe) {
            return false;
        }
        if (d > 0 && d<32){
            return true;
        }
        return false;
    }

    public void sync(View view) {
        Intent i=new Intent(this,MainActivity.class);
        setResult(RESULT_OK,i);
        finish();
    }


    public void showdb(View view) {
        Cursor c = db.rawQuery("SELECT * FROM partable", null);
        // Checking if no records found 
        if (c.getCount() == 0) {
            showMessage("Error", "No records found");
            return;
        }
        // Appending records to a string buffer 
        StringBuffer buffer = new StringBuffer();
        int i=0;
        while (c.moveToNext() && c.getString(0)!=null)
        {
            i+=1;
            buffer.append("DATE: " + c.getString(0) + "\n");
            buffer.append("DAY: " + c.getString(1) + "\n");
            buffer.append("H/W: " + c.getString(2) + "\n");
            buffer.append("Hr.Sem UG & PG: " + c.getString(3) + "\n");
            buffer.append("IUG: " + c.getString(4) + "\n");
            buffer.append("1 INTEGRATED: " + c.getString(5) + "\n");
            buffer.append("1 PG: " + c.getString(6) + "\n");
            buffer.append("SPECIFICATIONS: " + c.getString(7) + "\n\n");
        }
        // Displaying all records 
        showMessage("EVENTS", String.valueOf(i)+buffer.toString());
    }
}
