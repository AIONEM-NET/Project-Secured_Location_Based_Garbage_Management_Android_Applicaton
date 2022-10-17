package location.garbage.management.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import location.garbage.management.R;
import location.garbage.management.storage.UserSharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    ImageView image;
    TextView welcome;
    String phone, Password;
    Button user_login, register_user;
    TextInputLayout edtPhone, edtPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        image = findViewById(R.id.propic);
        welcome = findViewById(R.id.welcomeid);
        edtPhone = findViewById(R.id.nameid);
        edtPassword = findViewById(R.id.edtPassword);
        user_login = findViewById(R.id.loginButton);
        register_user = findViewById(R.id.doneButton);
        progressBar = findViewById(R.id.progress);

        if(getPackageName().equals("location.garbage.management.driver")) {
            ((TextView) findViewById(R.id.welcomeid)).setText("Driver Login");
            findViewById(R.id.doneButton).setVisibility(View.GONE);

            edtPhone.setHint("Email");
            edtPhone.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        user_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = edtPhone.getEditText().getText().toString().trim();
                Password = edtPassword.getEditText().getText().toString().trim();

                if(getPackageName().equals("location.garbage.management.driver")) {

                    if(TextUtils.isEmpty(phone)){
                        Toast.makeText(LoginActivity.this,"Enter Email address",Toast.LENGTH_LONG).show();
                        edtPhone.setError("Email is required");
                        return;
                    }
                    edtPhone.setError(null);

                }else {

                    if(TextUtils.isEmpty(phone)){
                        Toast.makeText(LoginActivity.this,"Enter Phone number",Toast.LENGTH_LONG).show();
                        edtPhone.setError("Phone is required");
                        return;
                    }
                    if((phone.length() != 10 && (!phone.startsWith("078") && !phone.startsWith("079") && !phone.startsWith("072") && !phone.startsWith("073")))) {
                        Toast.makeText(getApplicationContext(), "Phone number is invalid", Toast.LENGTH_SHORT).show();
                        edtPhone.setError("Phone number is invalid");
                        return;
                    }
                    edtPhone.setError(null);
                }

                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
                    return;
                }

                String email;
                if(getPackageName().equals("location.garbage.management.driver")) {
                    email = phone;
                }else {
                    email = phone + "@tel.phone";
                }

                user_login.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = task.getResult().getUser();

                            UserSharedPreferences share = new UserSharedPreferences(LoginActivity.this);
                            share.setFilename(phone);

                            if(getPackageName().equals("location.garbage.management.driver")) {

                                if(true || "Driver".equalsIgnoreCase(firebaseUser.getDisplayName())) {

                                    startActivity(new Intent(getApplicationContext(), DriverActivity.class));

                                }else {

                                    Toast.makeText(LoginActivity.this, "You don't have driver's Access !!", Toast.LENGTH_LONG).show();
                                }

                            }else {
                                startActivity(new Intent(getApplicationContext(), DrawerActivity.class));

                            }

                            finish();

                        } else {
                            user_login.setEnabled(true);

                            if(task.getException()  != null) {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(LoginActivity.this, "Login Failed, please try again", Toast.LENGTH_LONG).show();
                            }

                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });


        register_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(image, "logo_trans");
                pairs[1] = new Pair<View, String>(welcome, "welcome_trans");
                pairs[2] = new Pair<View, String>(edtPhone, "email_trans");
                pairs[3] = new Pair<View, String>(edtPassword, "pw_trans");
                pairs[4] = new Pair<View, String>(user_login, "But_trans");
                pairs[5] = new Pair<View, String>(register_user, "But2_trans");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }
        });
    }
}
