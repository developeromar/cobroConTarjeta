package com.desarrolladorandroid.cobrocontarjeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu men) {
        getMenuInflater().inflate(R.menu.menuprincipal, men);
        return true;
    }
}
