package location.garbage.management.activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import location.garbage.management.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    Spinner spinner1, spinner2;
    String textDistrict, textHouse, name, houseNo, district, email, password, pin;
    TextInputLayout edtEmail, edtPassword, edtName, edtPin;
    Button register, btnLogin, file_upload;
    ImageButton select;
    ImageView image;
    TextView welcome;
    ProgressBar progressBar;

    Uri filepath;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    public void generateHouses(int position, String item){
        ArrayList<String> house = new ArrayList<>();
        String abbreviation = getResources().getStringArray(R.array.array_districts_abbreviation)[position];
        for(int i=1; i<=99; i++){
            String id = i < 10 ? "0"+i : ""+i;
            house.add(abbreviation +" "+ id);
        }
        spinner2.setAdapter(new ArrayAdapter<>(SignupActivity.this, android.R.layout.simple_spinner_dropdown_item, house));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        image = findViewById(R.id.propic);
        welcome = findViewById(R.id.welcomeid);
        spinner1 = findViewById(R.id.spinnerAddress);
        spinner2 = findViewById(R.id.houseid);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPin = findViewById(R.id.edtPin);
        file_upload = findViewById(R.id.upload);
        select = findViewById(R.id.profilepic);
        register = findViewById(R.id.doneButton);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progress);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        spinner1.setAdapter(new ArrayAdapter<>(SignupActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_districts)));

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textDistrict = parent.getItemAtPosition(position).toString();
                generateHouses(position, textDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textHouse = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                name = edtName.getEditText().getText().toString().trim();
                houseNo = spinner2.getSelectedItem().toString().trim();
                district = spinner1.getSelectedItem().toString().trim();
                email = edtEmail.getEditText().getText().toString().trim();
                password = edtPassword.getEditText().getText().toString().trim();
                pin = edtPin.getEditText().getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(),"Enter Name",Toast.LENGTH_LONG).show();
                    edtName.setError("Name is required");
                    return;
                }
                edtName.setError(null);

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter Email",Toast.LENGTH_LONG).show();
                    edtEmail.setError("Email required");
                    return;
                }
                if(!LoginActivity.isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                    edtEmail.setError("Email is invalid");
                    return;
                }
                edtEmail.setError(null);

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_LONG).show();
                    edtPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(),"Password must have 6 characters minimum",Toast.LENGTH_LONG).show();
                    edtPassword.setError("Password is too short");
                    return;
                }
                edtPassword.setError(null);

                if(TextUtils.isEmpty(pin)){
                    Toast.makeText(getApplicationContext(),"Enter Pin",Toast.LENGTH_LONG).show();
                    edtPin.setError("Pin is required");
                    return;
                }
                if(pin.length() < 6){
                    Toast.makeText(getApplicationContext(),"Pin must have 6 numbers minimum",Toast.LENGTH_LONG).show();
                    edtPin.setError("Pin is too short");
                    return;
                }
                edtPin.setError(null);

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = task.getResult().getUser();

                            databaseReference.child(firebaseUser.getUid()).child("name").setValue(name);
                            databaseReference.child(firebaseUser.getUid()).child("houseNo").setValue(houseNo);
                            databaseReference.child(firebaseUser.getUid()).child("phone").setValue("");
                            databaseReference.child(firebaseUser.getUid()).child("district").setValue(district);
                            databaseReference.child(firebaseUser.getUid()).child("email").setValue(email);
                            databaseReference.child(firebaseUser.getUid()).child("pin").setValue(pin);
                            databaseReference.child(firebaseUser.getUid()).child("isApproved").setValue(false);

                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    FirebaseAuth.getInstance().signOut();

                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Check your email to verify your account", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();

                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);

                            if(task.getException()  != null) {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Registration Failed, please try again", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(image, "logo_trans");
                pairs[1] = new Pair<View, String>(welcome, "welcome_trans");
                pairs[2] = new Pair<View, String>(edtEmail, "email_trans");
                pairs[3] = new Pair<View, String>(edtPassword, "pw_trans");
                pairs[4] = new Pair<View, String>(btnLogin, "But_trans");
                pairs[5] = new Pair<View, String>(register, "But2_trans");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }
        });

        file_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            filepath = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                select.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if(filepath != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference reference = storageReference.child("Users/" + firebaseAuth.getCurrentUser().getUid());
            reference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+ (int)progress+"%");
                }
            });
        }
    }

}