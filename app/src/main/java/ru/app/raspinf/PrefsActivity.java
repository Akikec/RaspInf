package ru.app.raspinf;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;


public class PrefsActivity extends Activity {

    SharedPreferences mSettings;
    String mCourse;
    Spinner spinner_group;
    ArrayList<String> groups_list;
    ArrayAdapter<String> adapter_group;
    Spinner spinner_course;
    ArrayAdapter<String> adapter_course;
    String mGroup;
    Integer mCoureId,mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mCourse = mSettings.getString(MyRefs.COURSE, "");
        mGroup = mSettings.getString(MyRefs.GROUP,"");
        mCoureId = mSettings.getInt("CourseSpinerID",0);
        mGroupId = mSettings.getInt("GroupeSpinerID",0);

        groups_list = new ArrayList<>();
        spinner_group = (Spinner) findViewById(R.id.spinner_group);

        spinner_course = (Spinner) findViewById(R.id.spinner_course);
        adapter_course = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, PresentRaspActivity.mCourseArray);
        spinner_course.setAdapter(adapter_course);

        groups_list = new ArrayList<>();
        GetSpinerListFromDB(getBaseContext(), groups_list, mGroup);
        adapter_group = new ArrayAdapter<>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item, groups_list);
        spinner_group.setAdapter(adapter_group);

        spinner_course.setSelection(mCoureId);
        spinner_group.setSelection(mGroupId);

        spinner_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                Spinner mySpinner = (Spinner) findViewById(R.id.spinner_course);
                mySpinner.setPrompt(selectedItem);




                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(MyRefs.COURSE, Integer.toString(position + 1));
                editor.putInt("CourseSpinerID", position );
                Log.i("String settnigs", MyRefs.COURSE + " || " + Integer.toString(position + 1));
                editor.apply();


                groups_list = new ArrayList<>();
                GetSpinerListFromDB(getBaseContext(), groups_list, Integer.toString(position + 1));
                adapter_group = new ArrayAdapter<>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item, groups_list);
                spinner_group.setAdapter(adapter_group);
                adapter_group.notifyDataSetChanged();
                spinner_group.setSelection(mGroupId);

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       /* spinner_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                Spinner mySpinner = (Spinner) findViewById(R.id.spinner_course);
                mySpinner.setPrompt(selectedItem);


                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(MyRefs.GROUP, selectedItem);
                editor.putInt("GroupeSpinerID", position );
                Log.v("String settnigs", MyRefs.GROUP + " || " + selectedItem);
                editor.apply();

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/




        //getBaseContext()

    }

    @Override
    protected void onResume() {

        mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mCourse = mSettings.getString(MyRefs.COURSE, "");
        mGroup = mSettings.getString(MyRefs.GROUP,"");
        mCoureId = mSettings.getInt("CourseSpinerID",0);
        mGroupId = mSettings.getInt("GroupeSpinerID",0);


        spinner_course.setSelection(mCoureId);
        spinner_group.setSelection(mGroupId);

        super.onResume();
    }

    @Override
    protected void onPause() {
        int id = spinner_group.getSelectedItemPosition();
        String selectedItem = spinner_group.getItemAtPosition(id).toString();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(MyRefs.GROUP, selectedItem);
        editor.putInt("GroupeSpinerID", id );
        Log.v("String settnigs", MyRefs.GROUP + " || " + selectedItem);
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void GetSpinerListFromDB(Context cont,ArrayList<String> list,String course){

        RaspDB sqh = new RaspDB(cont);
        SQLiteDatabase sqdb = sqh.getWritableDatabase();

        String query = "SELECT " + MyRefs.GROUP  + " FROM " + MyRefs.RASP_TABLE_NAME
                + " WHERE " + MyRefs.COURSE +" = '" + course
                + "' GROUP BY " + MyRefs.GROUP;
        Cursor cursorForIteration = sqdb.rawQuery(query, null);
        while (cursorForIteration.moveToNext()) {
            String name = cursorForIteration.getString(cursorForIteration.getColumnIndex(MyRefs.GROUP));
            list.add(name);
            Log.i("LOG_TAG", "Group NAME " + name);
        }
        cursorForIteration.close();

        sqdb.close();
        sqh.close();

    }


    public void button_finis(View view) {
        finish();
    }
}
