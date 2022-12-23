package location.garbage.management.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import location.garbage.management.R;


public class GarbageSelectAdapter extends ArrayAdapter<String> {

    HashMap<Integer, View> views = new HashMap<>();

    public GarbageSelectAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        View view = inflater.inflate(R.layout.spinner, null, true);

        TextView textView = (TextView) view.findViewById(R.id.textView);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        textView.setText((position + 1) +"."+ getItem(position));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(onGarbageClickListener != null) {
                    onGarbageClickListener.onGarbageClick(position, view, getItem(position));
                }

            }
        });

        views.put(position, view);

        return view;
    }

    public View getView(int position) {
        return views.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    OnGarbageClickListener onGarbageClickListener;
    public void setOnGarbageListener(OnGarbageClickListener onGarbageClickListener) {
        this.onGarbageClickListener = onGarbageClickListener;
    }

    public static interface OnGarbageClickListener {

        public void onGarbageClick(int position, View view, String item);

    }

}