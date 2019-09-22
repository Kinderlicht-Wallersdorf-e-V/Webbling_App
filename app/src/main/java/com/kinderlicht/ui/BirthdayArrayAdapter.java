

package com.kinderlicht.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BirthdayArrayAdapter extends RecyclerView.Adapter<BirthdayArrayAdapter.BirthdayItemViewHolder> {

    public static class BirthdayItemViewHolder extends RecyclerView.ViewHolder{
        public TextView lV_Name;
        public TextView lV_Birthdate;
        public TextView lV_Age;
        public View itemView;

        public BirthdayItemViewHolder(final View itemView){
            super(itemView);
            this.lV_Name = (TextView) itemView.findViewById(R.id.tV_BirthdayListItem_Name);
            this.lV_Birthdate = (TextView) itemView.findViewById(R.id.tV_BirthdayListItem_Birthdate);
            this.lV_Age = (TextView) itemView.findViewById(R.id.tV_BirthdayListItem_Age);
            this.itemView = itemView;
        }

        public void init(String name, String birthday, int age, int theme, Context context){
            this.lV_Name.setText(name);
            this.lV_Birthdate.setText(birthday);
            this.lV_Age.setText("" + age);

            if(checkDate(age)){
                switch(theme){
                    case 0:
                        //firstLine.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText));
                        //rowView.setBackgroundColor(Color.rgb(255, 205, 36));
                        itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight));
                        break;
                    case 1:
                        lV_Name.setTextColor(ContextCompat.getColor(context, R.color.colorAccentLight));
                        break;
                }
            }
        }

        private boolean checkDate(int age) {
            int[] special = {18, 19, 21};
            if (age % 10 == 0) {
                return true;
            }
            if (age % 11 == 0) {
                return true;
            }
            for (int a : special) {
                if (age == a) {
                    return true;
                }
            }
            return false;
        }
    }

    private final Context context;
    private final String[] names;
    private final String[] birthdays;
    private final int[] ages;
    private int theme;

    public BirthdayArrayAdapter(Context context, String[] names, String[] birthdays, int[] ages) {
        this.context = context;
        this.names = names;
        this.birthdays = birthdays;
        this.ages = ages;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.settings_file_name), context.MODE_PRIVATE);
        theme = sharedPreferences.getInt(context.getString(R.string.settings_key_theme), 0);
    }

    @NonNull
    @Override
    public BirthdayItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        BirthdayItemViewHolder viewHolder = new BirthdayItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_1, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BirthdayItemViewHolder viewHolder, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.settings_file_name), context.MODE_PRIVATE);
        theme = sharedPreferences.getInt(context.getString(R.string.settings_key_theme), 0);
        viewHolder.init(names[position], birthdays[position], ages[position], theme, context);

    }

    @Override
    public int getItemCount() {
        return names.length;
    }




}
