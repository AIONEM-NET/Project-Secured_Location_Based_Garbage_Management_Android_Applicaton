package location.garbage.management.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import location.garbage.management.R;


public class FingerprintLock extends AppCompatActivity {

    public static boolean isVerified = false;

    public static boolean startAction(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!FingerprintHandler.isFingerPrintSupported(context)) return false;
        }else {
            return false;
        }

        startActivityIntent(context, FingerprintLock.class);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        setFullscreenApp(getActivity());

        isVerified = false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ImageView imgFingerPrint = (ImageView) findViewById(R.id.ALnCore_ScreenLock_FingerPrint_ImgFingerPrint);
            TextView txtInfo = (TextView) findViewById(R.id.ALnCore_ScreenLock_FingerPrint_TxtInfo);
            CheckBox checkBoxOnOff = (CheckBox) findViewById(R.id.ALnCore_ScreenLock_FingerPrint_CheckBoxOnOff);


            new FingerprintHandler(getContext(), new FingerprintHandler.AuthenticationListener() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    isVerified = false;

                    imgFingerPrint.setImageResource(R.drawable.filter_1);
                    txtInfo.setText(R.string.fingerprint_info_error);
                    txtInfo.setTextColor(getResources().getColor(R.color.AlnRed));

                    makeWait(2000, new MakeWait() {
                        @Override
                        public void onWaitDone() {

                            imgFingerPrint.setImageResource(R.drawable.icon_core_white_fingerprint);
                            txtInfo.setText(R.string.fingerprint_info_default);
                            txtInfo.setTextColor(getResources().getColor(R.color.AlnGrey));

                        }
                    });
                }

                @Override
                public void onAuthenticationFailed() {
                    isVerified = false;

                    imgFingerPrint.setImageResource(R.drawable.icon_core_white_fingerprint_error);
                    txtInfo.setText(R.string.fingerprint_info_failed);
                    txtInfo.setTextColor(getResources().getColor(R.color.AlnRed));

                    makeWait(2000, new MakeWait() {
                        @Override
                        public void onWaitDone() {

                            imgFingerPrint.setImageResource(R.drawable.icon_core_white_fingerprint);
                            txtInfo.setText(R.string.fingerprint_info_default);
                            txtInfo.setTextColor(getResources().getColor(R.color.AlnGrey));

                        }
                    });
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

                    txtInfo.setText(helpString);
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    isVerified = true;

                    imgFingerPrint.setImageResource(R.drawable.icon_core_white_fingerprint_success);
                    txtInfo.setText(R.string.fingerprint_info_success);
                    txtInfo.setTextColor(getResources().getColor(R.color.AlnGreen));

                    makeWait(3000, new MakeWait() {
                        @Override
                        public void onWaitDone() {

                            finish();

                        }
                    });
                }
            });


            checkBoxOnOff.setChecked(true);

        }else {

            finish();
        }

    }

    public FingerprintLock getActivity() {
        return this;
    }
    public Context getContext() {
        return this;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(!isVerified) {
            // killApp(getActivity());
        }
    }



    public static void startActivityIntent(Context context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
    }


    public static interface MakeWait {
        public void onWaitDone();
    }
    public static CountDownTimer makeWait(int time, MakeWait makeWait) {
        return new CountDownTimer((time % 2 == 0 ? time : time-1)/2, (time % 2 == 0 ? time : time+1)/2) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                if(makeWait != null) makeWait.onWaitDone();
            }
        }.start();
    }


    public static void setFullscreenApp(Activity activity) {
        setFullscreen(activity);
    }
    public static void setFullscreen(Activity activity) {
        try {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void killApp(Activity activity) {
        try {
            activity.finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        killApp();
    }

    public static void killApp() {
        try {
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public static class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        public FingerprintHandler(Context context) {
            this(context, null);
        }

        public FingerprintHandler(final Context context, AuthenticationListener authenticationListener) {
            setAuthenticationListener(authenticationListener);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);

                if(fingerprintManager != null && keyguardManager != null) {

                    if (!fingerprintManager.isHardwareDetected()) {

                        Toast.makeText(context, R.string.fingerprint_initialization_failed_hardware, Toast.LENGTH_LONG).show();

                        isVerified = true;

                    } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(context, R.string.fingerprint_initialization_failed_permission, Toast.LENGTH_LONG).show();

                    } else if (!keyguardManager.isKeyguardSecure()) {

                        Toast.makeText(context, R.string.fingerprint_initialization_failed_service, Toast.LENGTH_LONG).show();

                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {

                        Toast.makeText(context, R.string.fingerprint_initialization_failed_enrol, Toast.LENGTH_LONG).show();

                    } else {

                        generateKey();

                        if(cipherInit()) {

                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

                            startAuth(fingerprintManager, cryptoObject);

                            // Toast.makeText(context, R.string.fingerprint_initialization_success, Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(context, R.string.fingerprint_initialization_failed, Toast.LENGTH_LONG).show();
                        }
                    }

                }else {
                    Toast.makeText(context, R.string.fingerprint_initialization_failed_setup, Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(context, R.string.fingerprint_initialization_failed_unsupported, Toast.LENGTH_LONG).show();
            }

        }

        public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

            android.os.CancellationSignal cancellationSignal = new android.os.CancellationSignal();

            fingerprintManager.authenticate(cryptoObject,cancellationSignal,0,this,null);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);

            if(authenticationListener != null) authenticationListener.onAuthenticationError(errorCode, errString);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();

            if(authenticationListener != null) authenticationListener.onAuthenticationFailed();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);

            if(authenticationListener != null) authenticationListener.onAuthenticationHelp(helpCode, helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);

            if(authenticationListener != null) authenticationListener.onAuthenticationSucceeded(result);
        }

        private AuthenticationListener authenticationListener;
        public void setAuthenticationListener(AuthenticationListener authenticationListener) {
            this.authenticationListener = authenticationListener;
        }

        public interface AuthenticationListener {

            void onAuthenticationError(int errorCode, CharSequence errString);

            void onAuthenticationFailed();

            void onAuthenticationHelp(int helpCode, CharSequence helpString);

            void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result);

        }


        private KeyStore keyStore;
        private Cipher cipher;
        private String KEY_NAME="androidkeyname";


        @TargetApi(Build.VERSION_CODES.M)
        private void generateKey() {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                keyStore.load(null);

                keyGenerator.init(new
                        KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
                keyGenerator.generateKey();
            }catch(KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
                e.printStackTrace();
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        private boolean cipherInit() {
            try {
                cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException("Failed to get Cipher", e);
            }

            try {
                keyStore.load(null);

                SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);

                cipher.init(Cipher.ENCRYPT_MODE, key);

                return true;
            } catch (KeyPermanentlyInvalidatedException e) {
                e.printStackTrace();
                return false;
            } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException("Failed to init Cipher", e);
            }
        }

        public static boolean isFingerPrintSupported(Context context) {
            return isFingerPrintSupported(context, true);
        }
        public static boolean isFingerPrintSupported(Context context, boolean showMessage) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);

                if(fingerprintManager != null && keyguardManager != null) {

                    if (!fingerprintManager.isHardwareDetected()) {
                        if(showMessage) Toast.makeText(context, R.string.fingerprint_initialization_failed_hardware, Toast.LENGTH_LONG).show();
                        return false;

                    }else if (ContextCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                        if(showMessage) Toast.makeText(context, R.string.fingerprint_initialization_failed_permission, Toast.LENGTH_LONG).show();
                        return false;

                    }else if (!keyguardManager.isKeyguardSecure()) {
                        if(showMessage) Toast.makeText(context, R.string.fingerprint_initialization_failed_service, Toast.LENGTH_LONG).show();
                        return false;

                    }else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        if(showMessage) Toast.makeText(context, R.string.fingerprint_initialization_failed_enrol, Toast.LENGTH_LONG).show();
                        return false;
                    }

                }else {
                    if(showMessage) Toast.makeText(context, R.string.fingerprint_initialization_failed, Toast.LENGTH_LONG).show();
                    return false;
                }

            }else {
                if(showMessage) Toast.makeText(context, R.string.fingerprint_initialization_failed_unsupported, Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }

    }
}