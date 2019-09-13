package com.kinderlicht.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.kinderlicht.sql.table_objects.Project_Item;
import com.kinderlicht.sql.table_objects.ToDoList_Item;

public class ProjectArrayAdapter extends ArrayAdapter {

    private final Context context;
    private final Project_Item[] projects;
    private final boolean[] collapsed;

    public ProjectArrayAdapter(Context context, Project_Item[] projects){
        super(context, -1, projects);
        this.context = context;
        this.projects = projects;
        this.collapsed = new boolean[projects.length];

        for(boolean a: this.collapsed){
            a = false;
        }

    }

    @Override
    public View getView(final int position, View covertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_project, parent, false);

        TextView tv_projectListLable = (TextView) rowView.findViewById(R.id.tv_projectListLable);
        ImageButton b_collapse = (ImageButton) rowView.findViewById(R.id.b_collapse);
        ListView lv_todoListProject = (ListView) rowView.findViewById(R.id.lv_todoListProject);

        tv_projectListLable.setText(projects[position].getName());

        TodoListArrayAdapter items_adapter = new TodoListArrayAdapter(this.context, projects[position].getItems());
        lv_todoListProject.setAdapter(items_adapter);


        return rowView;
    }



}
