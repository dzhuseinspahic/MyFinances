package ba.unsa.etf.rma.rma20huseinspahicdzenana13.detail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.R;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.Transaction;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.TypeInteractor;

public class TransactionDetailFragment extends Fragment implements ITransactionDetailView {
    private TextView internetTextView;

    private EditText date;
    private EditText amount;
    private EditText title;
    private EditText type;
    private EditText itemDescription;
    private EditText transactionInterval;
    private EditText endDate;
    private Button save;
    private Button delete;

    private String dateValidation;
    private String amountValidation;
    private String typeValidation;
    private String titleValidation;
    private String itemDescrValidation;
    private String transInterValidation;
    private String endDateValidation;

    private ArrayList<String> types;

    private ITransactionDetailPresenter presenter;

    private boolean internet;

    public ITransactionDetailPresenter getPresenter() {
        if (presenter == null) {
            presenter = new TransactionDetailPresenter(this, getActivity());
        }
        return presenter;
    }

    private onClick onSaveButtonClick;
    private onClick onDeleteButtonClick;
    public interface onClick{
        void onSaveButtonClicked();
        void onDeleteButtonClicked();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);

        types = new TypeInteractor().getTypes();

        onSaveButtonClick = (onClick) getActivity();
        onDeleteButtonClick = (onClick) getActivity();

        internetTextView = fragmentView.findViewById(R.id.internetTextView);
        date = fragmentView.findViewById(R.id.dateText);
        amount = fragmentView.findViewById(R.id.amountText);
        title = fragmentView.findViewById(R.id.titleText);
        type = fragmentView.findViewById(R.id.typeText);
        itemDescription = fragmentView.findViewById(R.id.itemDescText);
        transactionInterval = fragmentView.findViewById(R.id.transIntervalText);
        endDate = fragmentView.findViewById(R.id.endDateText);
        save = fragmentView.findViewById(R.id.saveButton);
        delete = fragmentView.findViewById(R.id.deleteButton);

        if (getArguments() != null) {
            if (getArguments().containsKey("add"))
                delete.setEnabled(false);
            else {
                delete.setEnabled(true);
                Transaction transaction = getArguments().getParcelable("transaction");
                getPresenter().setTransactionFromAnotherFragment(getArguments().getParcelable("transaction"));
                if (transaction.getAkcija() != null && transaction.getAkcija().equals("delete")) {
                    internetTextView.setText("Offline brisanje");
                    delete.setText("Undo");
                    save.setEnabled(false);
                }
                setTransaction((Transaction) getArguments().getParcelable("transaction"));
                delete.setOnClickListener(deleteOnClickListener);
            }

            save.setOnClickListener(saveOnClickListener);

            dateValidation = date.getText().toString();
            amountValidation = amount.getText().toString();
            typeValidation = type.getText().toString();
            titleValidation = title.getText().toString();
            itemDescrValidation = itemDescription.getText().toString();
            transInterValidation = transactionInterval.getText().toString();
            endDateValidation = endDate.getText().toString();

            date.addTextChangedListener(dateTextChangeListener);
            amount.addTextChangedListener(amountTextChangeListener);
            title.addTextChangedListener(titleTextChangeListener);
            type.addTextChangedListener(typeTextChangeListener);
            itemDescription.addTextChangedListener(itemDescriptionTextChangeListener);
            transactionInterval.addTextChangedListener(transactionIntervalTextChangeListener);
            endDate.addTextChangedListener(endDateTextChangeListener);
        }

        checkInternetConnection(getActivity());

