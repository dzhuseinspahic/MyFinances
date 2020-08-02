package ba.unsa.etf.rma.rma20huseinspahicdzenana13.buget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.R;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;

public class BudgetFragment extends Fragment implements IBudgetView, GestureDetector.OnGestureListener {
    private EditText budgetEditText;
    private EditText totalLimitEditText;
    private EditText monthLimitEditText;
    private Button saveButton;
    private TextView textView;

    private IBudgetPresenter budgetPresenter;
    private GestureDetector gestureDetector;

    public IBudgetPresenter getPresenter() {
        if (budgetPresenter == null) {
            budgetPresenter = new BudgetPresenter(this, getActivity());
        }
        return budgetPresenter;
    }

    private OnClick onSwipe;
    public interface OnClick{
        void swipe(String swipe);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_budget, container, false);

        onSwipe = (OnClick) getActivity();

        gestureDetector = new GestureDetector(this);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        textView = fragmentView.findViewById(R.id.textView);
        budgetEditText = fragmentView.findViewById(R.id.budgetEditText);
        totalLimitEditText = fragmentView.findViewById(R.id.totalLimitEditText);
        monthLimitEditText = fragmentView.findViewById(R.id.monthLimitEditText);

        saveButton = fragmentView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(onSaveButtonClickListener);

        setBudget(getPresenter().getAccount());

        checkInternetConnection(getActivity());

        return fragmentView;
    }

    private View.OnClickListener onSaveButtonClickListener = new AdapterView.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                checkInternetConnection(getActivity());
                Account account = new Account(getPresenter().getAccount().getDatebaseId(), getPresenter().getAccount().getId(),
                        getPresenter().getAccount().getBudget(),
                        Double.parseDouble(String.valueOf(totalLimitEditText.getText())),
                        Double.parseDouble(String.valueOf(monthLimitEditText.getText())));
                 getPresenter().updateAccount(account);
            } catch (Exception exception) {
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

    @Override
    public void setBudget(Account account) {   //view
        budgetEditText.setText(String.valueOf(account.getBudget()));
        totalLimitEditText.setText(String.valueOf(account.getTotalLimit()));
        monthLimitEditText.setText(String.valueOf(account.getMonthLimit()));
    }

    @Override
    public void notifyBudgetDataSetChanged() {      //view
        notifyBudgetDataSetChanged();
    }

    private boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getPresenter().setInternet(true);
            return true;
        }
        textView.setText("Offline izmjena");
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
