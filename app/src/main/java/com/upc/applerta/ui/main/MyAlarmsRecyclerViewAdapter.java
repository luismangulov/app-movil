package com.upc.applerta.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.upc.applerta.MainActivity;
import com.upc.applerta.R;
import com.upc.applerta.data.model.Alarm;

import java.util.List;

public class MyAlarmsRecyclerViewAdapter extends RecyclerView.Adapter<MyAlarmsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<Alarm> mValues;

    public MyAlarmsRecyclerViewAdapter(Context c, List<Alarm> items) {
        context = c;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_alarms, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(holder != null) {
            holder.mIdView.setText(mValues.get(position).getDateTime());
            holder.mContentView.setText(mValues.get(position).getDisplayName());
            holder.mLatView.setText(String.valueOf(mValues.get(position).getLat()));
            holder.mLogView.setText(String.valueOf(mValues.get(position).getLon()));
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mLatView;
        public final TextView mLogView;
        public final Button mButtonView;
        private Context context;

        public ViewHolder(View view, Context c) {
            super(view);
            context = c;
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.myalarmitem_number);
            mContentView = (TextView) view.findViewById(R.id.myalarmcontent);
            mLatView = (TextView) view.findViewById(R.id.myalarmitem_lat);
            mLogView = (TextView) view.findViewById(R.id.myalarmitem_log);
            mButtonView = (Button) view.findViewById(R.id.myalarmitem_button);
            mButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
                    List<Fragment> lista = fm.getFragments();
                    MapsFragment fragment = null;
                    for (Fragment f: lista){
                        if(f instanceof MapsFragment){
                            fragment = (MapsFragment)f;
                        }
                    }
                    if(fragment != null) {
                        fragment.moveCamaraMapReady(Double.parseDouble(mLatView.getText().toString()), Double.parseDouble(mLogView.getText().toString()));

                        TabLayout tabLayout = (TabLayout) view.getRootView().findViewById(R.id.tabs);
                        TabLayout.Tab tab = tabLayout.getTabAt(1);
                        tab.select();
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}