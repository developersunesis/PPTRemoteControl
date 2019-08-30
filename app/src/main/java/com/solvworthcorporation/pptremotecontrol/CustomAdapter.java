package com.solvworthcorporation.pptremotecontrol;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class CustomAdapter extends ArrayAdapter {

    private Context context;
    private String[] names;

    public CustomAdapter(@NonNull Context context, String[] names){
        super(context, 0, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
