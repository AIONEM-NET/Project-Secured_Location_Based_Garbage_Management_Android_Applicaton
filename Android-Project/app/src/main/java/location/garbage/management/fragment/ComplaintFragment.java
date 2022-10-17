package location.garbage.management.fragment;

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

import location.garbage.management.R;
import location.garbage.management.activity.DrawerActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ComplaintFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_complaint, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText feed = view.findViewById(R.id.feedback);
        Button submit = view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Complaint").child(DrawerActivity.myHouseNO);
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

            }
        });

    }

}
