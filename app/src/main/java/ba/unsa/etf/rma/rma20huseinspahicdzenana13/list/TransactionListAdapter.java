package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.R;

public class TransactionListAdapter extends ArrayAdapter<Transaction> {
    private int resource;
    public TextView titleView;
    public TextView amountView;
    public ImageView imageView;

    public TransactionListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Transaction> items) {
        super(context, resource, items);
        this.resource = resource;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.clear();
        this.addAll(transactions);
    }

    public Transaction getTransaction(int position) {
        return this.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout newView;
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            newView = (LinearLayout)convertView;
        }

        Transaction transaction = getItem(position);

        titleView = newView.findViewById(R.id.title);
        amountView = newView.findViewById(R.id.amount);
        imageView = newView.findViewById(R.id.icon);

        titleView.setText(transaction.getTitle());
        amountView.setText(String.valueOf(transaction.getAmount()));

        String transactionType = String.valueOf(transaction.getType()).toLowerCase().replace(" ", "");
        try{
            Class res = R.drawable.class;
            Field field = res.getField(transactionType);
            int drawableId = field.getInt(null);
            imageView.setImageResource(drawableId);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.picture);
        }

        return newView;
    }
}
