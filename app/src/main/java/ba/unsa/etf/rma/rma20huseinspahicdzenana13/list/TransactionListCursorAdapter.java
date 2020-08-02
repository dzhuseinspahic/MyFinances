package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.R;

public class TransactionListCursorAdapter extends ResourceCursorAdapter {
    public TextView titleView;
    public TextView amountView;
    public ImageView imageView;

    public TransactionListCursorAdapter(Context context, int layout, Cursor c, boolean autoRequery) {
        super(context, layout, c, autoRequery);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        titleView = view.findViewById(R.id.title);
        amountView = view.findViewById(R.id.amount);
        imageView = view.findViewById(R.id.icon);

        titleView.setText(cursor.getString(cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TITLE)));
        amountView.setText(cursor.getString(cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.AMOUNT)));

        String transactionType = cursor.getString(cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TYPE)).toLowerCase().replace(" ", "");
        try{
            Class res = R.drawable.class;
            Field field = res.getField(transactionType);
            int drawableId = field.getInt(null);
            imageView.setImageResource(drawableId);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.picture);
        }
    }
}
