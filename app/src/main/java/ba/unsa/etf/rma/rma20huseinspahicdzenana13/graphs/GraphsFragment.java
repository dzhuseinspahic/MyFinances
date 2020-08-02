package ba.unsa.etf.rma.rma20huseinspahicdzenana13.graphs;

import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.R;

public class GraphsFragment extends Fragment implements IGraphsView, GestureDetector.OnGestureListener {
    private BarChart paymentBarChart;
    private BarChart incomeBarChart;
    private BarChart allBarChart;

    private RadioButton dayRadioButton;
    private RadioButton monthRadioButton;
    private RadioButton weekRadioButton;
    private RadioGroup radioGroup;

    private IGraphsPresenter graphsPresenter;
    private GestureDetector gestureDetector;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public IGraphsPresenter getGraphsPresenter() {
        if (graphsPresenter == null) {
            graphsPresenter = new GraphsPresenter(this, getActivity());
        }
        return graphsPresenter;
    }

    private OnClick onSwipe;
    public interface OnClick {
        void swipe(String swipe);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_graphs, container, false);

        onSwipe = (OnClick) getActivity();

        gestureDetector = new GestureDetector(this);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        paymentBarChart = fragmentView.findViewById(R.id.paymentBarChart);
        incomeBarChart = fragmentView.findViewById(R.id.incomeBarChart);
        allBarChart = fragmentView.findViewById(R.id.allBarChart);

        dayRadioButton = fragmentView.findViewById(R.id.dayRadioButton);
        weekRadioButton = fragmentView.findViewById(R.id.weekRadioButton);
        monthRadioButton = fragmentView.findViewById(R.id.monthRadioButton);

        radioGroup = fragmentView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupCheckedListener);

        getGraphsPresenter().refreshGraphs(potrosnjaDataValues(getCheckedRadioButton()),
                zaradaDataValues(getCheckedRadioButton()), ukupnoDataValues(getCheckedRadioButton()));

        return fragmentView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<BarEntry> potrosnjaDataValues(String type) {
        ArrayList<BarEntry> dataVals = new ArrayList<>();
        if (type.equals("day")) {
            for (int i=1; i <=getGraphsPresenter().getDaysOfMonth(); i++) {
                double day = getGraphsPresenter().getDayPayment(i);
                dataVals.add(new BarEntry(i, (float) day));
            }
        } else if (type.equals("week")) {
            for (int i=1; i<=52; i++) {
                double week = getGraphsPresenter().getWeekPayment(i);
                dataVals.add(new BarEntry(i, (float) week));
            }
        }  else {
            for (int i=1; i<=12; i++) {
                double month = getGraphsPresenter().getMonthPayment(i);
                dataVals.add(new BarEntry(i, (float) month));
            }
        }
        return dataVals;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<BarEntry> zaradaDataValues(String type) {
        ArrayList<BarEntry> dataVals = new ArrayList<>();
        if (type.equals("day")) {
            for (int i=1; i<=getGraphsPresenter().getDaysOfMonth(); i++) {
                double day = getGraphsPresenter().getDayIncome(i);
                dataVals.add(new BarEntry(i, (float) day));
            }
        } else if (type.equals("week")) {
            for (int i=1; i<=52; i++) {
                double week = getGraphsPresenter().getWeekIncome(i);
                dataVals.add(new BarEntry(i, (float) week));
            }
        }  else {
            for (int i=1; i<=12; i++) {
                double month = getGraphsPresenter().getMonthIncome(i);
                dataVals.add(new BarEntry(i, (float) month));
            }
        }
        return dataVals;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<BarEntry> ukupnoDataValues(String type) {
        ArrayList<BarEntry> dataVals = new ArrayList<>();
        if (type.equals("day")) {
            for (int i=1; i<=getGraphsPresenter().getDaysOfMonth(); i++) {
                double day = getGraphsPresenter().getDayAllTrans(i);
                dataVals.add(new BarEntry(i, (float) day));
            }
        } else if (type.equals("week")) {
            for (int i=1; i<=52; i++) {
                double week = getGraphsPresenter().getWeekAllTrans(i);
                dataVals.add(new BarEntry(i, (float) week));
            }
        } else {
            for (int i=1; i<=12; i++) {
                double month = getGraphsPresenter().getMonthAllTrans(i);
                dataVals.add(new BarEntry(i, (float) month));
            }
        }
        return dataVals;
    }

    private RadioGroup.OnCheckedChangeListener radioGroupCheckedListener = new RadioGroup.OnCheckedChangeListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            getGraphsPresenter().refreshGraphs(potrosnjaDataValues(getCheckedRadioButton()),
                    zaradaDataValues(getCheckedRadioButton()), ukupnoDataValues(getCheckedRadioButton()));
        }
    };

    @Override
    public void setGraphs(BarDataSet potrosnja, BarDataSet zarada, BarDataSet ukupno) {
        BarData potrosnjaBarData = new BarData();
        potrosnjaBarData.addDataSet(potrosnja);
        Description d = new Description();
        d.setText("");
        paymentBarChart.setDescription(d);
        paymentBarChart.setData(potrosnjaBarData);
        paymentBarChart.invalidate();

        BarData zaradaBarData = new BarData();
        zaradaBarData.addDataSet(zarada);
        incomeBarChart.setDescription(d);
        incomeBarChart.setData(zaradaBarData);
        incomeBarChart.invalidate();

        BarData ukupnoBarData = new BarData();
        ukupnoBarData.addDataSet(ukupno);
        allBarChart.setDescription(d);
        allBarChart.setData(ukupnoBarData);
        allBarChart.invalidate();

    }

    @Override
    public String getCheckedRadioButton() {
        if (radioGroup.getCheckedRadioButtonId() == dayRadioButton.getId()) return "day";
        if (radioGroup.getCheckedRadioButtonId() == weekRadioButton.getId()) return "week";
        return "month";
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
