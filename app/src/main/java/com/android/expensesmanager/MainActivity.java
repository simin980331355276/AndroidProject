package com.android.expensesmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText amount,types,datepick;
    private Button sendFb;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String textamount,texttypes;
    private Spinner category;
    private FloatingActionButton pay,earn;
    private DatePickerDialog mDatePicker;
    private Calendar mCalendar;
    androidx.appcompat.widget.Toolbar tb;

    int mYear, mMonth, mDay, mHour, mMinute;


    Spinner mSpinner;
    CustomAdapter adapter;
    DatabaseReference mDatabaseReference;

    //---------------Testing spinner with image--------------------------
    //array
    String[] names = {
            "Category","Food","Transport","Medicine","Necessary","Pet"
    };

    int[] images = {
            R.drawable.ic_clipboards,R.drawable.ic_burger,R.drawable.ic_double_decker_bus,R.drawable.ic_doctor,R.drawable.ic_make_up,R.drawable.ic_dog
    };

    //--------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String user_id = mAuth.getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        amount = (EditText) findViewById(R.id.amount);
        types = (EditText) findViewById(R.id.types);
        sendFb = (Button) findViewById(R.id.send);
        mSpinner = (Spinner) findViewById(R.id.vCategories);
        datepick =  findViewById(R.id.display_date);
        adapter = new CustomAdapter(this,names,images);
        tb = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(tb);
        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        getSupportActionBar().setTitle("New Record");
        //add back arrow to toolbar
       if(getSupportActionBar() != null) {
           getSupportActionBar().setDisplayShowHomeEnabled(true);
           getSupportActionBar().setDisplayShowHomeEnabled(true);
       }


        //--------------------Spinner Testing-----------------------------

        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                if(i > 0) {
                    Toast.makeText(getApplicationContext(), names[i], Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });


        //-----------------------------------------------------------------


        //Date
        datepick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                datepicker();
            }

        });





        //button
        sendFb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //passing data from model
                String textamount = amount.getText().toString();
                String texttypes = types.getText().toString();
                String category = mSpinner.getSelectedItem().toString();
                String date =  datepick.getText().toString();

                //error handling
                if (date.isEmpty()) {
                    datepick.setError("Please select the date");
                    datepick.requestFocus();
                } else if (textamount.isEmpty()) {
                    amount.setError("Please enter amount");
                    amount.requestFocus();
                } else if (texttypes.isEmpty()) {
                    types.setError("Please enter the types");
                    types.requestFocus();

                }
                else if (date.isEmpty() && textamount.isEmpty() && texttypes.isEmpty()){
                    Toast.makeText(MainActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
                else {
                    getdata();
                    Toast.makeText(MainActivity.this, "New Record added", Toast.LENGTH_SHORT).show();
                    Intent intToMain = new Intent(MainActivity.this, RetrieveData.class);
                    startActivity(intToMain);
                }

            }
        });





    }


    public void getdata(){
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).push();
        final String uniqid = current_user_db.getKey(); //get unique key of selected data

        //passing data from model
        String textamount = amount.getText().toString();
        String texttypes = types.getText().toString();
        String category = mSpinner.getSelectedItem().toString();
        String date =  datepick.getText().toString();


        if(!TextUtils.isEmpty(textamount) && !TextUtils.isEmpty(texttypes)){

            //put data into firebase

            Map newPost = new HashMap();

            newPost.put("userAmount",textamount);
            newPost.put("amountType",texttypes);
            newPost.put("categories",category);
            newPost.put("date",date);
            newPost.put("Ref",uniqid);

            current_user_db.setValue(newPost);
            amount.setText("");
            types.setText("");
            datepick.setText("");

        }
        else {

            Toast.makeText(MainActivity.this,"Enter Again!.", Toast.LENGTH_SHORT).show();
        }



    }


    //----datepicker function-----//
    public void datepicker(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get((Calendar.YEAR));
        mMonth = c.get((Calendar.MONTH));
        mDay = c.get((Calendar.DAY_OF_MONTH));

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayofMonth){
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DAY_OF_MONTH,dayofMonth);
                String dateformat = java.text.DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());
                datepick.setText(dateformat);
                //datepick.setText(dayofMonth + "/" + (month+1) + "/" + year);
            }
        },mYear,mMonth,mDay);
        dpd.show();


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
