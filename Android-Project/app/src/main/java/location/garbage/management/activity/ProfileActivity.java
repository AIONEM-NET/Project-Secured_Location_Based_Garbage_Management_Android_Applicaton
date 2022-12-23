package location.garbage.management.activity;

import static location.garbage.management.activity.DrawerActivity.databaseReference1;
import static location.garbage.management.activity.DrawerActivity.firebaseUser;
import static location.garbage.management.activity.DrawerActivity.storageReference;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import location.garbage.management.R;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    EditText edtName, edtPhone, edtHouseNo, edtDistrict, edtPin;
    ImageButton edit, choose;
    ImageView profile;
    Button done, upload;
    Uri filepath;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edit = findViewById(R.id.edit);
        done = findViewById(R.id.doneButton);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.phone);
        edtHouseNo = findViewById(R.id.houseid);
        edtDistrict = findViewById(R.id.spinnerAddress);
        edtPin = findViewById(R.id.edtPin);
        profile = findViewById(R.id.propic);
        upload = findViewById(R.id.upload);
        choose = findViewById(R.id.choose);
        progressBar = findViewById(R.id.progress);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                edtDistrict.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.grey));
                edtHouseNo.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.grey));

                edtName.setEnabled(true);
                edtDistrict.setEnabled(true);
                edtHouseNo.setEnabled(true);
                edtPhone.setEnabled(true);
                edtPin.setEnabled(true);

                done.setVisibility(View.VISIBLE);
                edit.setVisibility(View.INVISIBLE);
                choose.setVisibility(View.VISIBLE);
                upload.setVisibility(View.VISIBLE);

                DrawerActivity.flag = 0;
            }
        });

        edtPhone.setText(DrawerActivity.user.phone);
        edtName.setText(DrawerActivity.myName);
        edtHouseNo.setText(DrawerActivity.myHouseNO);
        edtDistrict.setText(DrawerActivity.myDistrict);
        edtPin.setText(DrawerActivity.user.pin);

        Bitmap bitmap = BitmapFactory.decodeFile(DrawerActivity.localFile.getAbsolutePath());
        if(bitmap != null) {
            profile.setImageBitmap(bitmap);
        }

        Glide.with(this)
                .load(DrawerActivity.user.photo)
                .placeholder(R.drawable.user_icon)
                .into(profile);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = edtPhone.getText().toString().trim();
                String name = edtName.getText().toString().trim();
                String houseNo = edtHouseNo.getText().toString().trim();
                String district = edtDistrict.getText().toString().trim();
                String pin = edtPin.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(ProfileActivity.this,"Enter Name",Toast.LENGTH_LONG).show();
                    edtName.setError("Name is required");
                    return;
                }
                edtName.setError(null);

                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(ProfileActivity.this,"Enter Phone number",Toast.LENGTH_LONG).show();
                    edtPhone.setError("Phone is required");
                    return;
                }
                if((phone.length() != 10 && (!phone.startsWith("078") && !phone.startsWith("079") && !phone.startsWith("072") && !phone.startsWith("073")))) {
                    Toast.makeText(getApplicationContext(), "Phone number is invalid", Toast.LENGTH_SHORT).show();
                    edtPhone.setError("Phone number is invalid");
                    return;
                }
                edtPhone.setError(null);

                if(TextUtils.isEmpty(district)){
                    Toast.makeText(ProfileActivity.this,"Enter District",Toast.LENGTH_LONG).show();
                    edtDistrict.setError("District is required");
                    return;
                }
                edtDistrict.setError(null);

                if(TextUtils.isEmpty(houseNo)){
                    Toast.makeText(ProfileActivity.this,"Enter House No.",Toast.LENGTH_LONG).show();
                    edtHouseNo.setError("House No. is required");
                    return;
                }
                edtHouseNo.setError(null);

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

                databaseReference1.child("email").setValue(firebaseUser.getEmail());
                databaseReference1.child("name").setValue(edtName.getText().toString().trim());
                databaseReference1.child("houseNo").setValue(edtHouseNo.getText().toString().trim());
                databaseReference1.child("phone").setValue(edtPhone.getText().toString().trim());
                databaseReference1.child("district").setValue(edtDistrict.getText().toString().trim());
                databaseReference1.child("pin").setValue(edtPin.getText().toString().trim());

                progressBar.setVisibility(View.GONE);

                Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), DrawerActivity.class));
                finish();
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
                profile.setImageBitmap(bitmap);
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

            storageReference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+ (int) progress + "%");
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        databaseReference1.child("photo").setValue(downloadUri.toString());
                    }
                }
            });

        }
    }
}