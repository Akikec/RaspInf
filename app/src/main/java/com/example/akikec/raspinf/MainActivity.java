package com.example.akikec.raspinf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File database=getApplicationContext().getDatabasePath(MyRefs.DATABASE_NAME);

        if (database.delete()){Log.i("Database", "Deleted");}
        Log.i("Database", String.valueOf(getDatabasePath(MyRefs.DATABASE_NAME)) );
        String url ="http://raspinf.my1.ru/kurs_";

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            Log.i("Database", "Not Found");

            for (int i = 1 ; i < 5;i++){
                excelURL(url+i+".xls",i);
                //exelTablesList.clear();
            }

        } else {
            Log.i("Database", "Found");

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick_Student(View view) {
            Intent sendStudent = new Intent(MainActivity.this,StudentActivity.class);
            startActivity(sendStudent);
    }
    public void onClick_Prepod(View view) {
        //Intent sendPrepod = new Intent(MainActivity.this,PrepodActivity.class);
        //startActivity(sendPrepod);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Функция не доступна", Toast.LENGTH_LONG);
        toast.show();
    }

    public void excelURL(String url,int i) {
        Log.v("excelURL:", url);
        new ExcelURL(i).execute(url);
    }

    private class ExcelURL extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        int course;
        String URL;

        private ExcelURL(int i){
            this.course = i;
        }

        protected void onPreExecute() {
            dialog.setMessage("Getting your data... Please wait...");
            dialog.show();
        }

        protected String doInBackground(String... urls) {

            URL = urls[0];
                try {
                    Log.e("AsyncTask: ", Integer.toString(course)+" || "+ URL);
                    HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                    HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                    ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
                    ArrayList<ExelTable> exelTablesList;
                    HttpGet httpGet = new HttpGet(URL);
                    response = httpclient.execute(httpGet);

                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        Log.e("AsyncTask_OK: ", "Connection establish");
                        exelTablesList = parseExcel(response.getEntity().getContent());
                        FromExelToDB(exelTablesList,course);
                    } else {
                        Log.w("HTTP1:", statusLine.getReasonPhrase());
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    Log.w("HTTP2:", e);
                    content = e.getMessage();
                    cancel(true);
                } catch (IOException e) {
                    Log.w("HTTP3:", e);
                    content = e.getMessage();
                    cancel(true);
                } catch (Exception e) {
                    Log.w("HTTP4:", e);
                    content = e.getMessage();
                    cancel(true);
                }

            return content;
        }

        protected void onCancelled() {
            dialog.dismiss();
            Toast toast = Toast.makeText(MainActivity.this, "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }

        protected void onPostExecute(String content) {
            dialog.dismiss();
        }
    }


    private ArrayList<ExelTable> parseExcel(InputStream fis) {

        ArrayList<ExelTable> exelTablesList = new ArrayList<ExelTable>();

        /*String resName = "kurs_" + String.valueOf(course);
        Log.i("name", resName);
        int id = getResources().getIdentifier(resName, "raw", "com.example.akikec.raspinf");
        */


        try {
            // Create a workbook using the Input Stream || getResources().openRawResource(id)
            Workbook workbook = new HSSFWorkbook(fis);
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

                    Log.v("Table " + fis.toString() + ": ",cellValue + " ID: " + rowIndex + "||" + collomIndex );

                }
                if(isFirst){isFirst=false;}
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return  exelTablesList;

    }

    private void SendToDB(int course,String day,String time,String predmet,String group){


        RaspDB sqh = new RaspDB(getApplicationContext());
        SQLiteDatabase sqdb = sqh.getWritableDatabase();

        String insertQuery = "INSERT INTO " + MyRefs.RASP_TABLE_NAME
                + " (" + MyRefs.DAY + ", " + MyRefs.TIME + ", " + MyRefs.PREDMET + ", " + MyRefs.GROUP + ", " + MyRefs.COURSE +  " )"
                + " VALUES ('" + day + "', '" + time + "', '" + predmet + "', '" + group + "', '" + String.valueOf(course) + "')";
        sqdb.execSQL(insertQuery);

        Log.i("SQL Query", insertQuery);

        sqdb.close();
        sqh.close();

    }

    private void FromExelToDB(ArrayList<ExelTable> exelTablesList,int course){
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

}
