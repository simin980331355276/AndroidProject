package com.android.expensesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Map;

public class RetrieveData extends AppCompatActivity {


    Button btnLogOut;
    TextView ttlPay,ttlEarn;
    FloatingActionButton btnAdd;
    RecyclerView mRecyclerView;
    androidx.appcompat.widget.Toolbar tb;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Trans,ViewHolder> adapter;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_data);

        btnAdd = (FloatingActionButton) findViewById(R.id.add);
        ttlEarn = (TextView) findViewById(R.id.tvAmount);
        ttlPay = (TextView) findViewById(R.id.tvAmount2);
        tb = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar3);

        //toolbar
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Welcome back!");

        //get current user
        String uid=mFirebaseAuth.getInstance().getCurrentUser().getUid();

        //Init database
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        //change path
        mRef = mFirebaseDatabase.getReference("Users").child(uid);

        //RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadList();
    }
    //load data into recyclerview
    private void loadList(){
        //
        FirebaseRecyclerOptions<Trans> options = new FirebaseRecyclerOptions.Builder<Trans>().setQuery(mRef,Trans.class).build();
        //get adapter
        adapter=new FirebaseRecyclerAdapter<Trans, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);

                final ViewHolder viewHolder = new ViewHolder(itemView);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //using intent to pass the details to Details.Activity
                        Intent intent = new Intent(view.getContext(), DetailsActivity.class);

                        String taskid=getRef(position).getKey();//get unique key
                        String xCategory = getItem(position).getCategories();
                        String xAmount = getItem(position).getUserAmount();
                        String xTypes = getItem(position).getAmountType();
                        String xDate = getItem(position).getDate();

                        //add extended data to intent
                        //1 parameter specify which extra data, another parameter is data itself
                        intent.putExtra("tid",taskid);
                        intent.putExtra("category", xCategory);
                        intent.putExtra("amount", xAmount);
                        intent.putExtra("types", xTypes);
                        intent.putExtra("date", xDate);
                        startActivity(intent);

                    }

                });

                return viewHolder;

            }


            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i, @NonNull Trans model) {
                viewHolder.mCategory.setText(""+model.getCategories());
                viewHolder.mAmount.setText("$ "+model.getUserAmount());
                viewHolder.mTypes.setText("Type: "+model.getAmountType());
                viewHolder.mDate.setText(""+model.getDate());

                if(model.getAmountType().equals("pay") || model.getAmountType().equals("Pay")){
                    viewHolder.mAmount.setTextColor(getResources().getColor(R.color.red));

                }
                else if(model.getAmountType().equals("earn") || model.getAmountType().equals("Earn")){
                    viewHolder.mAmount.setTextColor(getResources().getColor(R.color.green));
                }
                else{
                    Toast.makeText(RetrieveData.this,"New Record added",Toast.LENGTH_SHORT).show();
                }


            }


        };




        adapter.startListening();
        mRecyclerView.setAdapter(adapter);


        //add new record button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(RetrieveData.this, MainActivity.class));
            }
        });

        /////////////////////////////////////////////////////////////////////
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnAdd.getVisibility() == View.VISIBLE) {
                    btnAdd.hide();
                } else if (dy < 0 && btnAdd.getVisibility() != View.VISIBLE) {
                    btnAdd.show();
                }
            }
        });
        ////////////////////////////////////////////////////////////////////

        ///data snapshot getTotalEarn and pay
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float sumP=0;
                float sumE=0;

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Map<Integer,Object> map = (Map<Integer,Object>) ds.getValue();

                    if(map.get("amountType").equals("earn") || map.get("amountType").equals("Earn")){
                        Object tlamount = map.get("userAmount");
                        float pValue = Float.parseFloat(String.valueOf(tlamount));
                        sumP += pValue;

                        //get .2f for the output
                        NumberFormat formatter = NumberFormat.getNumberInstance(); //get .2f for the output
                        formatter.setMinimumFractionDigits(2);
                        formatter.setMaximumFractionDigits(2);
                        String outputP = formatter.format(sumP);

                        ttlEarn.setText("$ "+String.valueOf(outputP));
                    }
                    else if(map.get("amountType").equals("pay") || map.get("amountType").equals("Pay")){
                        Object tlamount = map.get("userAmount");
                        float pValue = Float.parseFloat(String.valueOf(tlamount));
                        sumE += pValue;

                        NumberFormat formatter = NumberFormat.getNumberInstance();
                        formatter.setMinimumFractionDigits(2);
                        formatter.setMaximumFractionDigits(2);
                        String outputE = formatter.format(sumE);

                        ttlPay.setText("$ "+String.valueOf(outputE));

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    protected void onResume(){
        super.onResume();
        adapter.startListening();
    }


    //toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_retrieve,menu);
        // To display icon on overflow menu
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        //handle arrow click

        switch(item.getItemId()){
            case R.id.statistic:
                Intent chart = new Intent(RetrieveData.this, chart.class);
                startActivity(chart);
                break;
            case R.id.logout:
                logout();  //show message
                break;
            case R.id.calendar:
                Intent calendar = new Intent(RetrieveData.this, CalendarActivity.class);
                startActivity(calendar);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    //logout
    private void logout(){

        FirebaseAuth.getInstance().signOut();
        Intent intToMain = new Intent(RetrieveData.this, LoginActivity.class);
        startActivity(intToMain);

    }





}
