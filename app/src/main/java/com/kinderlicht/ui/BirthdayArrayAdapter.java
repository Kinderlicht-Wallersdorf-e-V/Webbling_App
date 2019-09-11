

package com.kinderlicht.ui;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BirthdayArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] names;
    private final String[] birthdays;
    private final int[] ages;

    public BirthdayArrayAdapter(Context context, String[] names, String[] birthdays, int[] ages){
        super(context, -1, names);
        this.context = context;
        this.names = names;
        this.birthdays = birthdays;
        this.ages = ages;

    }

    @Override
    public View getView(int position, View covertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_1, parent, false);
        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secoundLine = (TextView) rowView.findViewById(R.id.secondLine);
        TextView ageLine = (TextView) rowView.findViewById(R.id.ageLine);

        firstLine.setText(names[position]);
        secoundLine.setText(birthdays[position]);
        ageLine.setText("" + ages[position]);

        if(checkDate(ages[position])){
            firstLine.setTextColor(Color.rgb(10, 0, 122));
            rowView.setBackgroundColor(Color.rgb(255, 205, 36));
        }

        return rowView;
    }

    private boolean checkDate(int age){
        int[] special = {18, 19, 21};
        if(age % 10 == 0){
            return true;
        }
        if(age % 11 == 0){
            return true;
        }
        for(int a: special){
            if(age == a){
                return true;
            }
        }
        return false;
    }
}
