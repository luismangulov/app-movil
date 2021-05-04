package com.upc.applerta.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.upc.applerta.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;

    public static Fragment newInstance(int index) {

        Fragment fragment = null;
        switch (index){
            case 1: fragment = new ProfileFragment(); break;
            case 2: fragment = new MapsFragment(); break;
            case 3: fragment = new AlarmsFragment(); break;
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        ViewPager viewPager = root.findViewById(R.id.subview_pager);
        setupViewPager(viewPager);
        
        TabLayout tabs = root.findViewById(R.id.tabsProfile);
        tabs.setupWithViewPager(viewPager);

        final Button btnCloseSession = root.findViewById(R.id.btnCloseSession);
        btnCloseSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                System.exit(0);
            }
        });

        final TextView lblDisplayName = root.findViewById(R.id.lblDisplayName);
        final TextView lblCity = root.findViewById(R.id.lblCity);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        lblDisplayName.setText(user.getDisplayName());
        lblCity.setText(user.getEmail());

        return root;
    }

    private void setupViewPager(ViewPager viewPager) {
        SubSectionsPagerAdapter adapter = new SubSectionsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MyAlarmsFragment(), "Mis alarmas");
        adapter.addFragment(new ConfirmationFragment(), "Mis confirmaciones");
        viewPager.setAdapter(adapter);
    }
}
