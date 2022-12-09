package location.garbage.management.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

import location.garbage.management.R;
import location.garbage.management.activity.DrawerActivity;
import location.garbage.management.activity.DriverActivity;
import location.garbage.management.model.Garbage;
import location.garbage.management.views.GarbageViewHolder;


public class GarbageAdapter extends RecyclerView.Adapter<GarbageViewHolder> {

    private Context context;
    private ArrayList<Garbage> listGarbage;

    public GarbageAdapter(Context context, ArrayList<Garbage> listGarbage) {
        this.context = context;
        this.listGarbage = listGarbage;
    }

    @NonNull
    @Override
    public GarbageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GarbageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_garbage, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GarbageViewHolder viewHolder, int position) {

        Garbage garbage = listGarbage.get(position);

        viewHolder.txtName.setText(garbage.name);
        viewHolder.txtDescription.setText(
                ""+
                        "Location: "+ garbage.houseNO +", "+ garbage.district
                        +"\nPackages: "+ garbage.packages
                        +"\nPhone: "+ garbage.phone
                        +"\n"+ new Date(garbage.time).toLocaleString()
        );

        if(garbage.isPicked) {

            viewHolder.btnStatus.setText("Picked");
            viewHolder.btnStatus.setBackgroundResource(R.drawable.oval);

            viewHolder.btnMap.setVisibility(View.GONE);

        }else {

            viewHolder.btnStatus.setText("Confirm ?");
            viewHolder.btnStatus.setBackgroundResource(R.drawable.oval_yellow);

            viewHolder.btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseDatabase.getInstance().getReference("Garbage").child(garbage.uid).child("isPicked").setValue(true);
                    FirebaseDatabase.getInstance().getReference("Garbage").child(garbage.uid).child("driver").setValue(DriverActivity.driver.uid);
                    FirebaseDatabase.getInstance().getReference("Garbage").child(garbage.uid).child("driverName").setValue(DriverActivity.driver.name);
                    FirebaseDatabase.getInstance().getReference("Garbage").child(garbage.uid).child("pickedDate").setValue(System.currentTimeMillis());

                    DrawerActivity.sendNotification(context,
                            (garbage.uid + garbage.phone).hashCode(),
                            "Picked garbage of "+ garbage.packages +" package"+(!garbage.packages.equals("1") ? "s" : "")+" ",
                            garbage.houseNO + ", " + garbage.district + " - " + garbage.phone + ""
                    );

                }
            });

            viewHolder.btnMap.setVisibility(View.VISIBLE);

            viewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+ garbage.houseNO);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);

                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return listGarbage.size();
    }

    public void setListGarbage(ArrayList<Garbage> listGarbage) {
        this.listGarbage = listGarbage;
    }

}