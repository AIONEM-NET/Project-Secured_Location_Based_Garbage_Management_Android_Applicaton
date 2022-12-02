package location.garbage.management.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

import androidx.core.app.ActivityCompat;

import com.flutterwave.raveandroid.RaveUiManager;

import java.util.ArrayList;

import location.garbage.management.R;


public class Payment {

    public static String COMPANY_MOMO_CODE = "0783177672";

    public static final int REQUEST_RESULT_PAY = 203;
    public static final int REQUEST_PERMISSION_CODE_CALL_PHONE = 67;


    public static boolean payCreditCard(Activity activity, String txRef, double amount, String currency, String phoneNumber, String email, String fName, String lName) {

        new RaveUiManager(activity)
                .setAmount(amount)
                .setCurrency(currency)
                .setPhoneNumber(phoneNumber, false)
                .setEmail(email)
                .setfName(fName)
                .setlName(lName)
                .setNarration("")
                .setPublicKey("FLWPUBK-ff3d43a81d0e9a431206d3a12599db99-X")
                .setEncryptionKey("?D(G+KbPeShVkYp3s6v9y$B&E)H@McQf")
                .setTxRef(txRef)
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptMpesaPayments(false)
                .acceptAchPayments(false)
                .acceptGHMobileMoneyPayments(false)
                .acceptUgMobileMoneyPayments(false)
                .acceptZmMobileMoneyPayments(false)
                .acceptRwfMobileMoneyPayments(false)
                .acceptSaBankPayments(false)
                .acceptUkPayments(false)
                .acceptBankTransferPayments(true)
                .acceptUssdPayments(true)
                .acceptBarterPayments(false)
                .acceptFrancMobileMoneyPayments(true, "Rwanda")
                .allowSaveCardFeature(false)
                .onStagingEnv(false)
                .setMeta(new ArrayList<>())
                .withTheme(R.style.PaymentTheme)
                .isPreAuth(true)
                .setSubAccounts(new ArrayList<>())
                .shouldDisplayFee(true)
                .showStagingLabel(true)
                .initialize();

        return true;
    }

    public static boolean payMoMo(Activity activity, String phone, int REQUEST_RESULT_CALL) {
        if(TextUtils.isEmpty(phone)) return false;

        AccessibilityManager accessibilityManager = (AccessibilityManager) activity.getSystemService(Context.ACCESSIBILITY_SERVICE);
        boolean isEnabled = accessibilityManager.isEnabled();

        if(!isEnabled) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            return false;
        }

        if(ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CALL_PHONE, Manifest.permission.RECEIVE_SMS}, REQUEST_PERMISSION_CODE_CALL_PHONE);
            return false;
        }else {

            Intent intentCall = new Intent(Intent.ACTION_CALL);
            intentCall.setData(Uri.parse("tel: "+ Uri.encode(phone)));
            activity.startActivityForResult(intentCall, REQUEST_RESULT_CALL);

            return true;
        }
    }

}