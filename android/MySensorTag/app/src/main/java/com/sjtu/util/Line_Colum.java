package com.sjtu.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ies.mysensortag.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;


public class Line_Colum extends android.support.v4.app.Fragment {
    private ComboLineColumnChartView chart;
    private ComboLineColumnChartData data;

    private int numberOfLines = 1;
    private int maxNumberOfLines = 1;
    private int numberOfPoints = 12;
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasPoints = true;
    private boolean hasLines = true;
    private boolean isCubic = false;
    private boolean hasLabels = false;

    public Line_Colum() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_line__colum, container, false);

        chart = (ComboLineColumnChartView) rootView.findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        generateValues_mine();
        generateData();

        return rootView;
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_hello_chart, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_reset) {
//            reset();
//            generateData();
//            return true;
//        }
//        if (id == R.id.action_add_line) {
//            addLineToData();
//            return true;
//        }
//        if (id == R.id.action_toggle_lines) {
//            toggleLines();
//            return true;
//        }
//        if (id == R.id.action_toggle_points) {
//            togglePoints();
//            return true;
//        }
//        if (id == R.id.action_toggle_cubic) {
//            toggleCubic();
//            return true;
//        }
//        if (id == R.id.action_toggle_labels) {
//            toggleLabels();
//            return true;
//        }
//        if (id == R.id.action_toggle_axes) {
//            toggleAxes();
//            return true;
//        }
//        if (id == R.id.action_toggle_axes_names) {
//            toggleAxesNames();
//            return true;
//        }
//        if (id == R.id.action_animate) {
//            prepareDataAnimation();
//            chart.startDataAnimation();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    private void generateValues_mine(){
        randomNumbersTab[0][0]= 5.0f;
        randomNumbersTab[0][1]= 5.0f;
        randomNumbersTab[0][2]= 5.0f;
        randomNumbersTab[0][3]= 5.0f;
        randomNumbersTab[0][4]= 5.0f;
        randomNumbersTab[0][5]= 5.0f;
        randomNumbersTab[0][6]= 4.0f;
        randomNumbersTab[0][7]= 4.0f;
        randomNumbersTab[0][8]= 4.0f;
        randomNumbersTab[0][9]= 5.0f;
        randomNumbersTab[0][10]= 6.0f;
        randomNumbersTab[0][11]= 7.0f;

    }
    private void generateValues() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 50f + 5;
            }
        }
    }

    private void reset() {
        numberOfLines = 1;

        hasAxes = true;
        hasAxesNames = true;
        hasLines = true;
        hasPoints = true;
        hasLabels = false;
        isCubic = false;

    }

    private void generateData() {
        // Chart looks the best when line data and column data have similar maximum viewports.
        data = new ComboLineColumnChartData(generateColumnData(), generateLineData());

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("时间(小时)");
                axisY.setName("温度（摄氏度）");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setComboLineColumnChartData(data);
    }

    private LineChartData generateLineData() {

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);
//            line.setColor(ChartUtils.COLORS[i]);
            line.setColor(ChartUtils.COLOR_GREEN);
            line.setCubic(isCubic);
            line.setHasLabels(hasLabels);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            lines.add(line);
        }

        LineChartData lineChartData = new LineChartData(lines);

        return lineChartData;

    }

    private ColumnChartData generateColumnData() {
        int numSubcolumns = 1;
        int numColumns = 12;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(18.0f, ChartUtils.COLOR_BLUE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(19.0f, ChartUtils.COLOR_BLUE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(18.0f, ChartUtils.COLOR_BLUE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(17.0f, ChartUtils.COLOR_BLUE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(16.0f, ChartUtils.COLOR_BLUE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(19.0f, ChartUtils.COLOR_BLUE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(19.0f, ChartUtils.COLOR_BLUE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(5.0f, ChartUtils.COLOR_ORANGE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(6.0f, ChartUtils.COLOR_ORANGE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(7.0f, ChartUtils.COLOR_ORANGE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(6.0f, ChartUtils.COLOR_ORANGE));
        columns.add(new Column(values));
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(9.0f, ChartUtils.COLOR_ORANGE));
        columns.add(new Column(values));

        ColumnChartData columnChartData = new ColumnChartData(columns);
        return columnChartData;
    }

    private void addLineToData() {
        if (data.getLineChartData().getLines().size() >= maxNumberOfLines) {
            //Toast.makeText(getActivity(), "Samples app uses max 4 lines!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            ++numberOfLines;
        }

        generateData();
    }

    private void toggleLines() {
        hasLines = !hasLines;

        generateData();
    }

    private void togglePoints() {
        hasPoints = !hasPoints;

        generateData();
    }

    private void toggleCubic() {
        isCubic = !isCubic;

        generateData();
    }

    private void toggleLabels() {
        hasLabels = !hasLabels;

        generateData();
    }

    private void toggleAxes() {
        hasAxes = !hasAxes;

        generateData();
    }

    private void toggleAxesNames() {
        hasAxesNames = !hasAxesNames;

        generateData();
    }

    private void prepareDataAnimation() {

        // Line animations
        for (Line line : data.getLineChartData().getLines()) {
            for (PointValue value : line.getValues()) {
                // Here I modify target only for Y values but it is OK to modify X targets as well.
                value.setTarget(value.getX(), (float) Math.random() * 50 + 5);
            }
        }

        // Columns animations
        for (Column column : data.getColumnChartData().getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setTarget((float) Math.random() * 50 + 5);
            }
        }
    }
    private class ValueTouchListener implements ComboLineColumnChartOnValueSelectListener {

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            //Toast.makeText(getActivity(), "Selected column: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value) {
            //Toast.makeText(getActivity(), "Selected line point: " + value, Toast.LENGTH_SHORT).show();
        }

    }
}