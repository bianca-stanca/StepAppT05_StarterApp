package com.example.stepapp.ui.report;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class DayFragment extends Fragment {

    AnyChartView anyChartView;
    public Map<String, Integer> stepsByDay = null;

    Date cDate = new Date();
    String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        Log.d("date", current_time);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_day, container, false);

        anyChartView = root.findViewById(R.id.hourBarChart);
        anyChartView.setProgressBar(root.findViewById(R.id.loadingBar));

        Cartesian cartesian = createColumnChart();
        anyChartView.setBackgroundColor("#00000000");
        anyChartView.setChart(cartesian);
        return root;
    }

    private Cartesian createColumnChart() {
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(this.getContext());
        stepsByDay = databaseHelper.loadStepsByDay(this.getContext());

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();

        TreeMap<String, Integer> graph_map = new TreeMap<>();
        for(int i=3; i >= 0; i--){
            String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(cDate.getTime() - TimeUnit.DAYS.toMillis(i)));
            graph_map.put(time, 0);
        }
        graph_map.putAll(stepsByDay);
        for (Map.Entry<String,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        Column column = cartesian.column(data);

        column.fill("#1EB980");
        column.stroke("#1EB980");

        column.tooltip()
                .titleFormat("On day: {%X}")
                .format("{%Value}{groupsSeparator: } Steps")
                .anchor(Anchor.RIGHT_TOP);

        column.tooltip().position(Position.RIGHT_TOP).offsetX(0d).offsetY(5);


        // Modifying properties of cartesian
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.yScale().minimum(0);

        cartesian.background().fill("#00000000");
        cartesian.yAxis(0).title("Number of steps");
        cartesian.xAxis(0).title("Hours");
        cartesian.animation(true);
        return cartesian;
    }

}