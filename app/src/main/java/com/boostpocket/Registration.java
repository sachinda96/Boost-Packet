package com.boostpocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Registration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText txtEmail,txtPassword;
    private ImageView btnSignUp;
    private TextView txtSignIn;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private Map<String, Object> entry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        txtEmail =findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnSignUp =findViewById(R.id.btnSignUp);
        progressBar= findViewById(R.id.pgrsSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");


        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Registration.this, (OnCompleteListener<AuthResult>) task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Registration.this, "Registration Success.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(Registration.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();

                            }
                        });




            }
        });


    }


}