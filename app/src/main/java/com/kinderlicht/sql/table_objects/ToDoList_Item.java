package com.kinderlicht.sql.table_objects;

public class ToDoList_Item {

    private int id;
    private boolean checked;
    private String label;


    public ToDoList_Item(){

    }


    //There are parameters missing like assigned to
    public ToDoList_Item(int id, boolean cheched, String lablel){
        this.id = id;
        this.checked = cheched;
        this.label = lablel;
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

    public void setChecked(){
        this.checked = !this.checked;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
