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
        Myrunable myrunable = new Myrunable();
        Thread mt11 = new Thread(myrunable);
        mt11.start();

    }

    class Myrunable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);//卖票速度是1s一张
                SharedPreferences sp = getSharedPreferences("Logindb", MODE_PRIVATE);
                if(sp.getString("type","").equals("")||sp.getString("token","").equals("")){
                    startActivity(new Intent(WelComeActivity.this,LoginActivity.class));
                }else{
                    Api.TYPE = sp.getString("type","");
                    Api.TOKEN = sp.getString("token","");
                    startActivity(new Intent(WelComeActivity.this,MainActivity.class));
                }
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
}
