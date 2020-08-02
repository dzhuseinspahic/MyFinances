package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import java.time.LocalDate;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;

public interface ITransactionListPresenter {

    void refreshSortFilterList(LocalDate date, int filterPosition, int sortPosition);
    ArrayList<Transaction> getInitialList();

    void setTransaction(Transaction transaction);
    Transaction getTransaction();

    LocalDate getDate();
    int getFilterPosition();
    int getSortPosition();

    Account getAccount();

    void setInternet(boolean internet);
    boolean getInternet();

    void saveOnServer();

    void cursorForAdapter(String where, String sort);
}
