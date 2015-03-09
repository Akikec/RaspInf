package ru.app.raspinf;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;


public class PrepodActivity extends Activity implements View.OnTouchListener {


    private final static int MOVE_LENGTH = 150;


    RaspDB sqh;
    SQLiteDatabase sqdb;
    private ViewFlipper flipper = null;
    String[] data = {"Товштейн М.Я.", "Герасимова О.Ю.", "Мустафин А.Ф.", "Мингалеева Л.Б."};
    String [] data_array = {"ПОНЕДЕЛЬНИК","ВТОРНИК","СРЕДА","ЧЕТВЕРГ", "ПЯТНИЦА","СУББОТА"};
    View contentView;
    Context mContext;

    private float fromPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepod);

        contentView = findViewById(R.id.prepod_main_layout);
        mContext = this;

        contentView.setOnTouchListener(this);

        /** Called when the activity is first created. */

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        flipper = (ViewFlipper) findViewById(R.id.flipperPrepod);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layouts[] = new int[]{ R.layout.dayforprepod_1, R.layout.dayforprepod_2, R.layout.dayforprepod_3, R.layout.dayforprepod_4 ,R.layout.dayforprepod_5, R.layout.dayforprepod_6};
        for (int layout : layouts)
            flipper.addView(inflater.inflate(layout, null));

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
                for(int i = 0 ;i<data_array.length;i++){
                    displayRasp(mContext,data_array[i],selectedItem,i+1);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private List<String> GetDataForListWiev(String prepodname,String day){

        sqh = new RaspDB(getApplicationContext());
        sqdb = sqh.getWritableDatabase();
        List<String> myList = new ArrayList<>();

        //mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        //String prepod = mSettings.getString(MyRefs.COURSE, "");
        //String day = mSettings.getString(MyRefs.DAY, "");

        //Log.i("Group", group);

        String query = "SELECT " + MyRefs.TIME + ", " + MyRefs.PREDMET + " FROM " + MyRefs.RASP_TABLE_NAME
                + " WHERE " //+ MyRefs.GROUP + " = '" + group
                + " " + MyRefs.DAY + " = '" + day
                + "' AND " + MyRefs.PREDMET + " LIKE '%" + prepodname
                + "%' GROUP BY "+ MyRefs.TIME ;

        Log.i("SQL Query", query);
        Cursor cursorForIteration = sqdb.rawQuery(query, null);
        while (cursorForIteration.moveToNext()) {
            String time = cursorForIteration.getString(cursorForIteration.getColumnIndex(MyRefs.TIME));
            String name = cursorForIteration.getString(cursorForIteration.getColumnIndex(MyRefs.PREDMET));
            myList.add(time + "\n" + name);
            Log.i("LOG_TAG", "Time:  " + time + " Predmet: " + name);
        }
        cursorForIteration.close();

        sqdb.close();
        sqh.close();
        return  myList;
    }


    private void displayRasp(Context cont,String day,String name,int i) {


        String resName_List = "listViewPrepod" + String.valueOf(i);
        String resName_Text = "textViewPrepod" + String.valueOf(i);
        Log.i("name", resName_List);
        int id_list_view = getResources().getIdentifier(resName_List, "id", "ru.app.raspinf");
        int id_text_view = getResources().getIdentifier(resName_Text, "id", "ru.app.raspinf");
        Log.i("name", Integer.toString(id_list_view));
        List<String> myList = GetDataForListWiev(name, day);


        TextView textView = (TextView) findViewById(id_text_view);

        textView.setText(day);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(cont, android.R.layout.simple_list_item_1, myList);
        ListView listView = (ListView) findViewById(id_list_view);
        listView.setAdapter(dataAdapter);

        listView.setOnTouchListener(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                String substr = selectedItem.substring(0, selectedItem.indexOf("\n"));
                Toast toast = Toast.makeText(getApplicationContext(), substr, Toast.LENGTH_LONG);
                toast.show();
                return true;
            }
        });
    }

    public boolean onTouch(View view, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                Log.i("onTouch","Start");
                break;
            case MotionEvent.ACTION_MOVE:
                float toPosition = event.getX();
                // MOVE_LENGTH - расстояние по оси X, после которого можно переходить на след. экран
                if ((fromPosition - MOVE_LENGTH) > toPosition)
                {
                    Log.i("onTouch","MoveLeft");
                    fromPosition = toPosition;
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_out));
                    flipper.showNext();

                    /*
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putInt(MyRefs.FLIPER_ID, flipper.getDisplayedChild() );
                    Log.i("String settnigs", MyRefs.FLIPER_ID + " || " + Integer.toString(flipper.getDisplayedChild()));
                    editor.apply();
                    */
                    return true;


                }
                else if ((fromPosition + MOVE_LENGTH) < toPosition)
                {
                    Log.i("onTouch","MoveRight");
                    fromPosition = toPosition;
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_out));
                    flipper.showPrevious();

                    /*
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putInt(MyRefs.FLIPER_ID, flipper.getDisplayedChild() );
                    Log.i("String settnigs", MyRefs.FLIPER_ID + " || " + Integer.toString(flipper.getDisplayedChild()));
                    editor.apply();
                    */
                    return true;
                }
            default:
                break;
        }
        return false;
    }

}
