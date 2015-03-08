package ru.app.raspinf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;


public class PrepodActivity extends ActionBarActivity {

    RaspDB sqh;
    SQLiteDatabase sqdb;
    String[] data = {"Товштейн М.Я.", "Герасимова О.Ю.", "Мустафин А.Ф.", "Мингалеева Л.Б."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepod);


    /** Called when the activity is first created. */


        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_prepod);
        spinner.setAdapter(adapter);
        // выделяем элемент
        spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // заголовок
                String selectedItem = parent.getItemAtPosition(position).toString();
                Spinner mySpinner = (Spinner) findViewById(R.id.spinner_prepod);
                mySpinner.setPrompt(selectedItem);
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Выбранный Преподаватель = " + selectedItem, Toast.LENGTH_SHORT).show();
                GetDataForListWiev(null,selectedItem,"день");
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void GetDataForListWiev(List<String> list,String prepodname,String day){

        sqh = new RaspDB(getApplicationContext());
        sqdb = sqh.getWritableDatabase();

        //mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        //String prepod = mSettings.getString(MyRefs.COURSE, "");
        //String day = mSettings.getString(MyRefs.DAY, "");

        //Log.i("Group", group);

        String query = "SELECT " + MyRefs.TIME + ", " + MyRefs.PREDMET + " FROM " + MyRefs.RASP_TABLE_NAME
                + " WHERE " //+ MyRefs.GROUP + " = '" + group
                + "' AND " + MyRefs.DAY + " = '" + day
                + "' AND " + MyRefs.PREDMET + " = '%" + prepodname
                + "%'";// GROUP BY " + MyRefs.TIME ;

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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_prepod, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
