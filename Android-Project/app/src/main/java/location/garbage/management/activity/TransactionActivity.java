package location.garbage.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import location.garbage.management.R;
import location.garbage.management.adapter.TransactionAdapter;
import location.garbage.management.model.Transaction;


public class TransactionActivity extends AppCompatActivity {

    ArrayList<Transaction> listTransactions = new ArrayList<>();

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();;

        if(firebaseUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }

        setSupportActionBar(findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        TransactionAdapter transactionAdapter = new TransactionAdapter(this, listTransactions);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(transactionAdapter);


        Query databaseReference = FirebaseDatabase.getInstance().getReference("Payments").orderByChild("time");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot list) {

                listTransactions.clear();

                for(DataSnapshot item : list.getChildren()) {

                    Transaction transaction = item.getValue(Transaction.class);

                    if(transaction != null) {

                        if(firebaseUser.getUid().equals(transaction.user)) {
                            listTransactions.add(transaction);
                        }

                    }

                }

                Collections.reverse(listTransactions);
                transactionAdapter.setListTransactions(listTransactions);
                transactionAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
