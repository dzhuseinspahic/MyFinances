package ba.unsa.etf.rma.rma20huseinspahicdzenana13.detail;

import android.os.Parcelable;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.Transaction;

public interface ITransactionDetailPresenter {
    void addTransaction(Transaction transaction);
    void deleteTransaction(Transaction transaction);
    void updateTransaction(Transaction transaction);

    void addTransactionToDatabase(Transaction transaction, String akcija);

    boolean checkLimits(Transaction transaction);
    Transaction getTransaction();
    Account getAccount();
    ArrayList<Transaction> getList();
    void create(Transaction transaction);
    void setTransactionFromAnotherFragment(Parcelable transaction);
}