        return fragmentView;
    }

    @Override
    public void setTransaction(Transaction transaction) {
        date.setText(transaction.getDate().toString());
        amount.setText(String.valueOf(transaction.getAmount()));
        title.setText(transaction.getTitle());
        type.setText(transaction.getType().toString());
        if (transaction.getItemDescription() == null) itemDescription.setText("");
        else itemDescription.setText(transaction.getItemDescription());
        transactionInterval.setText(String.valueOf(transaction.getTransactionInterval()));
        if (transaction.getEndDate() == null) endDate.setText("");
        else endDate.setText(transaction.getEndDate().toString());
    }

    @Override
    public void setColorBack() {
        date.setBackgroundColor(Color.parseColor("#F6E233"));
        amount.setBackgroundColor(Color.parseColor("#F6E233"));
        title.setBackgroundColor(Color.parseColor("#F6E233"));
        type.setBackgroundColor(Color.parseColor("#F6E233"));
        itemDescription.setBackgroundColor(Color.parseColor("#F6E233"));
        transactionInterval.setBackgroundColor(Color.parseColor("#F6E233"));
        endDate.setBackgroundColor(Color.parseColor("#F6E233"));
    }

    private TextWatcher dateTextChangeListener = new TextWatcher()  {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!internetTextView.getText().toString().contains("Offline") && internet == true) checkInternetConnection(getActivity());
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (!date.getText().toString().equals("")) {
                    LocalDate.parse(date.getText());
                    if (!date.getText().toString().equals(dateValidation))
                        date.setBackgroundColor(Color.parseColor("#FF4CAF50"));
                    else date.setBackgroundColor(Color.parseColor("#F6E233"));
                }
            } catch (Throwable throwable) {
                date.setBackgroundColor(Color.parseColor("#E60808"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher amountTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!internetTextView.getText().toString().contains("Offline") && internet == true) checkInternetConnection(getActivity());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try{
                if (!amount.getText().toString().equals("")) {
                    Double.parseDouble(String.valueOf(s));
                    if (!amount.getText().toString().equals(amountValidation))
                        amount.setBackgroundColor(Color.parseColor("#FF4CAF50"));
                    else amount.setBackgroundColor(Color.parseColor("#F6E233"));
                }
            } catch (Throwable throwable) {
                amount.setBackgroundColor(Color.parseColor("#E60808"));
            }
        }
    };

    private TextWatcher titleTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!internetTextView.getText().toString().contains("Offline") && internet == true) checkInternetConnection(getActivity());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!titleValidation.equals(title.getText().toString())) {
                if (title.getText().length() <= 3 || title.getText().length() >= 15)
                    title.setBackgroundColor(Color.parseColor("#E60808"));
                else title.setBackgroundColor(Color.parseColor("#FF4CAF50"));
            } else {
                title.setBackgroundColor(Color.parseColor("#F6E233"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher typeTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!internetTextView.getText().toString().contains("Offline") && internet == true) checkInternetConnection(getActivity());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString().replaceAll("\\s+", "").toUpperCase();
            if (!text.equals(typeValidation)) {
                boolean set = false;
                for (int i=1; i<=types.size(); i++) {
                    if (text.toUpperCase().replace(" ", "").equals(types.get(i-1).toUpperCase().replace(" ", ""))
                            || text.equals(String.valueOf(i))) {
                        type.setBackgroundColor(Color.parseColor("#FF4CAF50"));
                        set = true;
                        break;
                    }
                }
                if (!set) type.setBackgroundColor(Color.parseColor("#E60808"));
            } else type.setBackgroundColor(Color.parseColor("#F6E233"));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher transactionIntervalTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!internetTextView.getText().toString().contains("Offline") && internet == true) checkInternetConnection(getActivity());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try{
                if (!transactionInterval.getText().toString().equals("")) {
                    Integer.parseInt(String.valueOf(s));
                    if (!transactionInterval.getText().toString().equals(transInterValidation))
                        transactionInterval.setBackgroundColor(Color.parseColor("#FF4CAF50"));
                    else transactionInterval.setBackgroundColor(Color.parseColor("#F6E233"));
                }
            } catch (Throwable throwable) {
                transactionInterval.setBackgroundColor(Color.parseColor("#E60808"));
            }
        }
    };

    private TextWatcher itemDescriptionTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!internetTextView.getText().toString().contains("Offline") && internet == true) checkInternetConnection(getActivity());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!itemDescription.getText().toString().equals(itemDescrValidation))
                itemDescription.setBackgroundColor(Color.parseColor("#FF4CAF50"));
            else itemDescription.setBackgroundColor(Color.parseColor("#F6E233"));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher endDateTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!internetTextView.getText().toString().contains("Offline") && internet == true) checkInternetConnection(getActivity());
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (!endDate.getText().toString().equals("")) {
                    LocalDate.parse(endDate.getText());
                    String type = s.toString().replaceAll("\\s+", "").toUpperCase();
                    if (type.contains("REGULAR") && endDate.getText().toString().equals(""))
                        endDate.setBackgroundColor(Color.parseColor("#E60808"));
                    if (!endDate.getText().toString().equals(endDateValidation)) {
                        if (date.getText() != null && LocalDate.parse(date.getText()).isAfter(LocalDate.parse(endDate.getText())))
                            endDate.setBackgroundColor(Color.parseColor("#E60808"));
                        else endDate.setBackgroundColor(Color.parseColor("#FF4CAF50"));
                    } else endDate.setBackgroundColor(Color.parseColor("#F6E233"));
                }
            } catch (Throwable throwable) {
                endDate.setBackgroundColor(Color.parseColor("#E60808"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener saveOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            checkInternetConnection(getActivity());
            try{
                LocalDate endDatee;
                if (endDate.getText().toString().equals("")) endDatee = null;
                else endDatee = LocalDate.parse(endDate.getText().toString());
                int interval;
                if (transactionInterval.getText().toString().equals("")) interval = 0;
                else interval = Integer.parseInt(transactionInterval.getText().toString());
                final Transaction transaction;
                if (getArguments().containsKey("add")) transaction = new Transaction(LocalDate.parse(date.getText().toString()),
                        Double.parseDouble(amount.getText().toString()), title.getText().toString(),
                        type.getText().toString().replaceAll("\\s+", "").toUpperCase(),
                        itemDescription.getText().toString(), interval, endDatee);
                else if (getPresenter().getTransaction().getDatabaseId() != 0)
                    transaction = new Transaction(getPresenter().getTransaction().getDatabaseId(), getPresenter().getTransaction().getAkcija(),
                            getPresenter().getTransaction().getId(), LocalDate.parse(date.getText().toString()),
                        Double.parseDouble(amount.getText().toString()), title.getText().toString(),
                        type.getText().toString().replaceAll("\\s+", "").toUpperCase(),
                        itemDescription.getText().toString(), interval, endDatee);
                else transaction = new Transaction(getPresenter().getTransaction().getId(), LocalDate.parse(date.getText().toString()),
                        Double.parseDouble(amount.getText().toString()), title.getText().toString(),
                        type.getText().toString().replaceAll("\\s+", "").toUpperCase(),
                        itemDescription.getText().toString(), interval, endDatee);

                if (getPresenter().checkLimits(transaction)) {
                    AlertDialog alert = new AlertDialog.Builder(getActivity())
                            .setTitle("Limit Exceeded")
                            .setMessage("Are you sure you want to save changes you've made? ")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (internet == true) {
                                        if (getArguments().containsKey("add"))
                                            getPresenter().addTransaction(transaction);
                                        else getPresenter().updateTransaction(transaction);
                                    } else if (internet == false) {
                                        if (getArguments().containsKey("add"))
                                            getPresenter().addTransactionToDatabase(transaction, "add");
                                        else getPresenter().addTransactionToDatabase(transaction, "update");
                                    }
                                    onSaveButtonClick.onSaveButtonClicked();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {
                    if (internet == true) {
                        if (getArguments().containsKey("add"))
                            getPresenter().addTransaction(transaction);
                        else getPresenter().updateTransaction(transaction);
                    } else if (internet == false) {
                        if (getArguments().containsKey("add"))
                            getPresenter().addTransactionToDatabase(transaction, "add");
                        else getPresenter().addTransactionToDatabase(transaction, "update");
                    }
                    onSaveButtonClick.onSaveButtonClicked();
                }

                setColorBack();
            } catch (Throwable throwable) {
                AlertDialog alert = new AlertDialog.Builder(getActivity())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setTitle("Warning").setMessage("You did not entered the correct informations.").show();
            }
        }
    };

    private View.OnClickListener deleteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (delete.getText().toString().equals("Undo")) {
                AlertDialog alert = new AlertDialog.Builder(getActivity()).setTitle("Undo?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getPresenter().addTransactionToDatabase(getPresenter().getTransaction(), "undo");
                                onDeleteButtonClick.onDeleteButtonClicked();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            } else {
                checkInternetConnection(getActivity());
                AlertDialog alert = new AlertDialog.Builder(getActivity()).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this transaction?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (internet == true)
                                    getPresenter().deleteTransaction(getPresenter().getTransaction());
                                else getPresenter().addTransactionToDatabase(getPresenter().getTransaction(), "delete");
                                onDeleteButtonClick.onDeleteButtonClicked();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        }
    };

    private void checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED
                && cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            if (getArguments() != null && getArguments().containsKey("add"))
                internetTextView.setText("Offline dodavanje");
            else if (delete.getText().toString().equals("Undo")) internetTextView.setText("Offline brisanje");
            else internetTextView.setText("Offline izmjena");
            internet = false;
        } else internet = true;
    }

}
