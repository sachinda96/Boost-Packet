package com.boostpocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boostpocket.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private TextView signUpText;
    private ImageView btnSignUp;
    private ProgressBar progressBar;
    private EditText txtEmail,txtPassword;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        signUpText = findViewById(R.id.txtSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.pgrsSignUp);
        txtEmail =findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);



        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {


//                List<String> list = Arrays.asList("Family","Food & Drink","Transport","Vehicle","Leisure","Purchases","Insurance","Holiday");

                List<String> list = Arrays.asList("Salary","Saving");

                for (String e : list) {
                    Category category = new Category();
                    category.setName(e);
                    category.setType("INCOME");
                    category.setUser("all");

                    mDatabase.child("Category").child(UUID.randomUUID().toString()).setValue(category);
                }


                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {

                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                startActivity(new Intent(getApplicationContext(),Dashboard.class));
//                progressBar.setVisibility(View.VISIBLE);
//
//                mAuth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    progressBar.setVisibility(View.INVISIBLE);
//                                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
//                                    Toast.makeText(MainActivity.this, "Authentication Passed",
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    progressBar.setVisibility(View.INVISIBLE);
//                                    Toast.makeText(MainActivity.this, "Authentication failed.Check Email and Password",
//                                            Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//                        });

            }
        });


    }
}