package com.upc.applerta.ui.main;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<AlarmsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<Alarm> mValues;

    public AlarmsRecyclerViewAdapter(Context c, List<Alarm> items) {
        context = c;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_alarms, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(mValues.get(position).getDateTime());
        holder.mContentView.setText(mValues.get(position).getDisplayName());
        holder.mLatView.setText(String.valueOf(mValues.get(position).getLat()));
        holder.mLogView.setText(String.valueOf(mValues.get(position).getLon()));
        holder.cardAlarm.setTag(mValues.get(position).getAlarmId());
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
        public final CardView cardAlarm;
        public final Button mButtonGoMapView;
        public final Button mButtonConfirmView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mLatView = (TextView) view.findViewById(R.id.item_lat);
            mLogView = (TextView) view.findViewById(R.id.item_log);
            mButtonGoMapView = (Button) view.findViewById(R.id.btnGoMap);
            mButtonConfirmView = (Button) view.findViewById(R.id.btnConfirm);
            cardAlarm = (CardView)view.findViewById(R.id.cardAlarm);
            mButtonGoMapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
                    List<Fragment> lista = fm.getFragments();
                    MapsFragment fragment = null;
                    for (Fragment f: lista){
                        if(f instanceof MapsFragment){
                            fragment = (MapsFragment)f; break;
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

            mButtonConfirmView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
                    List<Fragment> lista = fm.getFragments();
                    MapsFragment fragment = null;
                    for (Fragment f: lista){
                        if(f instanceof MapsFragment){
                            fragment = (MapsFragment)f; break;
                        }
                    }
                    if(fragment != null) {
                        int t = fragment.confirmAlarm(cardAlarm.getTag().toString(), mContentView.getText().toString(), Double.parseDouble(mLatView.getText().toString()), Double.parseDouble(mLogView.getText().toString()));
                        if(t == 0){
                            TabLayout tabLayout = (TabLayout) view.getRootView().findViewById(R.id.tabs);
                            TabLayout.Tab tab = tabLayout.getTabAt(0);
                            tab.select();
                            TabLayout subtabLayout = (TabLayout) view.getRootView().findViewById(R.id.tabsProfile);
                            TabLayout.Tab subtab = subtabLayout.getTabAt(1);
                            subtab.select();
                        }
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