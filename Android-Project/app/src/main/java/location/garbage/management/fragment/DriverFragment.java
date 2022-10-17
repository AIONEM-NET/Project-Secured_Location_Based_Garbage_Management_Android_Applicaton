package location.garbage.management.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import location.garbage.management.R;
import location.garbage.management.activity.DrawerActivity;
import location.garbage.management.adapter.GarbageAdapter;
import location.garbage.management.model.Driver;
import location.garbage.management.model.Garbage;


public class DriverFragment extends Fragment {

    View view;

    Driver driver = new Driver();

    ArrayList<Garbage> listGarbage = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_driver, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        GarbageAdapter garbageAdapter = new GarbageAdapter(getContext(), listGarbage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(garbageAdapter);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Garbage");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot list) {

                listGarbage.clear();

                for(DataSnapshot item : list.getChildren()) {

                    Garbage garbage = item.getValue(Garbage.class);

                    if(garbage != null) {

                        if(TextUtils.isEmpty(driver.district) || TextUtils.isEmpty(garbage.district) || driver.district.contains(garbage.district)) {
                            listGarbage.add(garbage);
                        }

                    }

                }

                garbageAdapter.setListGarbage(listGarbage);
                garbageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference("Drivers").child(DrawerActivity.firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                driver = snapshot.getValue(Driver.class);

                if(driver == null) {
                    driver = new Driver();
                }

                databaseReference.removeEventListener(valueEventListener);

                databaseReference.addValueEventListener(valueEventListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
