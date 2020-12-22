package com.boostpocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.boostpocket.model.Category;
import com.boostpocket.model.IncomeExpenses;
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

public class IncomeExpensess extends AppCompatActivity {

    private RadioGroup radioGroup;
    private Button btnIncome,btnExpenses,btnSave;
    private EditText txtAmount;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private DatabaseReference databaseReference;
    private Map<String, Object> entry = null;
    private String type="INCOME";

    private Map<String, Object> userIncome = null;
    private List<IncomeExpenses> incomeExpenses = null;

    public IncomeExpensess() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_expensess);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        radioGroup = findViewById(R.id.radioCategoryGroup);
        btnIncome =findViewById(R.id.btnIncome);
        btnExpenses =findViewById(R.id.btnExpensess);
        btnSave = findViewById(R.id.btnSave);
        txtAmount = findViewById(R.id.txtAmount);

        Query query = FirebaseDatabase.getInstance().getReference("User Income").orderByChild("user").equalTo(mAuth.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIncome = (Map<String, Object>) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                type = "INCOME";
                getCategoryData();
            }
        });

        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                type = "EXPENSES";
                getCategoryData();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {

                if(type.equalsIgnoreCase("INCOME")){
                    saveIncome();
                }else if(type.equalsIgnoreCase("EXPENSES")){
                    saveExpenses();
                }

            }
        });



    }

    private void saveExpenses() {

        String id = findViewById(radioGroup.getCheckedRadioButtonId()).getTag().toString();

        if(id != null){

            String amount =txtAmount.getText().toString();
            setIncomeExpenses(userIncome,amount,id);



            Toast.makeText(IncomeExpensess.this, "Successful saved expenses",
                    Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(IncomeExpensess.this, "Select Category",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void saveIncome() {

        String id = findViewById(radioGroup.getCheckedRadioButtonId()).getTag().toString();

        if(id != null){

            mDatabase.child("User Income").child(UUID.randomUUID().toString()).setValue(new UserIncome(mAuth.getUid(),id,txtAmount.getText().toString(),formatDate(new Date()),txtAmount.getText().toString()));
            Toast.makeText(IncomeExpensess.this, "Successful saved new income",
                    Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(IncomeExpensess.this, "Select Category",
                    Toast.LENGTH_SHORT).show();
        }


    }

    private void getCategoryData(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                entry = (Map<String, Object>) snapshot.getValue();
                firebaseListConverter(entry);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IncomeExpensess.this, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void firebaseListConverter(Map<String, Object> value) {

        Integer seq = 0;

        for (Map.Entry<String, Object> entry : value.entrySet()){

            Category category = setCategory((Map) entry.getValue());

            if(category.getType().equals(type) && (category.getUser().equals("all") || category.getUser().equals(mAuth.getUid()))){

                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(category.getName());
                radioButton.setId(seq);
                radioButton.setTag(entry.getKey());
                seq = seq+1;

                radioGroup.addView(radioButton);

            }

        }
    }

    private Category setCategory(Map map){

        Category category =new Category();
        category.setIcon(map.get("icon").toString());
        category.setType(map.get("type").toString());
        category.setName(map.get("name").toString());
        category.setUser(map.get("user").toString());
        return category;
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

    private String formatDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);

    }

    private void setIncomeExpenses(Map<String, Object> value , String amount,String categoryId){

        String expensesId = UUID.randomUUID().toString();

        mDatabase.child("User Expenses").child(expensesId).setValue(new UserExpenses(mAuth.getUid(),categoryId,formatDate(new Date())
                            ,amount));

        incomeExpenses= new ArrayList<>();
        for (Map.Entry<String, Object> income : value.entrySet()) {

            UserIncome userIncome = setUserIncome((Map) income.getValue());
            if (new BigDecimal(userIncome.getBalance()).compareTo(new BigDecimal(0)) == 1) {

                if (new BigDecimal(userIncome.getBalance()).compareTo(new BigDecimal(amount)) == -1) {
                    amount = new BigDecimal(amount).subtract(new BigDecimal(userIncome.getBalance())).setScale(2).toString();
                    userIncome.setBalance("0");
                }else{
                    String currentValue = new BigDecimal(userIncome.getBalance()).setScale(2).subtract(new BigDecimal(amount).setScale(2)).setScale(2).toString();
                    System.out.println("currentValue "+currentValue);
                    userIncome.setBalance(currentValue);

                }
                mDatabase.child("User Income").child(income.getKey()).setValue(userIncome);

                incomeExpenses.add(new IncomeExpenses(income.getKey(),expensesId,amount,formatDate(new Date())));
            }
        }

        for (IncomeExpenses incomeExpenses : incomeExpenses) {
            mDatabase.child("Income Expenses").child(UUID.randomUUID().toString()).setValue(incomeExpenses);
        }

    }
}