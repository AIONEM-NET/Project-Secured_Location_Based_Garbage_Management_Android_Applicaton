package location.garbage.management.activity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import location.garbage.management.R;
import location.garbage.management.adapter.GarbageAdapter;
import location.garbage.management.model.Driver;
import location.garbage.management.model.Garbage;


public class DriverActivity extends Activity {

    public static Driver driver = new Driver();

    ArrayList<Garbage> listGarbage = new ArrayList<>();

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_driver);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();;

        if(firebaseUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }

        TextView txtDriverInfo = (TextView) findViewById(R.id.txtDriverInfo);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        GarbageAdapter garbageAdapter = new GarbageAdapter(this, listGarbage);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(garbageAdapter);


        Query databaseReference = FirebaseDatabase.getInstance().getReference("Garbage").orderByChild("isPicked");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot list) {

                listGarbage.clear();

                for(DataSnapshot item : list.getChildren()) {

                    Garbage garbage = item.getValue(Garbage.class);

                    if(garbage != null) {

                        if(TextUtils.isEmpty(driver.district) || TextUtils.isEmpty(garbage.district) || driver.district.contains(garbage.district)) {
                            listGarbage.add(garbage);

                            if(!garbage.isPicked) {

                                long timeInterval = System.currentTimeMillis() - garbage.time;

                                if(timeInterval < 1*60*1000) {

                                    sendNotification(DriverActivity.this,
                                            (garbage.uid + garbage.phone).hashCode(),
                                            "New "+ garbage.packages +" package"+(!garbage.packages.equals("1") ? "s" : "")+" to pick",
                                            garbage.houseNO + ", " + garbage.district + " - " + garbage.phone + ""
                                    );

                                }

                            }

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

        FirebaseDatabase.getInstance().getReference("Drivers").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                driver = snapshot.getValue(Driver.class);

                if(driver == null) {
                    driver = new Driver();
                }

                driver.uid = snapshot.getKey();

                databaseReference.removeEventListener(valueEventListener);

                if(driver.isApproved) {
                    databaseReference.addValueEventListener(valueEventListener);

                    txtDriverInfo.setText(driver.district);
                    txtDriverInfo.setBackgroundColor(Color.parseColor("#00574B"));

                }else {
                    txtDriverInfo.setText("Not Approved");
                    txtDriverInfo.setBackgroundColor(Color.parseColor("#B10202"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private static final String CHANNEL_ID = "Secured Garbage Management";
    private static final String CHANNEL_NAME = "Secured Garbage Management";
    private static final String CHANNEL_DESC = "Secured Garbage Management";

    public static void sendNotification(Context context, int id, String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(id, builder.build());
    }

}
