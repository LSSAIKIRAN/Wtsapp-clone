package com.kiran.wtsapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.kiran.wtsapp.MainActivity;
import com.kiran.wtsapp.ModelClasses.Users;
import com.kiran.wtsapp.R;
import com.kiran.wtsapp.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class signUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setTitle("SigningUp");
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);




        binding.btnSignUp.setOnClickListener(view -> {
            dialog.show();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String username = binding.etUserName.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty() && !username.isEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                String id = Objects.requireNonNull(task.getResult()).getUser().getUid();
                                Users user = new Users(username, email, password);
                                database.getReference().child("Users").child(id).setValue(user);
                                Toast.makeText(signUp.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(signUp.this, MainActivity.class));
                                finish();
                            }
                            else {
                                dialog.dismiss();
                                Toast.makeText(signUp.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                dialog.dismiss();
                Toast.makeText(signUp.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
            }
        });


        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(signUp.this, MainActivity.class));
            finish();
        }



        binding.tvSignIn.setOnClickListener(view -> {
            startActivity(new Intent(signUp.this, signIn.class));
            finish();
        });

    }


}