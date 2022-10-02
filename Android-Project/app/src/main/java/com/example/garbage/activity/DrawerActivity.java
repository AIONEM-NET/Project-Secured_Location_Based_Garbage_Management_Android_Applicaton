package com.example.garbage.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;
import com.example.garbage.R;
import com.example.garbage.storage.UserSharedPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    UserSharedPreferences share;
    public static DatabaseReference databaseReference, databaseReference1;
    public static StorageReference storageReference;
    DrawerLayout drawer;
    ImageView propic;
    TextView name, wait;
    View bblanks;
    static File localFile;
    public static String Email, houseNO, WardNo, pw, fullName;
    String Notification;
    private NotificationManagerCompat notificationManager;
    private static final String CHANNEL_ID = "INC";
    private static final String CHANNEL_NAME = "InfiniteLoop";
    private static final String CHANNEL_DESC = "Notified";
    LottieAnimationView animationView, connection;
    static int flag;
    int note;
    Animation fade;
    private FirebaseAuth firebaseAuth;

    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    String formattedDate = df.format(c);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        firebaseAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        share = new UserSharedPreferences(DrawerActivity.this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share.removeUser();
                firebaseAuth.signOut();
                Intent intent = new Intent(DrawerActivity.this, LoginActivity.class);

                Snackbar.make(view, "Nothing here", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(intent);
                finish();
            }
        });

        animationView = findViewById(R.id.load);
        wait = findViewById(R.id.wait);
        bblanks = findViewById(R.id.blank);
        connection = findViewById(R.id.connection);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork == null) {
            connection.setVisibility(View.VISIBLE);
            wait.setVisibility(View.INVISIBLE);
            bblanks.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.INVISIBLE);
        }
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);

        drawer = findViewById(R.id.drawer_layout);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_complaint, R.id.nav_closure,
                 R.id.nav_feedback)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        name = navigationView.getHeaderView(0).findViewById(R.id.naaav);
        propic = navigationView.getHeaderView(0).findViewById(R.id.pro_button);
        propic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                drawer.closeDrawers();
            }
        });

        if(flag == 0) {
            bblanks.setVisibility(View.VISIBLE);
            wait.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.VISIBLE);
            animationView.playAnimation();
            note = getSharedPreferences("Notifications", MODE_PRIVATE).getInt("notified", 0);
            getData();
        }

    }

    public void sendNoti(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Collection").child(formattedDate).child(houseNO);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Notification = dataSnapshot.getKey().trim();
                    if (Notification.equals(houseNO)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                            notificationChannel.setDescription(CHANNEL_DESC);
                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(notificationChannel);
                            getSharedPreferences("Notifications", MODE_PRIVATE).edit().putInt("notified", 1).apply();
                        }

                        notificationManager = NotificationManagerCompat.from(DrawerActivity.this);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(DrawerActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Garbage Collected").setContentText("Garbage is picked up from your house "+houseNO).setPriority(NotificationCompat.PRIORITY_HIGH);

                        notificationManager.notify(0, builder.build());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getData(){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Email = dataSnapshot.child("mobile").getValue().toString();
                Email = Email.substring(0,10);
                fullName = dataSnapshot.child("name").getValue().toString();
                name.setText(fullName);
                houseNO = dataSnapshot.child("houseNo").getValue().toString();

                if (note == 0)
                    sendNoti();
                WardNo = dataSnapshot.child("wardNo").getValue().toString();
                pw = dataSnapshot.child("password").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        storageReference = FirebaseStorage.getInstance().getReference().child("User dps/"+ currentUser);
        try {
            localFile = File.createTempFile("images", "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            animationView.setVisibility(View.INVISIBLE);
                            bblanks.setVisibility(View.INVISIBLE);
                            bblanks.setAnimation(fade);
                            wait.setVisibility(View.INVISIBLE);
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            propic.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    animationView.setVisibility(View.INVISIBLE);
                    bblanks.setVisibility(View.INVISIBLE);
                    bblanks.setAnimation(fade);
                    wait.setVisibility(View.INVISIBLE);
                    //Toast.makeText(drawer.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        flag = 1;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_scan:
                startActivity(new Intent(DrawerActivity.this, MapsActivity.class));
                return false;
            case R.id.action_share:
                shareIt();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "\ne-TRASH:  Simplify garbage Collection\n\nGoogle drive link:\nhttps://drive.google.com/open?id=1G6nzSCyE6Mb1jPLfFgB4iWnwqFP4xmvd\n\nOne drive link:\nhttps://1drv.ms/u/s!Ap1arhakXq-OaoxAkWUsLRaTi2M?e=gxh9zm";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
