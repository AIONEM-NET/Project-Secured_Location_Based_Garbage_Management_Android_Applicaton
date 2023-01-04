package location.garbage.management.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import androidx.appcompat.widget.AppCompatSpinner;
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
import location.garbage.management.adapter.GarbageSelectAdapter;
import location.garbage.management.model.User;
import location.garbage.management.services.PaymentReceiver;
import location.garbage.management.services.PaymentResult;
import location.garbage.management.storage.UserSharedPreferences;

import com.bumptech.glide.Glide;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
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
    AppCompatSpinner spinnerGarbage;
    Button btnSubmit;
    ProgressBar progressBar;
    CheckBox checkBoxMoMoMTN;
    CheckBox checkBoxMoMoAirTel;
    CheckBox checkBoxCard;

    GarbageSelectAdapter garbageSelectAdapter;

    public static User user = new User();
    public static FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();;

        FingerprintLock.isVerified = false;

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
        spinnerGarbage = (AppCompatSpinner) findViewById(R.id.spinnerGarbage);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        checkBoxMoMoMTN = (CheckBox) findViewById(R.id.checkBoxMoMoMTN);
        checkBoxMoMoAirTel = (CheckBox) findViewById(R.id.checkBoxMoMoAirTel);
        checkBoxCard = (CheckBox) findViewById(R.id.checkBoxVisa);

        // garbageSelectAdapter = new GarbageSelectAdapter(this, R.layout.spinner, R.id.textView, listTrashes);
        garbageSelectAdapter = new GarbageSelectAdapter(this, android.R.layout.simple_spinner_dropdown_item, listTrashes);

        spinnerGarbage.setAdapter(garbageSelectAdapter);

        spinnerGarbage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String garbage = listTrashes.get(position);
                double price = listTrashesPrice.get(position);
                String packages = edtPackages.getText().toString();

                double packagesNo = !TextUtils.isEmpty(packages) ? Double.parseDouble(packages) : 0;
                selectedPrice = price;
                selectedGarbage = garbage;

                int amount = (int) (packagesNo * selectedPrice);

                edtAmount.setText(amount + " Rwf");

                edtGarbage.setText(garbage +" ( "+ price +" Rwf / Pac )");
                edtGarbage.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        garbageSelectAdapter.setOnGarbageListener(new GarbageSelectAdapter.OnGarbageClickListener() {
            @Override
            public void onGarbageClick(int position, View view, String item) {

                Toast.makeText(DrawerActivity.this, item, Toast.LENGTH_SHORT).show();

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

                if(checkBoxMoMoMTN.isChecked()) {
                    selectedPayment = "MTN Mobile Money";
                }else if(checkBoxMoMoAirTel.isChecked()) {
                    selectedPayment = "AirTel Money";
                }else if(checkBoxCard.isChecked()) {
                    selectedPayment = "Credit Card";
                }

                if(!findViewById(R.id.lLayoutPayment).isShown()) {
                    btnSubmit.setText("VERIFY CREDENTIALS");
                    findViewById(R.id.lLayoutPayment).setVisibility(View.VISIBLE);
                    return;
                }

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
                int amountNo = (int) (packagesNo * selectedPrice);

                mapDataGarbage.put("user", firebaseUser.getUid());
                mapDataGarbage.put("garbage", selectedGarbage);
                mapDataGarbage.put("packages", packages);
                mapDataGarbage.put("price", selectedPrice);
                mapDataGarbage.put("amount", amountNo);
                mapDataGarbage.put("phone", phone);
                mapDataGarbage.put("name", myName);
                mapDataGarbage.put("district", myDistrict);
                mapDataGarbage.put("houseNO", myHouseNO);
                mapDataGarbage.put("time", System.currentTimeMillis());

                mapDataPayment.put("user", firebaseUser.getUid());
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

                if(checkBoxMoMoAirTel.isChecked()) {
                    phonePay = "*182*1*2*" + Payment.COMPANY_MOMO_CODE + "*" + amountNo + "#";
                }else {
                    phonePay = "*182*1*1*" + Payment.COMPANY_MOMO_CODE + "*" + amountNo + "#";
                }

                paying = 0;

                boolean isPay = false;

                if(checkBoxCard.isChecked()) {

                    Payment.payCreditCard(DrawerActivity.this, "123", amountNo, "RWF", phone, myEmail, myName, myDistrict);

                }else {

                    isPay = Payment.payMoMo(DrawerActivity.this, phonePay, Payment.REQUEST_RESULT_PAY);

                }

                if(isPay) {
                    progressBar.setVisibility(View.VISIBLE);
                }

            }
        });

        checkBoxMoMoMTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked) {
                    selectedPayment = "MTN Mobile Money";
                    checkBoxMoMoAirTel.setChecked(false);
                    checkBoxCard.setChecked(false);
                }else {
                    selectedPayment = "";
                }

            }
        });

        checkBoxMoMoAirTel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked) {
                    selectedPayment = "AirTel Money";
                    checkBoxMoMoMTN.setChecked(false);
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
                    checkBoxMoMoMTN.setChecked(false);
                    checkBoxMoMoAirTel.setChecked(false);
                }else {
                    selectedPayment = "";
                }

            }
        });

        getUserData();


        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(new PaymentReceiver(), filter);

        paymentReceiver = new location.garbage.management.services.PaymentReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(paymentReceiver, intentFilter);

        startService(new Intent(getApplicationContext(), PaymentResult.class));

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                String ussd = arg1.getExtras().getString("ussd");

                String message = (""+ ussd).toLowerCase();

                if(message.contains("washyizeho:")) return;

                boolean isPayed =
                        message.contains("wohereje ") ||
                                message.contains("usigaranye ") ||
                                message.contains("murakoze gukoresha mtn mobile money")
                        ;

                if(isPayed) {

                    completePayment();

                    paying = 0;

                }else {

                    progressBar.setVisibility(View.GONE);

                    sendNotification(getApplicationContext(), mapDataPayment.hashCode(), "Mobile Money Payment", ussd);

                }

            }
        };

        registerReceiver(receiver, new IntentFilter("com.times.ussd.action.REFRESH"));

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(User.class);

                if(user == null) {
                    user = new User();
                }

                Glide.with(DrawerActivity.this)
                        .load(DrawerActivity.user.photo)
                        .placeholder(R.drawable.user_icon)
                        .into(propic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        closeKeyboard();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);

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

    public static void sendNotification(Context context, int id, String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
            context.getSharedPreferences("Notifications", MODE_PRIVATE).edit().putInt("notified", 1).apply();
        }

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        notificationManager.notify(id, builder.build());
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

                    if(TextUtils.isEmpty(myDistrict) || TextUtils.isEmpty(district) || district.contains(myDistrict) || myDistrict.contains(district) || district.equalsIgnoreCase("All")) {
                        listTrashes.add(name);
                        listTrashesPrice.add(priceNo);
                    }

                }

                garbageSelectAdapter.notifyDataSetChanged();

                // System.out.println("SpinnerView: "+ view.);

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
    protected void onResume() {
        super.onResume();

        if(FingerprintLock.isVerified && btnSubmit != null) {
            btnSubmit.setText("PAY NOW");
        }

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
        if(id == R.id.nav_transactions) {
            Intent intent = new Intent(DrawerActivity.this, TransactionActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_logout) {
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

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    String phonePay = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Payment.REQUEST_PERMISSION_CODE_CALL_PHONE) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Payment.payMoMo(this, phonePay, Payment.REQUEST_RESULT_PAY);
            }

        }else {

            IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(new PaymentReceiver(), filter);

        }

    }


    location.garbage.management.services.PaymentReceiver paymentReceiver;

    int paying = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {

            String message = data.getStringExtra("response");

            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                System.out.println( "PAYMENT SUCCESS " + message);

                completePayment();

            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                System.out.println( "PAYMENT ERROR " + message);

            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                System.out.println( "PAYMENT CANCELLED " + message);
            }

            progressBar.setVisibility(View.GONE);

        }else if(requestCode == Payment.REQUEST_RESULT_PAY) {
            paying++;

        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    HashMap<String, Object> mapDataGarbage = new HashMap<>();
    HashMap<String, Object> mapDataPayment = new HashMap<>();

    BroadcastReceiver receiver;

    public void completePayment() {

        if(paying == 0) return;

        DatabaseReference databaseReferenceGarbage = FirebaseDatabase.getInstance().getReference("Garbage").push();
        mapDataGarbage.put("uid", databaseReferenceGarbage.getKey());
        mapDataPayment.put("isPaid", true);

        databaseReferenceGarbage.setValue(mapDataGarbage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                DatabaseReference databaseReferencePayment = FirebaseDatabase.getInstance().getReference("Payments").push();
                mapDataPayment.put("uid", databaseReferencePayment.getKey());
                databaseReferencePayment.setValue(mapDataPayment);

                edtPackages.setText("");
                checkBoxMoMoMTN.setChecked(false);
                checkBoxCard.setChecked(false);

                progressBar.setVisibility(View.GONE);

                sendNotification(getApplicationContext(), mapDataPayment.hashCode(), "Garbage Payment: "+ mapDataPayment.get("amount") +" Rwf", "You Garbage information is submitted");

                Toast.makeText(getApplicationContext(), "Data submitted successfully", Toast.LENGTH_SHORT).show();

            }
        });

    }


}