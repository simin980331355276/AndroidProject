package com.android.expensesmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class EditFragment extends Fragment {
    EditText sdate,samount,stype,scategories,datepick;
    FloatingActionButton confirm;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference mDatabaseReference;

    int mYear, mMonth, mDay, mHour, mMinute;

    Spinner mSpinner;
    CustomAdapter adapter;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_edit, container, false);

        sdate = (EditText) v.findViewById(R.id.vDate);
        samount = (EditText) v.findViewById(R.id.vAmount);
        //scategories = (EditText) v.findViewById(R.id.vCategory);
        mSpinner = (Spinner) v.findViewById(R.id.vCategories2);
        stype = (EditText) v.findViewById(R.id.vType);
        confirm = (FloatingActionButton) v.findViewById(R.id.update);
        mSpinner = (Spinner) v.findViewById(R.id.vCategories2);
        adapter = new CustomAdapter(getActivity(),names,images);
        //mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();



        //get the data from intent
        Intent i = getActivity().getIntent();
        String d = getActivity().getIntent().getStringExtra("date");
        String a = getActivity().getIntent().getStringExtra("amount");
        String t = getActivity().getIntent().getStringExtra("types");
        String c = getActivity().getIntent().getStringExtra("category");
        final String taid = getActivity().getIntent().getStringExtra("tid");

        //display on text views
        sdate.setText(d);
        samount.setText(a);
        stype.setText(t);
        //mSpinner.setSelection(Integer.parseInt(c));
        //scategories.setText(c);

        //Date
        sdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                datepicker();
            }

        });

        //--------------------Spinner Testing-----------------------------

        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                if(i > 0) {
                    Toast.makeText(getContext(), names[i], Toast.LENGTH_LONG).show();
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });


        //-----------------------------------------------------------------



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String odate = sdate.getText().toString();
                final String oamount = samount.getText().toString();
                final String otype = stype.getText().toString();
                final String ocategory = mSpinner.getSelectedItem().toString();
                //error handling
                if (odate.isEmpty()) {
                    sdate.setError("Please select the date");
                    sdate.requestFocus();
                } else if (oamount.isEmpty()) {
                    samount.setError("Please enter amount");
                    samount.requestFocus();
                } else if (otype.isEmpty()) {
                    stype.setError("Please enter the types");
                    stype.requestFocus();

                }
                else if (odate.isEmpty() && oamount.isEmpty() && otype.isEmpty()){
                    Toast.makeText(getActivity(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                }

                //get current user
                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                //compare the
                Query query = current_user_db.orderByChild("Ref").equalTo(taid);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){

                            ds.getRef().child("amountType").setValue(otype);
                            ds.getRef().child("categories").setValue(ocategory);
                            ds.getRef().child("date").setValue(odate);
                            ds.getRef().child("userAmount").setValue(oamount);
                        }
                        Toast.makeText(getActivity(),"Updated",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getActivity(), RetrieveData.class);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }
        });
        return v;


    }

    //----datepicker function-----//
    public void datepicker(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get((Calendar.YEAR));
        mMonth = c.get((Calendar.MONTH));
        mDay = c.get((Calendar.DAY_OF_MONTH));

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayofMonth){
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DAY_OF_MONTH,dayofMonth);
                String dateformat = java.text.DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());
                sdate.setText(dateformat);
                //datepick.setText(dayofMonth + "/" + (month+1) + "/" + year);
            }
        },mYear,mMonth,mDay);
        dpd.show();

    }


}
