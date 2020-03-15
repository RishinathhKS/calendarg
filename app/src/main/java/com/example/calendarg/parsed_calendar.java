package com.example.calendarg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

        getSupportActionBar().hide();

        lv=findViewById(R.id.lv);
        db = openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS partable");
        db.execSQL("CREATE TABLE IF NOT EXISTS partable(dat date,day VARCHAR,HW VARCHAR,hrsemugpg VARCHAR,fug VARCHAR,integrated VARCHAR,fpg VARCHAR,specifications VARCHAR);");

        readExcelFileFromAssets();
    }
    public void readExcelFileFromAssets() {
        String next[] = {};
        List<String[]> list = new ArrayList<String[]>();
        try {
            SharedPreferences sp = getSharedPreferences("mycredentials",
                    Context.MODE_PRIVATE);
            String name = sp.getString("name","NA");


            String yourFilePath = Environment.getExternalStorageDirectory() + "/" + "Download" + "/" + name;
            File filename = new File(yourFilePath);
            FileInputStream fis = new FileInputStream(filename);
//            File notes = new File(filePath); //getting the notes dir
//            List<String> lines = new ArrayList<>();
//
//            for (File file : notes.listFiles()) { //iterating the files in the dir
//                lines.add(file.getName());
//            }



            InputStream inputStream = new FileInputStream(filename);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();
            categoryList = new ArrayList();
            val = new String[2000][100];
            String curmon = " ", curval = " ";
            //outter loop, loops through rows
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                String st = "";
                //inner loop, loops through columns
                for (int c = 0; c < cellsCount; c++) {
                    //handles if there are to many columns on the excel sheet.
                    String value = getCellAsString(row, c, formulaEvaluator);
                    String cellInfo = "r:" + r + "; c:" + c + "; v:" + value;
                    //Log.d(TAG, "readExcelData: Data from row: " + cellInfo);
                    if (value == "") {
                        value = " ";
                    }
                    if(ismon(value) ) {
                        curmon = value;
                        curval = monthval(value);
                    }
                    if(c==0){
                        if(isnum(value)){
                            String year="";
                            for(int a=curmon.length()-4;a<curmon.length();a++){
                                year+=curmon.charAt(a);
                            }
                            String concat="";
                            concat=concat+year;
                            concat+=curval;
                            if(value.length()==1)
                                concat=concat+"0"+value;
                            else
                                concat+=value;
                            value=concat;

                        }}

                    st += value + " ";
                    val[r][c] = value;
                }
                categoryList.add(st);
                //txt.setText(sb.toString());
            }
            ArrayAdapter ada = new ArrayAdapter(this,
                    R.layout.custom_simple_list_item_1,
                    categoryList);
            lv.setAdapter(ada);
            addtodatabase(val);

        } catch (Exception e) {
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
    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            // value = ""+cellValue.getStringValue();
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = "" + cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    int v=(int) numericValue;
                    value = "" + v;

                    break;
                case Cell.CELL_TYPE_STRING:
                    value = "" + cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {

        }
        return value;
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
        if(cell==" ")
            return false;
        if(cell.length()<3)
            return false;
        String y="";
        String[] month={"january","february","march","april","may","june","july","august","september","october","november","december"};
        try{
        for(int a=cell.length()-4;a<cell.length();a++){
            y+=cell.charAt(a);
        }
        if(Integer.parseInt(y)>2000) {
            if (cell.contains("-")) {
                for (int i = 0; i < month.length; i++) {
                    if (cell.toLowerCase().contains(month[i])) {
                        return true;
                    }
                }
            }
        }}catch (Exception e){
            return false;
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
        Intent i=new Intent(this,synccal.class);
        startActivity(i);
//        setResult(RESULT_OK,i);
//        finish();
    }


    public void showdb(View view) {
//        String a="'2020-01-01'";
//        String b="'2020-01-31'";
        Cursor c = db.rawQuery("SELECT * FROM partable ", null);
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
