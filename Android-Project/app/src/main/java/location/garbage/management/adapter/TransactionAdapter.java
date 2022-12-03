package location.garbage.management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import location.garbage.management.R;
import location.garbage.management.model.Transaction;
import location.garbage.management.views.TransactionViewHolder;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

    private Context context;
    private ArrayList<Transaction> listTransactions;

    public TransactionAdapter(Context context, ArrayList<Transaction> listTransactions) {
        this.context = context;
        this.listTransactions = listTransactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder viewHolder, int position) {

        Transaction transaction = listTransactions.get(position);

        viewHolder.txtName.setText(transaction.garbage +" : "+ transaction.amount +"Rwf");
        viewHolder.txtDescription.setText(
                ""+
                        "Packages: "+ transaction.packages
                        +"\nOn: "+ transaction.method
                        +"\n"+ new Date(transaction.time).toLocaleString()
        );

        if(transaction.isPayed) {

            viewHolder.btnStatus.setText("Payed");
            viewHolder.btnStatus.setBackgroundResource(R.drawable.oval);

        }else {

            viewHolder.btnStatus.setText("Unpaid");
            viewHolder.btnStatus.setBackgroundResource(R.drawable.oval_yellow);
        }

    }

    @Override
    public int getItemCount() {
        return listTransactions.size();
    }

    public void setListTransactions(ArrayList<Transaction> listTransactions) {
        this.listTransactions = listTransactions;
    }

}