package com.upc.applerta.ui.main;

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
import com.upc.applerta.data.model.Confirmation;
import com.upc.applerta.ui.main.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ConfirmationRecyclerViewAdapter extends RecyclerView.Adapter<ConfirmationRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<Confirmation> mValues;

    public ConfirmationRecyclerViewAdapter(Context c, List<Confirmation> items) {
        context = c;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_confirmation, parent, false);
        return new ConfirmationRecyclerViewAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(holder != null) {
            holder.mIdView.setText(mValues.get(position).getDateTime());
            holder.mContentView.setText("Confirmación de alerta de " + mValues.get(position).getDisplayName());
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
            mIdView = (TextView) view.findViewById(R.id.confirmationitem_number);
            mContentView = (TextView) view.findViewById(R.id.confirmationcontent);
            mLatView = (TextView) view.findViewById(R.id.confirmationitem_lat);
            mLogView = (TextView) view.findViewById(R.id.confirmationitem_log);
            mButtonView = (Button) view.findViewById(R.id.confirmationitem_button);
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