package com.upc.applerta.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upc.applerta.R;
import com.upc.applerta.data.model.Alarm;
import com.upc.applerta.data.model.Confirmation;
import com.upc.applerta.ui.main.dummy.DummyContent;

import java.util.ArrayList;

public class ConfirmationFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_confirmation_list, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        inicializarFirebase();
        ArrayList<Confirmation> confirmations = new ArrayList<>();
        Query recentPostsQuery = databaseReference.child("Confirmation").orderByChild("authId").equalTo(user.getUid());
        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                confirmations.clear();
                for(DataSnapshot item:snapshot.getChildren()){
                    Confirmation a = item.getValue(Confirmation.class);
                    confirmations.add(a);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(new ConfirmationRecyclerViewAdapter(view.getContext(), confirmations));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new ConfirmationRecyclerViewAdapter(context, confirmations));
        }
        return view;
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}