package com.example.akikec.raspinf;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {
// rrraaasd
    //ttt
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

}
