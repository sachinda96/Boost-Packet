package com.boostpocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dashboard extends AppCompatActivity {

    private Button addButton;
    private TextView txtDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        addButton = findViewById(R.id.btnAdd);
        txtDay =findViewById(R.id.txtDay);

        txtDay.setText(getDateShortName(new SimpleDateFormat("EEEE").format(new Date()).toUpperCase()));


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                startActivity(new Intent(getApplicationContext(), IncomeExpensess.class));
            }
        });
    }

    private String getDateShortName(String day){

        switch(day){
            case "MONDAY":
                return "MN";
            case "TUESDAY":
                return  "TU";
            case "WEDNESDAY":
                return  "WE";
            case "THURSDAY":
                return  "TH";
            case "FRIDAY":
                return  "FR";
            case "SATURDAY":
                return  "ST";
            case "SUNDAY":
                return  "SU";
            default:
                return "DY";
        }

    }
}