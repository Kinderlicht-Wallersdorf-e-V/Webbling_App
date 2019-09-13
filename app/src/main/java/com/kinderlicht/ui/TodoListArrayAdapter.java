package com.kinderlicht.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kinderlicht.sql.table_objects.ToDoList_Item;

public class TodoListArrayAdapter extends ArrayAdapter {
    private final Context context;
    private final ToDoList_Item[] items;

    public TodoListArrayAdapter(Context context, ToDoList_Item[] items){
        super(context, -1, items);
        this.context = context;
        this.items = items;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_todo, parent, false);
        TextView label = (TextView) rowView.findViewById(R.id.tv_todoLabel);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.cB_todoList);

        label.setText(items[position].getLabe());
        checkBox.setActivated(items[position].isChecked());

        return rowView;
    }
}
