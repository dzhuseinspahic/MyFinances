package ba.unsa.etf.rma.rma20huseinspahicdzenana13.graphs;

import com.github.mikephil.charting.data.BarDataSet;

public interface IGraphsView {
    void setGraphs(BarDataSet potrosnja, BarDataSet zarada, BarDataSet ukupno);
    String getCheckedRadioButton();
}
