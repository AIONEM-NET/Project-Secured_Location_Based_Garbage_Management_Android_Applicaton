package com.example.garbage.activity;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.garbage.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng latLng;
    Marker marker;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    TextView name, phone;
    ImageView profile;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_track);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        name = findViewById(R.id.nameid);
        phone = findViewById(R.id.phone);
        profile = findViewById(R.id.propic);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Location").child(DrawerActivity.WardNo);

    }



    public void getPic(String id){
        storageReference = FirebaseStorage.getInstance().getReference().child("Cleaner dps/"+ id);
        try {
            final File files = File.createTempFile("images", "jpg");
            storageReference.getFile(files)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(files.getAbsolutePath());
                        profile.setImageBitmap(bitmap);

                    }).addOnFailureListener(exception -> {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        databaseReference.addValueEventListener(new ValueEventListener() {
            Double Latitude, Longitude;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (marker != null) {
                    marker.remove();
                }
                if(!dataSnapshot.exists()){
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("Tracking Unavailable")
                            .setMessage("The tracking information is not updated yet. Please try again later.")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else {
                    //Toast.makeText(MapsActivity.this, "Yes YEs ", Toast.LENGTH_SHORT).show();
                        try {
                            name.setText(dataSnapshot.child("Name").getValue().toString());
                            phone.setText(dataSnapshot.child("Mobile").getValue().toString());
                            id = dataSnapshot.child("ID").getValue().toString();
                            getPic(id);

                            if( dataSnapshot.child("Latitude").getValue()!=null) {
                                latLng = new LatLng((Double) dataSnapshot.child("Latitude").getValue(), (Double) dataSnapshot.child("Longitude").getValue());
                                Latitude = Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString());
                                Longitude = Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString());
                                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("My Current Position"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 2000, null);
                            }
                        } catch (SecurityException e) {
                            finish();
                        }

                    // Toast.makeText(getApplicationContext(), Latitude + " " +Longitude, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

}
