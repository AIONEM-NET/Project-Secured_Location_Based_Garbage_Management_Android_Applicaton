package location.garbage.management.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import location.garbage.management.R;
import location.garbage.management.storage.UserSharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    public static boolean isApproved = false;
    public static String myPhone, myHouseNO, myDistrict, myEmail, myName;
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


    double selectedPrice = 0;
    String selectedGarbage = "";
    String selectedPayment = "";
    List<String> listTrashes = new ArrayList<>();;
    List<Double> listTrashesPrice = new ArrayList<>();
    EditText edtGarbage;
    EditText edtPackages;
    EditText edtPhone;
    EditText edtAmount;
    Spinner spinnerGarbage;
    Button btnSubmit;
    ProgressBar progressBar;
    CheckBox checkBoxMoMo;
    CheckBox checkBoxCard;


    public static FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();;

        if(firebaseUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }

        share = new UserSharedPreferences(DrawerActivity.this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
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

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_driver, R.id.nav_complaint)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

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


        edtGarbage = (EditText) findViewById(R.id.edtGarbage);
        edtPackages = (EditText) findViewById(R.id.edtPackages);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtAmount = (EditText) findViewById(R.id.edtAmount);
        spinnerGarbage = (Spinner) findViewById(R.id.spinnerGarbage);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        checkBoxMoMo = (CheckBox) findViewById(R.id.checkBoxMoMo);
        checkBoxCard = (CheckBox) findViewById(R.id.checkBoxVisa);

        spinnerGarbage.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listTrashes));

        spinnerGarbage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String garbage = listTrashes.get(position);
                double price = listTrashesPrice.get(position);
                String packages = edtPackages.getText().toString();

                double packagesNo = !TextUtils.isEmpty(packages) ? Double.parseDouble(packages) : 0;
                selectedPrice = price;
                selectedGarbage = garbage;

                double amount = packagesNo * selectedPrice;

                edtAmount.setText(amount + " Rwf");

                edtGarbage.setText(garbage +" ( "+ price +" Rwf / Pac )");
                edtGarbage.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtPackages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String packages = edtPackages.getText().toString();

                double packagesNo = !TextUtils.isEmpty(packages) ? Double.parseDouble(packages) : 0;
                double amount = packagesNo * selectedPrice;

                edtAmount.setText(amount + " Rwf");

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String garbage = edtGarbage.getText().toString();
                String packages = edtPackages.getText().toString();
                String phone = edtPhone.getText().toString();
                String amount = edtAmount.getText().toString();

                if(TextUtils.isEmpty(garbage)) {
                    Toast.makeText(getApplicationContext(), "Garbage is required", Toast.LENGTH_SHORT).show();
                    edtGarbage.setError("Garbage is required");
                    return;
                }
                edtGarbage.setError(null);

                if(TextUtils.isEmpty(packages)) {
                    Toast.makeText(getApplicationContext(), "Packages is required", Toast.LENGTH_SHORT).show();
                    edtPackages.setError("Packages is required");
                    return;
                }
                edtPackages.setError(null);

                if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "Phone is required", Toast.LENGTH_SHORT).show();
                    edtPhone.setError("Phone is required");
                    return;
                }
                if((phone.length() != 10 && (!phone.startsWith("078") && !phone.startsWith("079") && !phone.startsWith("072") && !phone.startsWith("073")))) {
                    Toast.makeText(getApplicationContext(), "Phone number is invalid", Toast.LENGTH_SHORT).show();
                    edtPhone.setError("Phone number is invalid");
                    return;
                }
                edtPhone.setError(null);

                if(TextUtils.isEmpty(selectedPayment)) {
                    Toast.makeText(getApplicationContext(), "Payment method is required", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!FingerprintLock.isVerified) {
                    startActivity(new Intent(getApplicationContext(), FingerprintLock.class));
                    return;
                }



                if(!isApproved) {
                    Toast.makeText(DrawerActivity.this, "You are not yet Approved by the administrator", Toast.LENGTH_SHORT).show();
                    return;
                }


                double packagesNo = !TextUtils.isEmpty(packages) ? Double.parseDouble(packages) : 0;
                double amountNo = packagesNo * selectedPrice;

                progressBar.setVisibility(View.VISIBLE);

                HashMap<String, Object> mapDataGarbage = new HashMap<>();
                mapDataGarbage.put("garbage", selectedGarbage);
                mapDataGarbage.put("packages", packages);
                mapDataGarbage.put("price", selectedPrice);
                mapDataGarbage.put("amount", amountNo);
                mapDataGarbage.put("phone", phone);
                mapDataGarbage.put("name", myName);
                mapDataGarbage.put("district", myDistrict);
                mapDataGarbage.put("houseNO", myHouseNO);
                mapDataGarbage.put("time", System.currentTimeMillis());

                DatabaseReference databaseReferenceGarbage = FirebaseDatabase.getInstance().getReference("Garbage").push();
                mapDataGarbage.put("uid", databaseReferenceGarbage.getKey());
                databaseReferenceGarbage.setValue(mapDataGarbage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        HashMap<String, Object> mapDataPayment = new HashMap<>();
                        mapDataPayment.put("garbage", selectedGarbage);
                        mapDataPayment.put("packages", packages);
                        mapDataPayment.put("price", selectedPrice);
                        mapDataPayment.put("amount", amountNo);
                        mapDataPayment.put("method", selectedPayment);
                        mapDataPayment.put("phone", phone);
                        mapDataPayment.put("name", myName);
                        mapDataPayment.put("district", myDistrict);
                        mapDataPayment.put("houseNO", myHouseNO);
                        mapDataPayment.put("time", System.currentTimeMillis());

                        DatabaseReference databaseReferencePayment = FirebaseDatabase.getInstance().getReference("Payments").push();
                        mapDataPayment.put("uid", databaseReferencePayment.getKey());
                        databaseReferencePayment.setValue(mapDataPayment);

                        edtPackages.setText("");
                        checkBoxMoMo.setChecked(false);
                        checkBoxCard.setChecked(false);

                        progressBar.setVisibility(View.GONE);

                        sendNotification("Garbage Payment: "+ amountNo +" Rwf", "You Garbage information is submitted");

                        Toast.makeText(getApplicationContext(), "Data submitted successfully", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        checkBoxMoMo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked) {
                    selectedPayment = "Mobile Money";
                    checkBoxCard.setChecked(false);
                }else {
                    selectedPayment = "";
                }

            }
        });

        checkBoxCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked) {
                    selectedPayment = "Credit Card";
                    checkBoxMoMo.setChecked(false);
                }else {
                    selectedPayment = "";
                }

            }
        });


        getUserData();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void sendNotification(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Collection").child(formattedDate).child(myHouseNO);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Notification = dataSnapshot.getKey().trim();
                    if (Notification.equals(myHouseNO)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                            notificationChannel.setDescription(CHANNEL_DESC);
                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(notificationChannel);
                            getSharedPreferences("Notifications", MODE_PRIVATE).edit().putInt("notified", 1).apply();
                        }

                        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        notificationManager = NotificationManagerCompat.from(DrawerActivity.this);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(DrawerActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Garbage Collected")
                                .setContentText("Garbage is picked up from your house "+ myHouseNO)
                                .setSound(soundUri)
                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                        notificationManager.notify(0, builder.build());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNotification(String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
            getSharedPreferences("Notifications", MODE_PRIVATE).edit().putInt("notified", 1).apply();
        }

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notificationManager = NotificationManagerCompat.from(DrawerActivity.this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DrawerActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(0, builder.build());
    }

    public void getUserData(){

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myPhone = String.valueOf(dataSnapshot.child("phone").getValue());
                myName = String.valueOf(dataSnapshot.child("name").getValue());
                myDistrict = String.valueOf(dataSnapshot.child("district").getValue());
                myEmail = String.valueOf(dataSnapshot.child("email").getValue());
                myHouseNO = String.valueOf(dataSnapshot.child("houseNo").getValue());
                isApproved = Boolean.TRUE.equals(dataSnapshot.child("isApproved").getValue());

                getTrashData();

                if(TextUtils.isEmpty(edtPhone.getText().toString())) {
                    edtPhone.setText(myPhone);
                }

                if(isApproved) {
                    btnSubmit.setBackgroundResource(R.drawable.oval);
                }else {
                    btnSubmit.setBackgroundResource(R.drawable.oval_red);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getTrashData() {

        FirebaseDatabase.getInstance().getReference("Trashes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot list) {

                listTrashes.clear();
                listTrashesPrice.clear();

                for(DataSnapshot item : list.getChildren()) {

                    String name = String.valueOf(item.child("name").getValue());
                    String district = String.valueOf(item.child("district").getValue());
                    String type = String.valueOf(item.child("type").getValue());
                    String price = String.valueOf(item.child("price").getValue());
                    double priceNo = !TextUtils.isEmpty(price) ? Double.parseDouble(price) : 0;


                    if(TextUtils.isEmpty(myDistrict) || TextUtils.isEmpty(district) || district.contains(myDistrict) || district.equalsIgnoreCase("All")) {
                        listTrashes.add(name);
                        listTrashesPrice.add(priceNo);
                    }

                }

                spinnerGarbage.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listTrashes));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void getData(){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myPhone = String.valueOf(dataSnapshot.child("phone").getValue());
                myName = String.valueOf(dataSnapshot.child("name").getValue());
                myDistrict = String.valueOf(dataSnapshot.child("district").getValue());
                myEmail = String.valueOf(dataSnapshot.child("email").getValue());
                myHouseNO = String.valueOf(dataSnapshot.child("houseNo").getValue());

                name.setText(myName);

                if (note == 0) {
                    sendNotification();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("Users/"+ currentUser);
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
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_scan) {
            startActivity(new Intent(DrawerActivity.this, MapsActivity.class));
            return false;
        }else if (itemId == R.id.action_profile) {
            startActivity(new Intent(DrawerActivity.this, ProfileActivity.class));
            return false;
        }else if (itemId == R.id.action_driver) {
            startActivity(new Intent(DrawerActivity.this, DriverActivity.class));
            return false;
        }else if (itemId == R.id.action_logout) {
            logout();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_logout) {
            logout();
        }
        return false;
    }

    public void logout() {
        share.removeUser();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DrawerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}