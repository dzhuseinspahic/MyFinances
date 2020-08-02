package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.ConnectivityBroadcastReceiver;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.AccountInteractor;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.detail.TransactinoDetailInteractor;

public class TransactionListPresenter implements ITransactionListPresenter {
    private ITransactionListView view;
    private Context context;
    private Transaction transaction;
    private static LocalDate date;
    private static int filterPosition, sortPosition;
    private Account account;
    private ArrayList<Transaction> transactions;
    private ArrayList<String> types;
    private TransactionListInteractor interactor;
    private AccountInteractor accountInteractor;
    private ArrayList<Transaction> datebaseTransactions;
    private Account databaseAccount;
    private boolean internet;

    public TransactionListPresenter(ITransactionListView view, Context context) {
        this.view = view;
        this.context = context;
        interactor = new TransactionListInteractor();
        accountInteractor = new AccountInteractor();
        new TransactionListInteractor();
        new AccountInteractor();
        try {
            transactions = new TransactionListInteractor().execute("getAll").get();
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }
        if (transactions == null) transactions = interactor.getTransactions();
        try {
            this.account = new AccountInteractor().execute().get();
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datebaseTransactions = interactor.getTransactionFromDatabase(context.getApplicationContext());
            if (transactions == null) transactions = datebaseTransactions;
        }
        databaseAccount = accountInteractor.getAccountDatabase(context.getApplicationContext());
        if (databaseAccount != null && account != null) account.setDatebaseId(databaseAccount.getDatebaseId());
        if (databaseAccount == null && this.account != null) {
            accountInteractor.saveToDatabase(this.account, "insert", context.getApplicationContext());
            databaseAccount = accountInteractor.getAccountDatabase(context.getApplicationContext());
        }
        if (account == null) this.account = databaseAccount;
    }

    @Override
    public void saveOnServer() {
        if (datebaseTransactions.size() != 0) {
            HashMap<String, Transaction> map = new HashMap<>();
            map.put("all", datebaseTransactions.get(0));
            int i = 0;
            for (Transaction trans : datebaseTransactions) {
                trans.setAkcija(trans.getAkcija() + i);
                map.put(trans.getAkcija(), trans);
                i++;
            }
            new TransactinoDetailInteractor().execute(map);
            interactor.deleteFromDatabase(datebaseTransactions, context);
        }
        databaseAccount = accountInteractor.getAccountDatabase(context.getApplicationContext());
        if (databaseAccount != null) {
            HashMap<String, Account> m = new HashMap<>();
            m.put("update", databaseAccount);
            try {
                new AccountInteractor().execute(m).get();
            } catch (ExecutionException e) {
            } catch (InterruptedException e) {
            }
            this.account = databaseAccount;
            accountInteractor.deleteFromDatabase(databaseAccount, context.getApplicationContext());
            if (this.account != null) accountInteractor.saveToDatabase(this.account, "insert", context.getApplicationContext());
        }
    }

