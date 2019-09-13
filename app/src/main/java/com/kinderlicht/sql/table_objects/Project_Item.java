package com.kinderlicht.sql.table_objects;

public class Project_Item {

    private String name;
    private ToDoList_Item[] items;

    public Project_Item(){

    }

    public Project_Item(String name, ToDoList_Item items[]){
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ToDoList_Item[] getItems() {
        return items;
    }

    public void setItems(ToDoList_Item[] items) {
        this.items = items;
    }
}
