package location.garbage.management.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import location.garbage.management.R;

public class CreditCardPay extends Activity {

    public static void payCreditCard(Activity activity, String txRef, double amount, String currency, String phoneNumber, String email, String fName, String lName) {

        Intent intent = new Intent(activity, CreditCardPay.class);

        activity.startActivityForResult(intent, 198);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_pay);

        EditText edtCardNumber = (EditText) findViewById(R.id.edtCardNumber);
        EditText edtCardExpiration = (EditText) findViewById(R.id.edtCardExpiration);
        EditText edtCardCVV = (EditText) findViewById(R.id.edtCardCVV);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.btnPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cardNumber = edtCardNumber.getText().toString();
                String cardExpiration = edtCardExpiration.getText().toString();
                String cardCVV = edtCardCVV.getText().toString();

                if(TextUtils.isEmpty(cardNumber)){
                    Toast.makeText(getApplicationContext(),"Enter Pin",Toast.LENGTH_LONG).show();
                    edtCardNumber.setError("Card number required");
                    return;
                }
                if(cardNumber.length() != 16){
                    Toast.makeText(getApplicationContext(),"Card Number invalid",Toast.LENGTH_LONG).show();
                    edtCardNumber.setError("Invalid Card invalid");
                    return;
                }
                edtCardNumber.setError(null);


                if(TextUtils.isEmpty(cardExpiration)){
                    Toast.makeText(getApplicationContext(),"Enter Expiration date",Toast.LENGTH_LONG).show();
                    edtCardExpiration.setError("Expiration required");
                    return;
                }
                edtCardExpiration.setError(null);


                if(TextUtils.isEmpty(cardCVV)){
                    Toast.makeText(getApplicationContext(),"Enter CCV code",Toast.LENGTH_LONG).show();
                    edtCardCVV.setError("Expiration CCV");
                    return;
                }
                edtCardCVV.setError(null);

                progressBar.setVisibility(View.VISIBLE);

                FingerprintLock.makeWait(3000, new FingerprintLock.MakeWait() {
                    @Override
                    public void onWaitDone() {

                        closeKeyboard();

                        progressBar.setVisibility(View.GONE);

                        boolean isAccepted = false;

                        if(cardNumber.equals("5531886652142950") && cardExpiration.equals("09/32") && cardCVV.equals("564")){
                            isAccepted = true;
                        }else if(cardNumber.equals("5399838383838381") && cardExpiration.equals("10/31") && cardCVV.equals("470")){
                            isAccepted = true;
                        }else if(cardNumber.equals("4187427415564246") && cardExpiration.equals("09/32") && cardCVV.equals("828")){
                            isAccepted = true;
                        }

                        if(isAccepted) {

                            Toast.makeText(getApplicationContext(), "Payment accepted", Toast.LENGTH_LONG).show();

                            setResult(199);
                            finish();

                        }else {
                            Toast.makeText(getApplicationContext(), "Card rejected", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}