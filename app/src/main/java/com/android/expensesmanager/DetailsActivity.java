package com.android.expensesmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {
    private TextView sdate,samount,scategory,stype;
    androidx.appcompat.widget.Toolbar tb;
    RecyclerView mRecyclerView;
    FloatingActionButton edit;
    Fragment fragmentObject;


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Trans,ViewHolder> adapter;
    FirebaseAuth mFirebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        sdate = (TextView) findViewById(R.id.vDate);
        samount = (TextView) findViewById(R.id.vAmount);
        scategory = (TextView) findViewById(R.id.vCategory);
        stype = (TextView) findViewById(R.id.vType);
        tb = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar2);
        edit = (FloatingActionButton) findViewById(R.id.update);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //get current user
        String user_id = mFirebaseAuth.getCurrentUser().getUid();
        mRef = mFirebaseDatabase.getReference("Users").child(user_id);


        //toolbar
        setSupportActionBar(tb);
        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        getSupportActionBar().setTitle("Details");
        //add back arrow to toolbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //fab
        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Fragment edit = new EditFragment();
                replacefagment(edit);

                }
        });

        //get the data from intent
        Intent intent = getIntent();
        String d = getIntent().getStringExtra("date");
        String a = getIntent().getStringExtra("amount");
        String t = getIntent().getStringExtra("types");
        String c = getIntent().getStringExtra("category");
        String tid=getIntent().getStringExtra("tid");

        //display on text views
        sdate.setText(d);
        samount.setText("$"+a);
        stype.setText(t);
        scategory.setText(c);



    }



    public void replacefagment(Fragment destFragment){
        // First get FragmentManager object.
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        // Begin Fragment transaction.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the layout holder with the required Fragment object.
        fragmentTransaction.replace(R.id.container, destFragment);

        // Commit the Fragment replace action.
        fragmentTransaction.commit();
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //handle arrow click
        switch(item.getItemId()){
            //if user click on the back arrow icon
            case android.R.id.home:
                finish();  //close this activity & return to preview activity
            break;
            //if user click on the dustbin icon
            case R.id.delete:

                //get data from intent (RetrieveData.java)
                Intent intent = getIntent();
                String d = getIntent().getStringExtra("date");
                String a = getIntent().getStringExtra("amount");
                String t = getIntent().getStringExtra("types");
                String c = getIntent().getStringExtra("category");
                String tid=getIntent().getStringExtra("tid");
                //call the function
                showDeleteDialog(d,a,t,c,tid);

            break;
        }
        return super.onOptionsItemSelected(item);
    }




    private void showDeleteDialog(String d, String a, String t, String c, final String tid){

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setTitle("Delete");
        builder.setTitle("Are you sure you want to delete?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //compare the "Ref" vslue from firebase and also the id of selected data
                mRef.orderByChild("Ref").equalTo(tid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postsnapshot:dataSnapshot.getChildren()){
                            postsnapshot.getRef().removeValue(); //remove value from firebase
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Toast.makeText(DetailsActivity.this,"Deleted", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(DetailsActivity.this, RetrieveData.class);
                startActivity(i);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }




    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toobar_menu,menu);
        return  true;
    }



    }



