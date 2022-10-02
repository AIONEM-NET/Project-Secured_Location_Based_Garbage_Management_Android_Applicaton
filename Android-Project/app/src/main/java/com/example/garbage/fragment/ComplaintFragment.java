package com.example.garbage.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.garbage.R;
import com.example.garbage.activity.DrawerActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ComplaintFragment extends Fragment implements View.OnClickListener {
    DatabaseReference databaseReference;
    private View view;
    EditText feed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_complaint, container, false);
        Button submit = view.findViewById(R.id.submit);
        feed = view.findViewById(R.id.feedback);
        submit.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit:
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Complaint").child(DrawerActivity.houseNO);
                databaseReference.setValue(feed.getText().toString().trim());
                new AlertDialog.Builder(getContext())
                        .setTitle("Complaint Registered")
                        .setMessage("We deeply regret the inconvenience caused. Our team will get back to you soon.")
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().onBackPressed();
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }
    }



}
