package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.BuildConfig;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.ConnectivityBroadcastReceiver;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.R;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.buget.BudgetFragment;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.detail.TransactionDetailFragment;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.graphs.GraphsFragment;

public class MainActivity extends AppCompatActivity implements TransactionListFragment.OnClick, TransactionDetailFragment.onClick,
        BudgetFragment.OnClick, GraphsFragment.OnClick {

    private boolean twoPaneMode;
    private TransactionListFragment listFragment;
    private ConnectivityBroadcastReceiver receiver = new ConnectivityBroadcastReceiver();
    private IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout detail = findViewById(R.id.transaction_detail);

        listFragment = (TransactionListFragment) fragmentManager.findFragmentById(R.id.transaction_list);
        if (listFragment == null) {
            listFragment = new TransactionListFragment();
            fragmentManager.beginTransaction().replace(R.id.transaction_list, listFragment).commit();
        } else {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (detail != null) {
            twoPaneMode = true;

            TransactionDetailFragment detailFragment = (TransactionDetailFragment) fragmentManager.findFragmentById(R.id.transaction_detail);
       //     if (detailFragment == null) {
                Bundle arguments = new Bundle();
                arguments.putString("add", "add");
                detailFragment = new TransactionDetailFragment();
                detailFragment.setArguments(arguments);
                fragmentManager.beginTransaction().replace(R.id.transaction_detail, detailFragment).commit();
         //   }
        } else {
            twoPaneMode = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onItemClicked(Transaction transaction) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("transaction", transaction);

        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);

        if (twoPaneMode) {
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_detail, detailFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, detailFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onAddButtonClicked(String add) {
        Bundle arguments = new Bundle();
        arguments.putString("add", add);

        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);

        if (twoPaneMode) {
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_detail, detailFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, detailFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void swipe(String swipe) {
        if (!twoPaneMode) {
            if (swipe.equals("rightToLeft")) {
                if (this.getActiveFragment().getClass().equals(TransactionListFragment.class)) {
                    BudgetFragment budgetFragment = new BudgetFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, budgetFragment)
                            .addToBackStack(null).commit();
                } else if (this.getActiveFragment().getClass().equals(GraphsFragment.class)) {
                    TransactionListFragment transactionListFragment = new TransactionListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, transactionListFragment)
                            .addToBackStack(null).commit();
                } else if (this.getActiveFragment().getClass().equals(BudgetFragment.class)) {
                    GraphsFragment graphsFragment = new GraphsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, graphsFragment)
                            .addToBackStack(null).commit();
                }
            } else if (swipe.equals("leftToRight")) {
                if (this.getActiveFragment().getClass().equals(TransactionListFragment.class)) {
                    GraphsFragment graphsFragment = new GraphsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, graphsFragment)
                            .addToBackStack(null).commit();
                } else if (this.getActiveFragment().getClass().equals(GraphsFragment.class)) {
                    BudgetFragment budgetFragment = new BudgetFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, budgetFragment)
                            .addToBackStack(null).commit();
                } else if (this.getActiveFragment().getClass().equals(BudgetFragment.class)) {
                    TransactionListFragment transactionListFragment = new TransactionListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, transactionListFragment)
                            .addToBackStack(null).commit();
                }
            }
        }
    }

    @Override    //detail fragment
    public void onSaveButtonClicked() {
        TransactionListFragment listFragment = new TransactionListFragment();
        if (twoPaneMode) {
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, listFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, listFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onDeleteButtonClicked() {
        TransactionListFragment listFragment = new TransactionListFragment();

        if (twoPaneMode) {
            Bundle argument = new Bundle();
            argument.putString("add", "add");
            TransactionDetailFragment detailFragment = new TransactionDetailFragment();
            detailFragment.setArguments(argument);

            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, listFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_detail, detailFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_list, listFragment).addToBackStack(null).commit();
        }
    }

    private Fragment getActiveFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) return fragment;
        }
        return null;
    }

}