package com.example.canteen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class WelComeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel_come);
        SharedPreferences sp = getSharedPreferences("Logindb", MODE_PRIVATE);
        if(sp.getString("type","").equals("")||sp.getString("token","").equals("")){
           startActivity(new Intent(this,LoginActivity.class));
        }else{
            Api.TYPE = sp.getString("type","");
            Api.TOKEN = sp.getString("token","");
            startActivity(new Intent(this,MainActivity.class));
        }
        finish();
    }
}
