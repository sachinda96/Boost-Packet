package com.boostpocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.boostpocket.model.Category;
import com.boostpocket.model.CategoryExpenses;
import com.boostpocket.model.UserExpenses;
import com.boostpocket.model.UserIncome;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Dashboard extends AppCompatActivity {

    private Button addButton;
    private TextView txtDay,txtTotalIncome,txtTotalExpenses;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Map<String,Object> userIncome;
    private Map<String,Object> userExpenses;
    private Map<String,Object> dailyExpenses;
    private List<CategoryExpenses> categoryExpensesList;
    private Category category= null;
    Map<String, Object> categoryList=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        addButton = findViewById(R.id.btnAdd);
        txtDay =findViewById(R.id.txtDay);
        txtTotalIncome =findViewById(R.id.txtTotIncome);
        txtTotalExpenses =findViewById(R.id.txtTotExpenses);

        txtDay.setText(getDateShortName(new SimpleDateFormat("EEEE").format(new Date()).toUpperCase()));

        setAllCategory();
        getAllUserIncome();
        getAllUserExpenses();
        getDailyExpenses();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                startActivity(new Intent(getApplicationContext(), IncomeExpensess.class));
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
                Toast.makeText(Dashboard.this, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
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

    private void getAllUserIncome(){

        Query query = FirebaseDatabase.getInstance().getReference("User Income").orderByChild("user").equalTo(mAuth.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIncome = (Map<String, Object>) snapshot.getValue();

                String totalIncome = "0.00";
                for (Map.Entry<String, Object> income : userIncome.entrySet()) {
                    UserIncome userIncome = setUserIncome((Map) income.getValue());
                    totalIncome = new BigDecimal(totalIncome).add(new BigDecimal(userIncome.getAmount())).setScale(2).toString();
                }
                txtTotalIncome.setText(totalIncome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getAllUserExpenses() {

        Query query = FirebaseDatabase.getInstance().getReference("User Expenses").orderByChild("user").equalTo(mAuth.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userExpenses= (Map<String, Object>) snapshot.getValue();

                String totalExpenses = "0.00";
                for (Map.Entry<String, Object> income : userExpenses.entrySet()) {
                    UserExpenses userExpenses = setUserExpenses((Map) income.getValue());
                    totalExpenses = new BigDecimal(totalExpenses).add(new BigDecimal(userExpenses.getAmount())).setScale(2).toString();
                }
                txtTotalExpenses.setText(totalExpenses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getDailyExpenses(){

        categoryExpensesList =new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference("User Expenses").orderByChild("date").equalTo(formatDate(new Date()));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dailyExpenses = (Map<String, Object>) snapshot.getValue();

                for (Map.Entry<String, Object> dailyExpenses : dailyExpenses.entrySet()) {

                    System.out.println("work 2 ");
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

                    List<HashMap<String,String>> listData= new ArrayList<>();

                    for (CategoryExpenses categoryExpenses : categoryExpensesList) {
                        HashMap<String,String> map = new HashMap<>();
                        map.put("ListTitle", categoryExpenses.getName());
                        map.put("ListDescription", categoryExpenses.getAmount());
                        listData.add(map);
                    }

                }


                System.out.println("size : "+categoryExpensesList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private UserIncome setUserIncome(Map map){
        UserIncome userIncome = new UserIncome();
        userIncome.setAmount(map.get("amount").toString());
        userIncome.setBalance(map.get("balance").toString());
        userIncome.setCategoryId(map.get("categoryId").toString());
        userIncome.setDate(map.get("date").toString());
        userIncome.setUser(map.get("user").toString());
        return userIncome;
    }

    private UserExpenses setUserExpenses(Map map){
       return new UserExpenses(map.get("user").toString(),map.get("categoryId").toString()
               ,map.get("date").toString(),map.get("amount").toString());

    }

    private String formatDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);

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
        category.setIcon(map.get("icon").toString());
        category.setType(map.get("type").toString());
        category.setName(map.get("name").toString());
        category.setUser(map.get("user").toString());
        return category;
    }

}