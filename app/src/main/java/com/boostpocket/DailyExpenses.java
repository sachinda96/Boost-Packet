package com.boostpocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.boostpocket.model.Category;
import com.boostpocket.model.CategoryExpenses;
import com.boostpocket.model.UserExpenses;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DailyExpenses extends AppCompatActivity {

    private ImageView viewBack,doneView;
    private TextView txtTitle;
    private CalendarView calendarView;
    private ScrollView scroller;
    private LinearLayout linearLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Map<String, Object> categoryList=null;
    private List<CategoryExpenses> categoryExpensesList;
    private Map<String,Object> dailyExpenses;

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_expenses);

        viewBack = findViewById(R.id.btnviewback);
        txtTitle = findViewById(R.id.txtTitle);
        doneView = findViewById(R.id.doneView);
        calendarView = findViewById(R.id.calendarView);
        scroller = findViewById(R.id.scrollMain);
        linearLayout = findViewById(R.id.layoutExpenses);

        scroller.setVisibility(View.INVISIBLE);

        setAllCategory();

        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        });


        doneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroller.setVisibility(View.VISIBLE);
                calendarView.setVisibility(View.INVISIBLE);
                doneView.setVisibility(View.INVISIBLE);
                txtTitle.setText("Selected Date Expenses");
                getDailyExpenses();
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                int currentMonth =month+1;
                date=year+"-"+currentMonth+"-"+day;
                System.out.println(date);
            }
        });
    }





    private void setAllCategory() {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Category");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList = (Map<String, Object>) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyExpenses.this, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void getDailyExpenses(){

        categoryExpensesList =new ArrayList<>();
        linearLayout.removeAllViews();

        if(date == null){
            date = formatDate(new Date());
        }


        Query query = FirebaseDatabase.getInstance().getReference("User Expenses").orderByChild("date").equalTo(date);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dailyExpenses = (Map<String, Object>) snapshot.getValue();

                if(dailyExpenses!=null){
                    for (Map.Entry<String, Object> dailyExpenses : dailyExpenses.entrySet()) {

                        boolean isNew =true;
                        UserExpenses userExp = setUserExpenses((Map) dailyExpenses.getValue());

                        if(!categoryExpensesList.isEmpty()){
                            for (CategoryExpenses categoryExpenses : categoryExpensesList) {
                                if(categoryExpenses.getCategoryId().equalsIgnoreCase(userExp.getCategoryId())){
                                    categoryExpenses.setAmount(new BigDecimal(categoryExpenses.getAmount()).add(new BigDecimal(userExp.getAmount())).setScale(2).toString());
                                    categoryExpensesList.set(categoryExpensesList.indexOf(categoryExpenses),categoryExpenses);
                                    isNew =false;
                                }
                            }
                        }

                        if(isNew){
                            categoryExpensesList.add(setCategoryExpenses(userExp));
                        }

                    }


                    if(!categoryExpensesList.isEmpty()){

                        String dailyTotalExpenses="0.00";

                        for (CategoryExpenses categoryExpenses : categoryExpensesList) {

                            dailyTotalExpenses =new BigDecimal(dailyTotalExpenses).add(new BigDecimal(categoryExpenses.getAmount())).setScale(2).toString();
                            TextView first = new TextView(getApplicationContext());
                            first.setLayoutParams(linearLayout.getLayoutParams());
                            first.setTextColor(Color.parseColor("#FF6761"));
                            first.setTextSize(20);
                            first.setTypeface(first.getTypeface(), Typeface.BOLD);
                            //first.setGravity(Gravity.CENTER);
                            first.setPadding(30, 0, 5, 5);
                            first.setText(categoryExpenses.getName()+" - "+ "Rs."+categoryExpenses.getAmount());
                            linearLayout.addView(first);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyExpenses.this, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String formatDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);

    }

    private UserExpenses setUserExpenses(Map map){
        return new UserExpenses(map.get("user").toString(),map.get("categoryId").toString()
                ,map.get("date").toString(),map.get("amount").toString());

    }

    private CategoryExpenses setCategoryExpenses(UserExpenses userExpenses){

        Category category = getCategory(userExpenses.getCategoryId());

        CategoryExpenses categoryExpenses = new CategoryExpenses();
        categoryExpenses.setAmount(userExpenses.getAmount());
        categoryExpenses.setCategoryId(userExpenses.getCategoryId());
        categoryExpenses.setName(category.getName());

        return categoryExpenses;
    }


    private Category getCategory(String categoryId){

        for (Map.Entry<String,Object> mapCategory : categoryList.entrySet()) {

            if(categoryId.equalsIgnoreCase(mapCategory.getKey())){
                return setCategory((Map) mapCategory.getValue());
            }


        }

        return null;

    }

    private Category setCategory(Map map){

        Category category =new Category();
        category.setType(map.get("type").toString());
        category.setName(map.get("name").toString());
        category.setUser(map.get("user").toString());
        return category;
    }

}