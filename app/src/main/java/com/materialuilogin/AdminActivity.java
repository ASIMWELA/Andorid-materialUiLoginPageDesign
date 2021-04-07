package com.materialuilogin;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class AdminActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent getAdmin = getIntent();
        String admin =  getAdmin.getStringExtra("admin");

        System.out.println(admin);
    }
}