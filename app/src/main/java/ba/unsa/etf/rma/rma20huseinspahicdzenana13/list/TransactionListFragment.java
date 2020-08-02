package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.R;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;

public class TransactionListFragment extends Fragment implements ITransactionListView, GestureDetector.OnGestureListener {
    private Button rightButton;
    private Button leftButton;
    private Button addTransactionButton;
    private ListView listView;
    private Spinner filterSpinner;
    private Spinner sortSpinner;
    private TextView dateTextView;
    private TextView globalTextView;
    private TextView limitTextView;

    private TransactionListAdapter transactionListAdapter;
    private TransactionListCursorAdapter cursorAdapter;
    private FilterSpinnerAdapter filterSpinnerAdapter;
    private ArrayAdapter<String> sortSpinnerAdapter;

    private ArrayList<FilterSpinnerItem> filterSpinnerList;
    private ArrayList<String> sortSpinnerList;

    private int positionOfLastItem;

    private ITransactionListPresenter transactionListPresenter;

    private GestureDetector gestureDetector;

    public ITransactionListPresenter getPresenter() {
        if (transactionListPresenter == null) {
            transactionListPresenter = new TransactionListPresenter(this, getActivity());
        }
        return transactionListPresenter;
    }

    private OnClick onItemClick;
    private OnClick onAddButtonClick;
    private OnClick onSwipe;
    public interface OnClick{
        void onItemClicked(Transaction transaction);
        void onAddButtonClicked(String add);
        void swipe(String swipe);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);

        new TypeInteractor().execute();

        onItemClick = (OnClick) getActivity();
        onAddButtonClick = (OnClick) getActivity();
        onSwipe = (OnClick) getActivity();

        gestureDetector = new GestureDetector(this);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        positionOfLastItem = -1;

        if (checkInternetConnection(getActivity()) == true) getPresenter().saveOnServer();

        dateTextView = fragmentView.findViewById(R.id.dateTextView);
        if (getPresenter().getDate() != null) {
            String dat = getPresenter().getDate().format(DateTimeFormatter.ofPattern("MMM, yyyy"));
            dateTextView.setText(dat);
        } else {
            String date = new SimpleDateFormat("MMM, yyyy", Locale.getDefault()).format(new Date());
            dateTextView.setText(date);
        }

        listView = fragmentView.findViewById(R.id.listView);
        if(checkInternetConnection(getActivity())) {
            transactionListAdapter = new TransactionListAdapter(getActivity(), R.layout.list_element, new ArrayList<Transaction>());
            listView.setAdapter(transactionListAdapter);
        } else {
            cursorAdapter = new TransactionListCursorAdapter(getActivity(), R.layout.list_element, null, false);
            listView.setAdapter(cursorAdapter);
        }
        listView.setOnItemClickListener(listItemClickListener);

        globalTextView = fragmentView.findViewById(R.id.globalAmountTextView2);
        globalTextView.setText(String.valueOf(getPresenter().getAccount().getBudget()));

        limitTextView = fragmentView.findViewById(R.id.limitTextView2);
        limitTextView.setText(String.valueOf(getPresenter().getAccount().getTotalLimit()));

        initFilterSpinnerList();
        filterSpinner = fragmentView.findViewById(R.id.filterSpinner);
        filterSpinnerAdapter = new FilterSpinnerAdapter(getActivity(), filterSpinnerList);
        filterSpinner.setAdapter(filterSpinnerAdapter);

