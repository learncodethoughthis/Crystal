package com.example.crystal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.widget.Toolbar;


public class RequestActivity extends AppCompatActivity {

    EditText userPort,userName;
    Button hostBtn;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        toolbar= findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crystal");
        hostBtn= findViewById(R.id.hostButton);
        userPort= findViewById(R.id.portEditText);
        userName=findViewById(R.id.userNameEditText);
        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username= userName.getText().toString();
                final String userport= userPort.getText().toString();
                if(username.isEmpty() || userport.isEmpty()){
                    showMessage("Please Vertified All Field");
                }else{
                    Intent mainActivity= new Intent(RequestActivity.this, MainActivity.class);
                    getIntent().putExtra("userName",username);
                    getIntent().putExtra("userPort",userport);
                    startActivity(mainActivity);
                    finish();
                }
            }
        });
    }

    private void showMessage(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }









}
