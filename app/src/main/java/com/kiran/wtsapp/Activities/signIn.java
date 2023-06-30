package com.kiran.wtsapp.Activities;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.kiran.wtsapp.MainActivity;
import com.kiran.wtsapp.ModelClasses.Users;
import com.kiran.wtsapp.R;
import com.kiran.wtsapp.databinding.ActivitySignInBinding;

import java.util.Objects;

public class signIn extends AppCompatActivity {


    ActivitySignInBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setTitle("Signing!");
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


       binding.btnsignIn.setOnClickListener(view -> {
           dialog.show();
           String email = binding.etEmail.getText().toString().trim();
           String password = binding.etPassword.getText().toString().trim();

           if (!email.isEmpty() && !password.isEmpty()) {
               auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                   dialog.dismiss();
                   if (task.isSuccessful()) {
                       startActivity(new Intent(signIn.this, MainActivity.class));
                       finish();
                   } else {
                       Toast.makeText(signIn.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                   }
               });
           } else {
               dialog.dismiss();
               Toast.makeText(signIn.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
           }
       });

       if (auth.getCurrentUser()!=null){
           startActivity(new Intent(signIn.this, MainActivity.class));
           finish();
       }

       binding.btnGoogle1.setOnClickListener(view -> {
           signIn();
       });


        binding.tvSignUp.setOnClickListener(view -> {
            startActivity(new Intent(signIn.this, signUp.class));
            finish();
        });
    }

    int RC_SIGN_IN = 20;
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG","firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());


            }catch (ApiException e){
                Log.w("TAG", "signIn Failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:Success");
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilePic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);


                            startActivity(new Intent(signIn.this, MainActivity.class));
                            finish();
                            Toast.makeText(signIn.this, "Success", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Log.w("TAG", "signInWithCredential:Failed", task.getException());
                        }
                    }
                });
    }
}