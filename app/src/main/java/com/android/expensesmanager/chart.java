package com.android.expensesmanager;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class chart extends AppCompatActivity {

    PieChart piechart;
    androidx.appcompat.widget.Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        piechart = (PieChart) findViewById(R.id.linechart);
        piechart.setHoleRadius(35f);
        piechart.setTransparentCircleRadius(35f);

        tb = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(tb);
        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        getSupportActionBar().setTitle("Statistic");
        //add back arrow to toolbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(12f,"Food"));
        value.add(new PieEntry(30f,"Transport"));
        value.add(new PieEntry(50f,"Gift"));
        value.add(new PieEntry(10f,"Necessary"));

        PieDataSet pieDataSet = new PieDataSet(value,"Months");
        PieData pieData = new PieData(pieDataSet);
        piechart.setData(pieData);

        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        piechart.animateXY(1400,1400);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //handle arrow click
        if(item.getItemId() == android.R.id.home){
            finish(); //close this activity & return to preview activity
        }
        return super.onOptionsItemSelected(item);
    }


}