        initSortSpinner();
        sortSpinner = fragmentView.findViewById(R.id.sortSpinner);
        sortSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, sortSpinnerList);
        sortSpinner.setAdapter(sortSpinnerAdapter);

        rightButton = fragmentView.findViewById(R.id.rightButton);
        rightButton.setOnClickListener(rightButtonClickListener);

        leftButton = fragmentView.findViewById(R.id.leftButton);
        leftButton.setOnClickListener(leftButtonClickListener);

        filterSpinner = fragmentView.findViewById(R.id.filterSpinner);
        filterSpinner.setOnItemSelectedListener(filterSpinnerItemSelectedListener);
        if (getPresenter().getFilterPosition() != 0) filterSpinner.setSelection(getPresenter().getFilterPosition());

        sortSpinner = fragmentView.findViewById(R.id.sortSpinner);
        sortSpinner.setOnItemSelectedListener(sortSpinnerItemSelectedListener);
        if (getPresenter().getSortPosition() != 0) sortSpinner.setSelection(getPresenter().getSortPosition());

        addTransactionButton = fragmentView.findViewById(R.id.addTransactionButton);
        addTransactionButton.setOnClickListener(addButtonOnClickListener);

        return fragmentView;
    }

    @Override
    public void setCursor(Cursor cursor) {
        cursorAdapter = new TransactionListCursorAdapter(getActivity(), R.layout.list_element, null, false);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(listItemClickListener);
        cursorAdapter.changeCursor(cursor);
    }

    @Override
    public void setTransactions(ArrayList<Transaction> transactions, String where, String sort) {
        if (transactionListAdapter != null
                && checkInternetConnection(getActivity()) == true) transactionListAdapter.setTransactions(transactions);
        else getPresenter().cursorForAdapter(where, sort);
    }

    @Override
    public void notifyTransactionListDataSetChanged() {
        if (transactionListAdapter != null) transactionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setAccountInfo(Account account) {
        limitTextView.setText(String.valueOf(account.getTotalLimit()));
    }

    @Override
    public int getFilterPosition() {
        return filterSpinner.getSelectedItemPosition();
    }

    @Override
    public int getSortPosition() {
        return sortSpinner.getSelectedItemPosition();
    }

    @Override
    public String getSortType() {
        return sortSpinnerAdapter.getItem(sortSpinner.getSelectedItemPosition());
    }

    @Override
    public LocalDate getDate() {
        return dateFromString((String) dateTextView.getText());
    }

    private void initSortSpinner() {
        sortSpinnerList = new ArrayList<>();
        sortSpinnerList.add("Price - Ascending");
        sortSpinnerList.add("Price - Descending");
        sortSpinnerList.add("Title - Ascending");
        sortSpinnerList.add("Title - Descending");
        sortSpinnerList.add("Date - Ascending");
        sortSpinnerList.add("Date - Descending");
    }

    private void initFilterSpinnerList() {
        filterSpinnerList = new ArrayList<>();
        filterSpinnerList.add(new FilterSpinnerItem("All transactions", R.drawable.picture));
        filterSpinnerList.add(new FilterSpinnerItem("Regular payment", R.drawable.regularpayment));
        filterSpinnerList.add(new FilterSpinnerItem("Regular income", R.drawable.regularincome));
        filterSpinnerList.add(new FilterSpinnerItem("Purchase", R.drawable.purchase));
        filterSpinnerList.add(new FilterSpinnerItem("Individual income", R.drawable.individualincome));
        filterSpinnerList.add(new FilterSpinnerItem("Individual payment", R.drawable.individualpayment));
    }

    private LocalDate dateFromString(String currentDate) {
        int length = currentDate.length();
        String y = currentDate.substring(length-4, length);
        String mon = currentDate.substring(0, length-6);

        int year = Integer.parseInt(y);
        mon = mon.substring(0,1).toUpperCase().concat(mon.substring(1, mon.length()));
        Month month = Enum.valueOf(Month.class, mon);

        LocalDate date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.of(year, month.getValue(), 1);
        }
        return date;
    }

    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (positionOfLastItem != position) {
                positionOfLastItem = position;
                listView.setSelector(R.color.colorAccent);
                if (transactionListAdapter != null) onItemClick.onItemClicked(transactionListAdapter.getTransaction(position));
                else {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    int dataId = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.DATABASE_TRANS_ID);
                    int transId = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TRANSACTION_ID);
                    int akcija = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.AKCIJA);
                    int date = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.DATE);
                    int title = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TITLE);
                    int amount = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.AMOUNT);
                    int type = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TYPE);
                    int transInterval = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TRANSACTION_INTERVAL);
                    int itemDesc = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.ITEM_DESCRIPTION);
                    int endDate = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.END_DATE);
                    LocalDate dat = LocalDate.parse(cursor.getString(date));
                    LocalDate end = null;
                    if (cursor.getString(endDate) != null) end = LocalDate.parse(cursor.getString(endDate));
                    Transaction transaction = new Transaction(cursor.getInt(dataId), cursor.getString(akcija), cursor.getInt(transId),
                            dat, Double.parseDouble(cursor.getString(amount)), cursor.getString(title), cursor.getString(type),
                            cursor.getString(itemDesc), cursor.getInt(transInterval), end);
                    onItemClick.onItemClicked(transaction);
                }
            } else {
                positionOfLastItem = -1;
                listView.setSelector(R.color.white);
                onAddButtonClick.onAddButtonClicked("add");
            }
        }
    };

    private AdapterView.OnClickListener addButtonOnClickListener = new AdapterView.OnClickListener() {
        @Override
        public void onClick(View v) {
            onAddButtonClick.onAddButtonClicked("add");
        }
    };

    private AdapterView.OnClickListener rightButtonClickListener = new AdapterView.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            filterSpinner.setSelection(0);
            sortSpinner.setSelection(0);
            LocalDate dat = dateFromString((String) dateTextView.getText());
            dat = dat.plusMonths(1);

            String date = dat.format(DateTimeFormatter.ofPattern("MMM, yyyy"));
            dateTextView.setText(date);

            checkInternetConnection(getActivity());
            getPresenter().refreshSortFilterList(dat, 0, 0);
        }
    };

    private AdapterView.OnClickListener leftButtonClickListener = new AdapterView.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            filterSpinner.setSelection(0);
            sortSpinner.setSelection(0);
            LocalDate dat = dateFromString((String) dateTextView.getText());
            dat = dat.minusMonths(1);

            String date = dat.format(DateTimeFormatter.ofPattern("MMM, yyyy"));
            dateTextView.setText(date);

            checkInternetConnection(getActivity());
            getPresenter().refreshSortFilterList(dat, 0, 0);
        }
    };

    private AdapterView.OnItemSelectedListener filterSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            LocalDate dat = dateFromString((String) dateTextView.getText());
            checkInternetConnection(getActivity());
            getPresenter().refreshSortFilterList(dat, position, getSortPosition());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemSelectedListener sortSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            LocalDate dat = dateFromString((String) dateTextView.getText());
            checkInternetConnection(getActivity());
            getPresenter().refreshSortFilterList(dat, getFilterPosition(), position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getPresenter().setInternet(true);
            return true;
        }
        getPresenter().setInternet(false);
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                if (diffX > 0) {
                    onSwipe.swipe("leftToRight");
                    return true;
                }
                else if (diffX < 0) {
                    onSwipe.swipe("rightToLeft");
                    return true;
                }
            }
        }

        return false;
    }

}
