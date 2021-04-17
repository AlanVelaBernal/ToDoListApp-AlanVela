package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolistapp.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton plusBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    private EditText titleUpdate;
    private EditText noteUpdate;
    private Button deleteButton;
    private Button updateButton;

    private String title;
    private String note;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar=findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My To Do List");

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId=mUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("ToDoList").child(uId);
        mDatabase.keepSynced(true);

        recyclerView=findViewById(R.id.recycle_view);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        plusBtn = findViewById(R.id.add_button);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myDialog=new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);

                View myView=inflater.inflate(R.layout.input_field, null);

                myDialog.setView(myView);

                AlertDialog dialog = myDialog.create();
                EditText title = myView.findViewById(R.id.title_input);
                EditText note= myView.findViewById(R.id.notes_input);

                Button saveButton = myView.findViewById(R.id.add_button);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mTitle = title.getText().toString().trim();
                        String mNotes = note.getText().toString().trim();

                        if (TextUtils.isEmpty(mTitle)){
                            title.setError("Required Field!");
                            return;
                        }
                        if (TextUtils.isEmpty(mNotes)){
                            title.setError("Required Field!");
                            return;
                        }

                        String mId=mDatabase.push().getKey();
                        String mDate= DateFormat.getDateInstance().format(new Date());

                        Data data = new Data(mTitle, mNotes, mDate, mId);

                        mDatabase.child(mId).setValue(data);

                        Toast.makeText(getApplicationContext(), "Added to list", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });


                dialog.show();

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out_menu_option:
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            break;
        }
        switch (item.getItemId()){
            case R.id.about_menu_option:
                openAboutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openAboutDialog(){
        AboutDialog aboutDialog= new AboutDialog();
        aboutDialog.show(getSupportFragmentManager(), "About");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item,
                        MyViewHolder.class,
                        mDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data data, int i) {

                viewHolder.setTitle(data.getTitle());
                viewHolder.setDate(data.getDate());
                viewHolder.setNote(data.getNote());

                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        id=getRef(i).getKey();
                        title=data.getTitle();
                        note=data.getNote();


                        updateData();
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        public static View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView=itemView;
        }

        public static void setTitle(String title){
            TextView mTitle=myView.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note){
            TextView mNote=myView.findViewById(R.id.notes);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate=myView.findViewById(R.id.date);
            mDate.setText(date);
        }


    }

    public void updateData(){
        AlertDialog.Builder myDialog=new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);

        View myView= inflater.inflate(R.layout.update_entry, null);
        myDialog.setView(myView);

        AlertDialog dialog=myDialog.create();

        titleUpdate=myView.findViewById(R.id.title_input_update);
        noteUpdate=myView.findViewById(R.id.notes_input_update);

        titleUpdate.setText(title);
        titleUpdate.setSelection(title.length());

        noteUpdate.setText(note);
        noteUpdate.setSelection(note.length());

        deleteButton=myView.findViewById(R.id.delete_button);
        updateButton=myView.findViewById(R.id.update_button);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title=titleUpdate.getText().toString().trim();
                note =noteUpdate.getText().toString().trim();

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data = new Data(title, note, mDate, id);
                mDatabase.child(id).setValue(data);
                dialog.dismiss();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(id).removeValue();
                dialog.dismiss();

            }
        });


        dialog.show();

    }
}