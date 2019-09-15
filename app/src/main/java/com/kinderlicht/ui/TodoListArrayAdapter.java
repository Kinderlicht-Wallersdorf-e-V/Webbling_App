package com.kinderlicht.ui;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kinderlicht.sql.table_objects.ToDoList_Item;

public class TodoListArrayAdapter extends RecyclerView.Adapter<TodoListArrayAdapter.ToDoItemViewHolder> {

    public static class ToDoItemViewHolder extends RecyclerView.ViewHolder{
        public CheckBox cB_todoList;
        public TextView tV_todoLabel;

        private ToDoList_Item item;

        public ToDoItemViewHolder(final View itemView){
            super(itemView);
            this.cB_todoList = (CheckBox) itemView.findViewById(R.id.cB_todoList);
            this.tV_todoLabel = (TextView) itemView.findViewById(R.id.tv_todoLabel);




            this.cB_todoList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonChanged();
                }
            });
        }

        public void init(ToDoList_Item item){
            this.item = item;
            tV_todoLabel.setText(item.getLabel());
            cB_todoList.setChecked(item.isChecked());

            updateItem();
        }

        private void buttonChanged(){
            item.setChecked();
            updateItem();
        }

        private void updateItem(){
            if(item.isChecked()){
                tV_todoLabel.setPaintFlags(tV_todoLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else{
                tV_todoLabel.setPaintFlags(tV_todoLabel.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }
    private final Context context;
    private final ToDoList_Item[] items;

    public TodoListArrayAdapter(Context context, ToDoList_Item[] items){
        //super(context, -1, items);
        this.context = context;
        this.items = items;

    }

    @NonNull
    @Override
    public ToDoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ToDoItemViewHolder viewHolder = new ToDoItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_todo, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoItemViewHolder toDoItemViewHolder, int position) {
        toDoItemViewHolder.init(items[position]);


    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
