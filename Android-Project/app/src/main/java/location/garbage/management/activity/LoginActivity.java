package location.garbage.management.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
    String email, Password;
    Button btnLogin, register_user;
    TextInputLayout edtEmail, edtPassword;
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
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        register_user = findViewById(R.id.doneButton);
        progressBar = findViewById(R.id.progress);

        if(getPackageName().equals("location.garbage.management.driver")) {
            ((TextView) findViewById(R.id.welcomeid)).setText("Driver APP");
            findViewById(R.id.doneButton).setVisibility(View.GONE);
        }else {
            ((TextView) findViewById(R.id.welcomeid)).setText("USER APP");
            findViewById(R.id.doneButton).setVisibility(View.VISIBLE);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard();

                email = edtEmail.getEditText().getText().toString().trim();
                Password = edtPassword.getEditText().getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this,"Enter Email address",Toast.LENGTH_LONG).show();
                    edtEmail.getEditText().setError("Email is required");
                    return;
                }
                edtEmail.setError(null);

                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
                    edtPassword.getEditText().setError("Password is required");
                    return;
                }
                edtPassword.getEditText().setError(null);

                btnLogin.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        btnLogin.setEnabled(true);

                        FirebaseUser firebaseUser = task.getResult().getUser();

                        if (task.isSuccessful() && firebaseUser != null) {

                            if(!firebaseUser.isEmailVerified()) {
                                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        FirebaseAuth.getInstance().signOut();

                                        progressBar.setVisibility(View.GONE);

                                        Toast.makeText(getApplicationContext(), "Email is not verified", Toast.LENGTH_LONG).show();
                                        Toast.makeText(getApplicationContext(), "Check your email to verify your account first", Toast.LENGTH_LONG).show();
                                        edtEmail.getEditText().setError("Email is not verified");

                                    }
                                });
                                return;
                            }

                            UserSharedPreferences share = new UserSharedPreferences(LoginActivity.this);
                            share.setFilename(email);

                            if(getPackageName().equals("location.garbage.management.driver")) {

                                progressBar.setVisibility(View.GONE);

                                if("Driver".equalsIgnoreCase(firebaseUser.getDisplayName())) {

                                    Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(getApplicationContext(), DriverActivity.class));
                                    finish();

                                }else {

                                    FirebaseAuth.getInstance().signOut();

                                    Toast.makeText(getApplicationContext(), "You don't have Driver's Access !!", Toast.LENGTH_LONG).show();
                                    edtEmail.getEditText().setError("Not a Driver's email");

                                }

                            }else {
                                Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_LONG).show();

                                startActivity(new Intent(getApplicationContext(), DrawerActivity.class));
                                finish();
                            }

                        } else {

                            progressBar.setVisibility(View.GONE);

                            btnLogin.setEnabled(true);

                            if(task.getException()  != null) {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Login Failed, please try again", Toast.LENGTH_LONG).show();
                            }

                        }

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
                pairs[2] = new Pair<View, String>(edtEmail, "email_trans");
                pairs[3] = new Pair<View, String>(edtPassword, "pw_trans");
                pairs[4] = new Pair<View, String>(btnLogin, "But_trans");
                pairs[5] = new Pair<View, String>(register_user, "But2_trans");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }
        });

        findViewById(R.id.buttonForgetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = edtEmail.getEditText().getText().toString().trim();

                String email = edtEmail.getEditText().getText().toString();

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    edtEmail.getEditText().setError("Email is required");
                    return;
                }
                if(!isValidEmail(email)) {
                    Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    edtEmail.getEditText().setError("Email is invalid");
                    return;
                }
                edtEmail.getEditText().setError(null);

                progressBar.setVisibility(View.VISIBLE);

                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(getApplicationContext(), "We have sent you a link to reset your password", Toast.LENGTH_LONG).show();

                    }
                });

            }
        });

        closeKeyboard();

    }

    public static boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email) && email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+"));
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
