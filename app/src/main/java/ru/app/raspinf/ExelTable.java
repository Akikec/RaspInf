package ru.app.raspinf;

import java.util.ArrayList;

public class ExelTable {

    /*
    Временое хранилище
    */

    ArrayList<String> cellList;

    public  String getCellString(int id) {
        return cellList.get(id);
    }
    public ArrayList<String> getCellList () { return cellList; }
    public void setCellList(ArrayList<String> mCellList) { this.cellList = mCellList; }



}

