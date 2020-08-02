package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.R;

public class FilterSpinnerAdapter extends ArrayAdapter<FilterSpinnerItem> {

    public ImageView imageView;
    public TextView textView;

    public FilterSpinnerAdapter(Context context, ArrayList<FilterSpinnerItem> filterSpinnerItems) {
        super(context, 0, filterSpinnerItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertiew, ViewGroup parent) {
        if (convertiew == null) {
            convertiew = LayoutInflater.from(getContext()).inflate(R.layout.filter_spinner, parent, false);
        }

        imageView = convertiew.findViewById(R.id.icon);
        textView = convertiew.findViewById(R.id.type);

        FilterSpinnerItem filterSpinnerItem = getItem(position);

        if (filterSpinnerItem != null) imageView.setImageResource(filterSpinnerItem.getImage());
        textView.setText(filterSpinnerItem.getType());

        return convertiew;
    }
}
