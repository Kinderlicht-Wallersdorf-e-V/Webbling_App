package com.kinderlicht.sql.table_objects;

public class ToDoList_Item {

    private int id;
    private boolean checked;
    private String labe;


    public ToDoList_Item(){

    }


    //There are parameters missing like assigned to
    public ToDoList_Item(int id, boolean cheched, String lable){
        this.id = id;
        this.checked = cheched;
        this.labe = lable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getLabe() {
        return labe;
    }

    public void setLabe(String labe) {
        this.labe = labe;
    }
}
