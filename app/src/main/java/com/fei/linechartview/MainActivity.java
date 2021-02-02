package com.fei.linechartview;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LineChartView lineChartView = findViewById(R.id.line_chart_view);

        //测试数据
        final List mChartDatas = new ArrayList<>();
        ChartData chartData = new ChartData("2020-01-10", "1.0");
        ChartData chartData1 = new ChartData("2020-01-11", "11.0");
        ChartData chartData2 = new ChartData("2020-01-12", "9.0");
        ChartData chartData3 = new ChartData("2020-01-13", "30.0");
        ChartData chartData4 = new ChartData("2020-01-14", "42.0");
        ChartData chartData5 = new ChartData("2020-01-15", "25.0");

        mChartDatas.add(chartData);
        mChartDatas.add(chartData1);
        mChartDatas.add(chartData2);
        mChartDatas.add(chartData3);
        mChartDatas.add(chartData4);
        mChartDatas.add(chartData5);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lineChartView.setData(mChartDatas);
            }
        },5000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity","onResume");
    }
}