    @Override
    public void cursorForAdapter(String where, String sort) {
        view.setCursor(interactor.getCursor(where, sort, context.getApplicationContext()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void refreshSortFilterList(LocalDate date, int filterPosition, int sortPosition) {
        this.date = date;
        this.filterPosition = filterPosition;
        this.sortPosition = sortPosition;
        ArrayList<Transaction> transactions = new ArrayList<>();

        if (internet == true) {
            String sort = view.getSortType();
            if (sort.contains("Price")) sort = "amount.";
            else if (sort.contains("Title")) sort = "title.";
            else sort = "date.";
            if (sort.contains("Ascending")) sort += "asc";
            else sort += "desc";

            try {
                String month = String.valueOf(date.getMonthValue());
                if (date.getMonthValue() < 10) month = "0" + month;
                String query = "&month=" + month + "&year=" + date.getYear() + "&sort=" + sort;

                if (filterPosition == 0) {
                    for (int i = 3; i < 6; i++)
                        transactions.addAll(new TransactionListInteractor().execute(query + "&typeId=" + i).get());
                    transactions.addAll(getRegularTransactions(date, query));
                    transactions = setElementsOfListSort(sortPosition, transactions);
                } else if (filterPosition >= 3 && filterPosition <= 5) {
                    query += "&typeId=" + filterPosition;
                    transactions = new TransactionListInteractor().execute(query).get();
                } else if (filterPosition == 1 || filterPosition == 2) {
                    query += "&typeId=" + filterPosition;
                    transactions.addAll(getRegularTransactions(date, query));
                    transactions = setElementsOfListSort(sortPosition, transactions);
                }
                view.setTransactions(transactions, null, null);
                view.notifyTransactionListDataSetChanged();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            String d = date.getYear() + "-";
            if (date.getMonthValue() < 10) d += "0" + date.getMonthValue();
            else d += date.getMonthValue();
            String where = TransactionListDBOpenHelper.DATE + " LIKE '" + d + "%'";
            if (date.getDayOfMonth() < 10) d += "-0" + date.getDayOfMonth();
            else d += "-" + date.getDayOfMonth();
       /*     if (filterPosition >= 0 && filterPosition <= 2)
                where += " OR (" + TransactionListDBOpenHelper.DATE + " <= " + d
                    + " AND " + TransactionListDBOpenHelper.END_DATE + " >= " + d + ")";
        */    String sort = null;
            if (sortPosition == 0) sort = TransactionListDBOpenHelper.AMOUNT + " ASC";
            else if (sortPosition == 1) sort = TransactionListDBOpenHelper.AMOUNT + " DESC";
            else if (sortPosition == 2) sort = TransactionListDBOpenHelper.TITLE + " ASC";
            else if (sortPosition == 3) sort = TransactionListDBOpenHelper.TITLE + " DESC";
            else if (sortPosition == 4) sort = TransactionListDBOpenHelper.DATE + " ASC";
            else if (sortPosition == 5) sort = TransactionListDBOpenHelper.DATE + " DESC";
            view.setTransactions(null, where, sort);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Transaction> getRegularTransactions(LocalDate date, String query) {
        ArrayList<Transaction> newList = new ArrayList<>();
        ArrayList<Transaction> regular = new ArrayList<>();

        query = query.substring(19);

        try {
            if (filterPosition == 0) {
                regular = new TransactionListInteractor().execute(query + "&typeId=1").get();
                regular.addAll(new TransactionListInteractor().execute(query + "&typeId=2").get());
            } else regular = new TransactionListInteractor().execute(query).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Transaction trans : regular) {
            String s = trans.getTitle();
            if ((trans.getDate().isBefore(date) || (trans.getDate().getMonthValue() == date.getMonthValue()
                    && trans.getDate().getYear() == date.getYear()))
                    && (trans.getEndDate().isAfter(date) || (trans.getEndDate().getMonthValue() == date.getMonthValue()
                    && trans.getEndDate().getYear() == date.getYear()))) {
               /* if (trans.getTransactionInterval() == 15) {
                    newList.add(trans);
                    newList.add(trans);
                } else {
                    int display = trans.getTransactionInterval() / 30;
                    if (display == 0) continue;
                    int temp = trans.getDate().getMonthValue();
                    LocalDate dat = trans.getDate();
                    while (true) {
                        if (temp == date.getMonthValue()) {
                            newList.add(trans);
                            break;
                        }
                        if (dat.isAfter(date)) break;
                        temp += display;
                        dat = dat.plusMonths(display);
                    }
                }*/
               if (trans.getTransactionInterval() == 0) {
                   if (trans.getDate().getYear() == date.getYear()
                           && trans.getDate().getMonthValue() == date.getMonthValue()) newList.add(trans);
                   continue;
               }
               LocalDate dat = trans.getDate();
               while (true) {
                   if (dat.getMonthValue() == date.getMonthValue() && dat.getYear() == date.getYear()) newList.add(trans);
                   if (dat.getMonthValue() > date.getMonthValue() && dat.getYear() == date.getYear()) break;
                   dat = dat.plusDays(trans.getTransactionInterval());
               }
            }
        }
        return newList;
    }

    private ArrayList<Transaction> setElementsOfListFilter(int position, ArrayList<Transaction> transactions) {
        ArrayList<Transaction> newList = new ArrayList<>();

        types = TypeInteractor.types;

        if (position == 0) newList.addAll(transactions);
        else {
            for (int i = 1; i <= types.size(); i++)
                if (i == position) {
                    for (Transaction trans : transactions)
                        if (types.get(i - 1).equals(trans.getType())) newList.add(trans);
                }
        }
          /*  else if (position == 1) {
            for(Transaction trans : transactions) {
                if (trans.getType().equals("Individual payment")) newList.add(trans);
            }
        } else if (position == 2) {
            for(Transaction trans : transactions) {
                if (trans.getType().equals("Regular payment")) newList.add(trans);
            }
        } else if (position == 3) {
            for(Transaction trans : transactions) {
                if (trans.getType().equals("Purchase")) newList.add(trans);
            }
        } else if (position == 4) {
            for(Transaction trans : transactions) {
                if (trans.getType().equals("Individual income")) newList.add(trans);
            }
        } else if (position == 5) {
            for(Transaction trans : transactions) {
                if (trans.getType().equals("Regular income")) newList.add(trans);
            }
        }*/

        return newList;
    }

    private ArrayList<Transaction> setElementsOfListSort(int position, ArrayList<Transaction> transactions) {
        if (position == 0) {   //price ascending
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction first, Transaction second) {
                    return (int) (first.getAmount() - second.getAmount());
                }
            });
        } else if (position == 1) {  //price descending
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction first, Transaction second) {
                    return (int) (second.getAmount() - first.getAmount());
                }
            });
        } else if (position == 2) {  //title ascending
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction first, Transaction second) {
                    return first.getTitle().compareTo(second.getTitle());
                }
            });
        } else if (position == 3) {  //title descending
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction first, Transaction second) {
                    return second.getTitle().compareTo(first.getTitle());
                }
            });
        } else if (position == 4) {  //date ascending
            Collections.sort(transactions, new Comparator<Transaction>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public int compare(Transaction first, Transaction second) {
                    return first.getDate().compareTo(second.getDate());
                }
            });
        } else if (position == 5) {  //date descending
            Collections.sort(transactions, new Comparator<Transaction>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public int compare(Transaction first, Transaction second) {
                    return second.getDate().compareTo(first.getDate());
                }
            });
        }

        return transactions;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public int getFilterPosition() {
        return filterPosition;
    }

    @Override
    public int getSortPosition() {
        return sortPosition;
    }

    @Override
    public Account getAccount() {
        if (this.account == null || (internet == false && databaseAccount != null)) this.account = databaseAccount;
        return this.account;
    }

    @Override
    public void setInternet(boolean internet) {
        this.internet = internet;
    }

    @Override
    public boolean getInternet() {
        return internet;
    }

    @Override
    public ArrayList<Transaction> getInitialList() {
        return transactions;
    }

    @Override
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }
}
