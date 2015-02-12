package com.example.akikec.raspinf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class PresentRaspActivity extends Activity implements View.OnTouchListener {

    private final static int MOVE_LENGTH = 150;

    RaspDB sqh;
    SQLiteDatabase sqdb;
    String mDay;
    String mGroup;
    String mCourse;
    SharedPreferences mSettings;
    public static final String[] mCourseArray ={"1 Курс","2 Курс", "3 Курс","4 Курс"};
    String [] data_array = {"ПОНЕДЕЛЬНИК","ВТОРНИК","СРЕДА","ЧЕТВЕРГ", "ПЯТНИЦА","СУББОТА"};
    TextView group_Text;

    private ViewFlipper flipper = null;
    private float fromPosition;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_rasp);

        group_Text = (TextView) findViewById(R.id.textViewGroup);


        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        mainLayout.setOnTouchListener(this);

        // Получаем объект ViewFlipper
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        // Создаем View и добавляем их в уже готовый flipper
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layouts[] = new int[]{ R.layout.day_1, R.layout.day_2, R.layout.day_3, R.layout.day_4 ,R.layout.day_5, R.layout.day_6};
        for (int layout : layouts)
            flipper.addView(inflater.inflate(layout, null));

        for(int i = 0 ;i<data_array.length;i++){
            displayRasp(this,data_array[i],i+1);
        }





        /*Spinner spinerDay = (Spinner) findViewById(R.id.spinner);
        final ArrayList<String> spinerListDate = new ArrayList<>();
        Collections.addAll(spinerListDate, dataDate);
        CreateSpinner(this, spinerDay, spinerListDate,MyRefs.DAY);*/

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

        //displayRasp(this);

        group_Text.setText(mGroup);


        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*private void CreateSpinner(final Context cont, Spinner spinner, final ArrayList<String> data,final String key) {



        ArrayAdapter<String> spinnerAdapterGroup = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, data);
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

    }*/

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

    public boolean onTouch(View view, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float toPosition = event.getX();
                // MOVE_LENGTH - расстояние по оси X, после которого можно переходить на след. экран
                if ((fromPosition - MOVE_LENGTH) > toPosition)
                {
                    fromPosition = toPosition;
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_out));
                    flipper.showNext();
                }
                else if ((fromPosition + MOVE_LENGTH) < toPosition)
                {
                    fromPosition = toPosition;
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_out));
                    flipper.showPrevious();
                }
            /*case MotionEvent.ACTION_UP:
                float toPosition_onUp = event.getX();
                if (fromPosition == toPosition_onUp)
                {

                }*/
            default:
                break;
        }
        return true;
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


    private void GetDataForListWiev(List<String> list,String day){

        sqh = new RaspDB(getApplicationContext());
        sqdb = sqh.getWritableDatabase();

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        String group = mSettings.getString(MyRefs.GROUP,"");
        String course = mSettings.getString(MyRefs.COURSE, "");
        //String day = mSettings.getString(MyRefs.DAY, "");

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



    private void displayRasp(Context cont,String day,int i) {


        String resName_List = "listView" + String.valueOf(i);
        String resName_Text = "textView" + String.valueOf(i);
        Log.i("name", resName_List);
        int id_list_view = getResources().getIdentifier(resName_List, "id", "com.example.akikec.raspinf");
        int id_text_view = getResources().getIdentifier(resName_Text, "id", "com.example.akikec.raspinf");
        Log.i("name", Integer.toString(id_list_view));
        List<String> myList = new ArrayList<>();
        GetDataForListWiev(myList,day);

        TextView textView = (TextView) findViewById(id_text_view);

        textView.setText(day);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(cont,android.R.layout.simple_list_item_1, myList);
        ListView listView = (ListView) findViewById(id_list_view);
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
        listView.setOnTouchListener(this);



    }





}

