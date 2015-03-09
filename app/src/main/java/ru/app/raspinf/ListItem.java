package ru.app.raspinf;

import java.lang.*;

public class ListItem implements Comparable<ListItem> {

    Float time_1;
    Float time_2;
    String text;
    String group;

    ListItem(String t1,String t2,String s,String _group){
        this.time_1 = Float.valueOf(t1);
        this.time_2 = Float.valueOf(t2);
        this.text = s;
        this.group = _group;
    }

    float GetTime_1(){
        return time_1;
    }
//    float GetTime_2(){
//        return time_2;
//    }
//    String GetText(){
//        return text;
//    }




    @Override
    public int compareTo(ListItem o) {
        return time_1.compareTo(o.GetTime_1());
    }


    @Override
    public String toString() {
        //return time_1 + "-" + time_2 + "\n" + text;
        return String.format("%.2f-%.2f \n %s \n Группа: %s", time_1,time_2,text,group);
    }
}
