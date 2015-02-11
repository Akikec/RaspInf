package com.example.akikec.raspinf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.Keyboard;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("deprecation")
public class PresentRaspActivity extends Activity {

    ArrayList<ExelTable> exelTablesList;
    RaspDB sqh;
    SQLiteDatabase sqdb;
    String mDay;
    String mGroup;
    String mCourse;
    SharedPreferences mSettings;
    public static final String[] mCourseArray ={"1 Курс","2 Курс", "3 Курс","4 Курс"};
    String [] dataDate = {"ПОНЕДЕЛЬНИК","ВТОРНИК","СРЕДА","ЧЕТВЕРГ", "ПЯТНИЦА","СУББОТА"};
    TextView group_Text;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_rasp);

        File database=getApplicationContext().getDatabasePath(MyRefs.DATABASE_NAME);

        //if (database.delete()){Log.i("Database", "Deleted");}

        group_Text = (TextView) findViewById(R.id.textViewGroup);




        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            Log.i("Database", "Not Found");

            for (int i = 1 ; i < 5;i++){

                parseExcel(i);
                FromExelToDB(i);
                exelTablesList.clear();
            }

        } else {
            Log.i("Database", "Found");

        }



        Spinner spinerDay = (Spinner) findViewById(R.id.spinner);
        final ArrayList<String> spinerListDate = new ArrayList<String>();
        Collections.addAll(spinerListDate, dataDate);


        CreateSpinner(this, spinerDay, spinerListDate,MyRefs.DAY);

    }

    @Override
    protected void onResume() {


        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        mCourse = mSettings.getString(MyRefs.COURSE, "");
        mDay = mSettings.getString(MyRefs.DAY, "ПОНЕДЕЛЬНИК");
        mGroup = mSettings.getString(MyRefs.GROUP,"");



        if (mCourse.equals("")){
            Intent intent = new Intent();
            intent.setClass(this, PrefsActivity.class);
            startActivity(intent);}

        displayRasp(this);

        group_Text.setText(mGroup);


        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void CreateSpinner(final Context cont, Spinner spinner, final ArrayList<String> data,final String key) {



        ArrayAdapter<String> spinnerAdapterGroup = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(spinnerAdapterGroup);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();

                Spinner mySpiner = (Spinner) findViewById(R.id.spinner);
                mySpiner.setPrompt(selectedItem);

                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(key, selectedItem);
                Log.v("String settnigs", key + " || " + selectedItem);
                editor.apply();

                displayRasp(cont);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        sqdb.close();
        sqh.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(this, PrefsActivity.class);
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    /*@Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {

            case 0:
            AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(this);
            builder.setTitle("Выбирете курс");

            builder.setItems(mCourseArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(getString(R.string.course), mCourseArray[item]);
                    Log.i("String settnigs", getString(R.string.course) + "   ||  " + mCourseArray[item]);
                    editor.apply();
                }
            });
            builder.setCancelable(false);
            return builder.create();

            default: return null;
        }
    }*/


    private void SendToDB(int course,String day,String time,String predmet,String group){


        sqh = new RaspDB(getApplicationContext());
        sqdb = sqh.getWritableDatabase();

        String insertQuery = "INSERT INTO " + MyRefs.RASP_TABLE_NAME
                + " (" + MyRefs.DAY + ", " + MyRefs.TIME + ", " + MyRefs.PREDMET + ", " + MyRefs.GROUP + ", " + MyRefs.COURSE +  " )"
                + " VALUES ('" + day + "', '" + time + "', '" + predmet + "', '" + group + "', '" + String.valueOf(course) + "')";
        sqdb.execSQL(insertQuery);

        Log.i("SQL Query", insertQuery);

        sqdb.close();
        sqh.close();

    }

    private void FromExelToDB(int course){
        ArrayList<String> tableDate = exelTablesList.get(0).getCellList() ;
        ArrayList<String> tableTime = exelTablesList.get(1).getCellList() ;
        if (tableTime.size()<tableDate.size()){tableTime.add("");}
        String s = "";
        for (int i=2;i<exelTablesList.size();i++ ) {
            ArrayList<String> table = exelTablesList.get(i).getCellList() ;

            for (int j = 1, l = table.size(); j < l; j++) {
                if (!table.get(j).equals("0.0") && !s.equals(table.get(0))) {
                    SendToDB(course, tableDate.get(j), tableTime.get(j), table.get(j),table.get(0));
                }
            }

            s = table.get(0);
        }

    }


    private void GetDataForListWiev(List<String> list){

        sqh = new RaspDB(getApplicationContext());
        sqdb = sqh.getWritableDatabase();

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        String group = mSettings.getString(MyRefs.GROUP,"");
        String course = mSettings.getString(MyRefs.COURSE, "");
        String day = mSettings.getString(MyRefs.DAY, "");

        Log.i("Group", group);

        String query = "SELECT " + MyRefs.TIME + ", " + MyRefs.PREDMET + " FROM " + MyRefs.RASP_TABLE_NAME
                + " WHERE " + MyRefs.GROUP + " = '" + group
                + "' AND " + MyRefs.DAY + " = '" + day
                + "' AND " + MyRefs.COURSE + " = '" + course
                + "' GROUP BY " + MyRefs.TIME ;

        Log.i("SQL Query", query);
        Cursor cursorForIteration = sqdb.rawQuery(query, null);
        while (cursorForIteration.moveToNext()) {
            String time = cursorForIteration.getString(cursorForIteration.getColumnIndex(MyRefs.TIME));
            String name = cursorForIteration.getString(cursorForIteration.getColumnIndex(MyRefs.PREDMET));
            list.add(time + "\n" + name);
            Log.i("LOG_TAG", "Time:  " + time + " Predmet: " + name);
        }
        cursorForIteration.close();

        sqdb.close();
        sqh.close();
    }



    private void displayRasp(Context cont) {


        List<String> myList = new ArrayList<String>();
        GetDataForListWiev(myList);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(cont,android.R.layout.simple_list_item_1, myList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(dataAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                String substr=selectedItem.substring(0,selectedItem.indexOf("\n"));
                Toast toast = Toast.makeText(getApplicationContext(),substr,Toast.LENGTH_LONG);
                toast.show();
                return true;
            }
        });



    }


    private void parseExcel(int course) {

        exelTablesList = new ArrayList<ExelTable>();

        String resName = "kurs_" + String.valueOf(course);
        Log.i("name", resName);
        int id = getResources().getIdentifier(resName, "raw", "com.example.akikec.raspinf");



        try {
            // Create a workbook using the Input Stream
            Workbook workbook = new HSSFWorkbook(getResources().openRawResource(id));
            // Get the first sheet from workbook
            Sheet mySheet = workbook.getSheetAt(0);

            List<CellRangeAddress> regionsList = new ArrayList<CellRangeAddress>();
            for(int i = 0; i < mySheet.getNumMergedRegions(); i++) {
                regionsList.add(mySheet.getMergedRegion(i));
            }

            // We now need something to iterate through the cells
            boolean isFirst = true;

            Iterator<Row> rowIter = mySheet.rowIterator();
            while(rowIter.hasNext()){

                Row myRow = (Row) rowIter.next();

                int collomIndex;
                int rowIndex = myRow.getRowNum();

                if(myRow.getRowNum() < 5) {
                    continue;
                }

                Iterator<Cell> cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){



                    Cell myCell = (Cell) cellIter.next();
                    Cell dataCell = myCell;
                    String cellValue = "";
                    collomIndex = myCell.getColumnIndex();
                    ExelTable table;
                    ArrayList<String> stringList;

                    if (collomIndex>10){break;}

                    if (isFirst) {table = new ExelTable(); stringList = new ArrayList<String>();}else {table = exelTablesList.get(collomIndex);stringList=table.getCellList();}


                    for(CellRangeAddress region : regionsList) {
                        if(region.isInRange(myCell.getRowIndex(), collomIndex)) {
                            int myRowNum = region.getFirstRow();
                            int myColNum = region.getFirstColumn();
                            dataCell = mySheet.getRow(myRowNum).getCell(myColNum);
                            break;
                        }
                    }

                    // Check for cell Type
                    if(dataCell.getCellType() == Cell.CELL_TYPE_STRING){
                        cellValue = dataCell.getStringCellValue();
                    }
                    else {
                        cellValue = String.valueOf(dataCell.getNumericCellValue());
                    }

                    stringList.add(cellValue);
                    table.setCellList(stringList);

                    if (isFirst){exelTablesList.add(table);}

                    Log.v("Table " + resName + ": ",cellValue + " ID: " + rowIndex + "||" + collomIndex );

                }
                if(isFirst){isFirst=false;}
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

