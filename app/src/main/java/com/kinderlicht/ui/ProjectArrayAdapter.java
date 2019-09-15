package com.kinderlicht.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.kinderlicht.sql.table_objects.Project_Item;
import com.kinderlicht.sql.table_objects.ToDoList_Item;

public class ProjectArrayAdapter extends RecyclerView.Adapter<ProjectArrayAdapter.ProjectItemViewHolder> {

    public static class ProjectItemViewHolder extends RecyclerView.ViewHolder{

        public RecyclerView lV_items;
        public ImageButton b_collapse;
        public TextView tV_Lable;
        public RecyclerView.LayoutManager layoutManager;
        private View divider;

        private Project_Item project;

        private boolean collapsed;

        public ProjectItemViewHolder(final View itemView, boolean collapsed) {
            super(itemView);
            this.lV_items = (RecyclerView) itemView.findViewById(R.id.lv_todoListProject);
            this.b_collapse = (ImageButton) itemView.findViewById(R.id.b_collapse);
            this.tV_Lable = (TextView) itemView.findViewById(R.id.tv_projectListLable);
            this.divider = (View) itemView.findViewById(R.id.di_todoItems);
            this.collapsed = collapsed;

            lV_items.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(itemView.getContext());
            lV_items.setLayoutManager(layoutManager);


            b_collapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonPressed();
                }
            });


        }

        public void init(Project_Item project, Context context){
            this.project = project;

            this.tV_Lable.setText(project.getName());
            TodoListArrayAdapter items_adapter = new TodoListArrayAdapter(context, project.getItems());

            this.lV_items.setAdapter(items_adapter);

            updateCollapsed();
        }

        private void buttonPressed(){
            this.collapsed = !this.collapsed;
            updateCollapsed();
        }

        private void updateCollapsed(){
            if(collapsed){
                lV_items.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                b_collapse.setImageResource(R.drawable.ic_expand_more_primcol_24dp);
            } else{
                lV_items.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                b_collapse.setImageResource(R.drawable.ic_expand_less_primcol_24dp);
            }
        }

        public void setCollapsed(boolean collapsed){
            this.collapsed = collapsed;
        }

        public boolean isCollapsed(){
            return this.collapsed;
        }
    }

    private final Context context;
    private final Project_Item[] projects;
    private final boolean[] collapsed;

    public ProjectArrayAdapter(Context context, Project_Item[] projects){
        //super(context, -1, projects);
        this.context = context;
        this.projects = projects;
        this.collapsed = new boolean[projects.length];

        for(boolean a: this.collapsed){
            a = false;
        }

    }

    @Deprecated
    public View getView(final int position, View covertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_project, parent, false);

        TextView tv_projectListLable = (TextView) rowView.findViewById(R.id.tv_projectListLable);
        ImageButton b_collapse = (ImageButton) rowView.findViewById(R.id.b_collapse);
        RecyclerView lv_todoListProject = (RecyclerView) rowView.findViewById(R.id.lv_todoListProject);

        tv_projectListLable.setText(projects[position].getName());

        TodoListArrayAdapter items_adapter = new TodoListArrayAdapter(this.context, projects[position].getItems());
        lv_todoListProject.setAdapter(items_adapter);


        return rowView;
    }

    @NonNull
    @Override
    public ProjectItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProjectItemViewHolder viewHolder =  new ProjectItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project, parent, false), true);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectItemViewHolder projectItemViewHolder, int position) {
        projectItemViewHolder.init(projects[position], this.context);
    }

    @Override
    public int getItemCount() {
        return projects.length;
    }
}
