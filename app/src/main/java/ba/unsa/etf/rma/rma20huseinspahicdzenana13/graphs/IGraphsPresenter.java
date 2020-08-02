package ba.unsa.etf.rma.rma20huseinspahicdzenana13.graphs;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public interface IGraphsPresenter {
    int getDaysOfMonth();
    void refreshGraphs(ArrayList<BarEntry> potrosnja, ArrayList<BarEntry> zarada, ArrayList<BarEntry> ukupno);
    double getDayPayment(int dayOfMonth);
    double getWeekPayment(int weekNumber);
    double getMonthPayment(int month);
    double getDayIncome(int dayOfMonth);
    double getWeekIncome(int weekNumber);
    double getMonthIncome(int month);
    double getDayAllTrans(int dayOfMonth);
    double getWeekAllTrans(int weekNumber);
    double getMonthAllTrans(int month);
}
