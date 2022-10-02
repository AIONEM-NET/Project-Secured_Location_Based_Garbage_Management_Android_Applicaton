package com.example.garbage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.garbage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackFragment extends Fragment implements View.OnClickListener {
    DatabaseReference databaseReference;
    private View view;
    EditText feed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_feedback, container, false);
        Button submit = view.findViewById(R.id.submit);
        feed = view.findViewById(R.id.feedback);
        submit.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit:
                String user  = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Feedbacks").child(user);
                databaseReference.setValue(feed.getText().toString().trim());
                Toast.makeText(getContext(), "Submitted", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                break;
        }
    }



}
