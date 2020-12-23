package com.boostpocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.boostpocket.model.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageCategory extends AppCompatActivity {

    private ImageView saveCategory;
    private DatabaseReference mDatabase;
    private EditText txtCategoryName;
    private RadioGroup radioGroup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        saveCategory = findViewById(R.id.doneViewCategory);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        radioGroup =findViewById(R.id.radioCategoryGroup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        saveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });

    }

    private void saveCategory() {

        String type = findViewById(radioGroup.getCheckedRadioButtonId()).getTag().toString();

        if(type ==null){
            mDatabase.child("Category").setValue(new Category(txtCategoryName.getText().toString(),type,mAuth.getUid()));
            Toast.makeText(ManageCategory.this, "Saved Success",
                    Toast.LENGTH_SHORT).show();
            txtCategoryName.setText("");
        }else{
            Toast.makeText(ManageCategory.this, "Select Category Type",
                    Toast.LENGTH_SHORT).show();
        }
    }
}