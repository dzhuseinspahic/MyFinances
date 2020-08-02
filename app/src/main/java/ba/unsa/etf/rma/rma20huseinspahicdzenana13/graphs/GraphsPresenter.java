package ba.unsa.etf.rma.rma20huseinspahicdzenana13.graphs;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.Transaction;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.TransactionListInteractor;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GraphsPresenter implements IGraphsPresenter {
    private Context context;
    private IGraphsView view;
    private LocalDate date = LocalDate.now();
    private ArrayList<Transaction> transactions;

    public GraphsPresenter(IGraphsView view, Context context) {
        this.context = context;
        this.view = view;
        try {
            this.transactions = new TransactionListInteractor().execute("getAll").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getDaysOfMonth() {
        return date.lengthOfMonth();
    }

    @Override
    public void refreshGraphs(ArrayList<BarEntry> potrosnja, ArrayList<BarEntry> zarada, ArrayList<BarEntry> ukupno) {
        BarDataSet potrosnjaBarDataSet = new BarDataSet(potrosnja, "Potrošnja");
        potrosnjaBarDataSet.setColor(Color.RED);

        BarDataSet zaradaBarDataSet = new BarDataSet(zarada, "Zarada");
        zaradaBarDataSet.setColor(Color.GREEN);

        BarDataSet ukupnoBarDataSet = new BarDataSet(ukupno, "Ukupno stanje");
        ukupnoBarDataSet.setColor(Color.YELLOW);

        view.setGraphs(potrosnjaBarDataSet, zaradaBarDataSet, ukupnoBarDataSet);
    }

    @Override
    public double getDayPayment(int dayOfMonth) {
        double result = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0
                    && ((transaction.getDate().getDayOfMonth() == dayOfMonth
                    && transaction.getDate().getMonth() == date.getMonth()
                    && transaction.getDate().getYear() == date.getYear())
                    || (transaction.getEndDate() != null
                    && transaction.getEndDate().isAfter(date)
                    && plusDays(dayOfMonth, transaction)))) {
                result -= transaction.getAmount();
            }
        }
        return result;
    }

    @Override
    public double getWeekPayment(int weekNumber) {
        double result = 0;
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        for (Transaction transaction : transactions) {
            int weekNum = transaction.getDate().get(weekFields.weekOfWeekBasedYear());
            if (transaction.getAmount() < 0
                    && ((transaction.getDate().getYear() == date.getYear()
                    && weekNumber == weekNum)
                    || (transaction.getEndDate() != null
                    && plusWeeks(transaction, weekNumber))))
                result -= transaction.getAmount();
        }
        return result;
    }

    @Override
    public double getMonthPayment(int month) {
        double result = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0
                    && ((transaction.getDate().getMonthValue() == month
                    && transaction.getDate().getYear() == date.getYear())
                    || transaction.getEndDate() != null)) {
                if (transaction.getEndDate() != null) {
                    if (transaction.getEndDate() != null) {
                        int plus = plusMonths(transaction, month);
                        for (int i = 0; i < plus; i++) result -= transaction.getAmount();
                    }
                }
                else result -= transaction.getAmount();
            }
        }
        return result;
    }

    @Override
    public double getDayIncome(int dayOfMonth) {
        double result = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0
                    && ((transaction.getDate().getDayOfMonth() == dayOfMonth
                    && transaction.getDate().getMonth() == date.getMonth()
                    && transaction.getDate().getYear() == date.getYear())
                    || (transaction.getEndDate() != null
                    && transaction.getEndDate().isAfter(date)
                    && plusDays(dayOfMonth, transaction))))
                result += transaction.getAmount();
        }
        return result;
    }

    @Override
    public double getWeekIncome(int weekNumber) {
        double result = 0;
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        for (Transaction transaction : transactions) {
            int weekNum = transaction.getDate().get(weekFields.weekOfWeekBasedYear());
            if (transaction.getAmount() > 0
                    && ((transaction.getDate().getYear() == date.getYear()
                    && weekNumber == weekNum)
                    || (transaction.getEndDate() != null
                    && plusWeeks(transaction, weekNumber))))
                result += transaction.getAmount();
        }
        return result;
    }

    @Override
    public double getMonthIncome(int month) {
        double result = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0
                    && ((transaction.getDate().getMonthValue() == month
                    && transaction.getDate().getYear() == date.getYear())
                    || transaction.getEndDate() != null)) {
                if (transaction.getEndDate() != null) {
                    int plus = plusMonths(transaction, month);
                    for (int i = 0; i < plus; i++) result += transaction.getAmount();
                } else result += transaction.getAmount();
            }
        }
        return result;
    }

    @Override
    public double getDayAllTrans(int dayOfMonth) {
        return getDayIncome(dayOfMonth) - getDayPayment(dayOfMonth);
    }

    @Override
    public double getWeekAllTrans(int weekNumber) {
        return getWeekIncome(weekNumber) - getWeekPayment(weekNumber);
    }

    @Override
    public double getMonthAllTrans(int month) {
        return getMonthIncome(month) - getMonthPayment(month);
    }

    private boolean plusDays(int dayOfMonth, Transaction transaction) {
        LocalDate datt = transaction.getDate();
        if (transaction.getTransactionInterval() == 0) return false;

        while(datt.getMonthValue() <= date.getMonthValue() && datt.getYear() <= date.getYear()
                && (datt.isBefore(transaction.getEndDate()) || datt.equals(transaction.getEndDate()))) {
            if (datt.getDayOfMonth() == dayOfMonth && datt.getMonth() == date.getMonth()) return true;
            datt = datt.plusDays(transaction.getTransactionInterval());
        }
        return false;
    }

    private boolean plusWeeks(Transaction transaction, int weekNumber) {
        LocalDate transactionDate = transaction.getDate();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        if (transaction.getTransactionInterval() == 0) return false;

        while(transactionDate.getYear() <= date.getYear()
                && (transactionDate.isBefore(transaction.getEndDate()) || transactionDate.equals(transaction.getEndDate()))) {
            int weekNum = transactionDate.get(weekFields.weekOfWeekBasedYear());
            if (weekNum == weekNumber && transactionDate.getYear() == date.getYear()) return true;
            transactionDate = transactionDate.plusDays(transaction.getTransactionInterval());
        }
        return false;
    }

    private int plusMonths(Transaction transaction, int month) {
        int result = 0;
        LocalDate transactionDate = transaction.getDate();
        while(transactionDate.getYear() <= date.getYear()
                && (transactionDate.isBefore(transaction.getEndDate()) || transactionDate.equals(transaction.getEndDate()))) {
            if (transactionDate.getMonthValue() == month
                    && transactionDate.getYear() == date.getYear()) result += 1;
            transactionDate = transactionDate.plusDays(transaction.getTransactionInterval());
        }
        return result;
    }
}
