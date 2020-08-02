package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.database.Cursor;

import java.time.LocalDate;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;

public interface ITransactionListView {
    void setTransactions(ArrayList<Transaction> transactions, String where, String sort);

    void notifyTransactionListDataSetChanged();
    void setAccountInfo(Account account);
    int getFilterPosition();
    int getSortPosition();
    String getSortType();
    LocalDate getDate();
    void setCursor(Cursor cursor);
}
