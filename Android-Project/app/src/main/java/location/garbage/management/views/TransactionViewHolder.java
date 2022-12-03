package location.garbage.management.views;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import location.garbage.management.R;


public class TransactionViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName;
    public TextView txtDescription;
    public Button btnStatus;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
        btnStatus = (Button) itemView.findViewById(R.id.btnStatus);
    }

}