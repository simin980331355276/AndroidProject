package com.android.expensesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    androidx.appcompat.widget.Toolbar tb;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Trans,ViewHolder> adapter;
    FirebaseRecyclerOptions<Trans> options;
    FirebaseAuth mFirebaseAuth;
    TextView date;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        tb = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarC);
        date = (TextView) findViewById(R.id.dateTextView);


        setSupportActionBar(tb);
        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        getSupportActionBar().setTitle("Calendar");
        //add back arrow to toolbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //get current user
        String uid=mFirebaseAuth.getInstance().getCurrentUser().getUid();


        //Init database
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        //change path
        mRef = mFirebaseDatabase.getReference("Users").child(uid);

        //RecyclerView
        mRecyclerView = findViewById(R.id.rRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Calendar today = Calendar.getInstance();
        final CalendarView cv = findViewById(R.id.calendar);
        final String todaydate = DateFormat.getDateInstance(DateFormat.FULL).format(today.getTime());
        date.setText(todaydate);

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = DateFormat.getDateInstance(DateFormat.FULL).format(myCalendar.getTime());
                date.setText(myFormat);
                final String currentDate=date.getText().toString();
                showData(currentDate);
            }
        });

    }

    public void showData(final String currentDate) {
        Query dquery = mRef.orderByChild("date").equalTo(currentDate);
        FirebaseRecyclerOptions<Trans>options=new FirebaseRecyclerOptions.Builder<Trans>().setQuery(dquery, Trans.class).build();

        adapter = new FirebaseRecyclerAdapter<Trans, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Trans model) {
                viewHolder.setInfo(getApplicationContext(),model.getCategories(),model.getUserAmount(),model.getAmountType(),model.getDate());
                if(model.getAmountType().equals("pay") || model.getAmountType().equals("Pay")){
                    viewHolder.mAmount.setTextColor(getResources().getColor(R.color.red));
                }
                else if(model.getAmountType().equals("earn") || model.getAmountType().equals("Earn")){
                    viewHolder.mAmount.setTextColor(getResources().getColor(R.color.green));
                }
                else{
                    Toast.makeText(CalendarActivity.this,"Selected",Toast.LENGTH_SHORT).show();
                }

            }
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //initiating layout row.xml
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
                ViewHolder viewHolder = new ViewHolder(itemView);

                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent(view.getContext(), DetailsActivity.class);

                        String xCategory = getItem(position).getCategories();
                        String xAmount = getItem(position).getUserAmount();
                        String xTypes = getItem(position).getAmountType();
                        String xDate = getItem(position).getDate();

                        intent.putExtra("category", xCategory);
                        intent.putExtra("amount", xAmount);
                        intent.putExtra("types", xTypes);
                        intent.putExtra("date", xDate);

                        startActivity(intent);

                    }


                });

                return viewHolder;


            }
        };
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle arrow click
        if (item.getItemId() == android.R.id.home) {
            finish(); //close this activity & return to preview activity
        }
        return super.onOptionsItemSelected(item);
    }


}
