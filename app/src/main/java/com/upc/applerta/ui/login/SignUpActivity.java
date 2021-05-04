package com.upc.applerta.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.upc.applerta.MainActivity;
import com.upc.applerta.R;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar loadingProgressBar = null;
    private Button signUpButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        final EditText usernameEditText = findViewById(R.id.txtSignUpUsername);
        final EditText passwordEditText = findViewById(R.id.txtSignUpPassword);
        final EditText confirmPasswordEditText = findViewById(R.id.txtSignUpConfirmPassword);
        final EditText fullnameEditText = findViewById(R.id.txtFullname);
        signUpButton = findViewById(R.id.btnSignUp);
        loadingProgressBar = findViewById(R.id.signUpLoading);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                signup(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        confirmPasswordEditText.getText().toString(),
                        fullnameEditText.getText().toString());
            }
        });
    }

    private void signup(String usernameEditText, String passwordEditText, String confirmPasswordEditText, String fullnameText) {

        if(usernameEditText.equals("") || passwordEditText.equals("") || confirmPasswordEditText.equals("")|| fullnameText.equals("")){
            Toast.makeText(SignUpActivity.this,"Ingresar los datos obligatorios",Toast.LENGTH_SHORT).show();
            loadingProgressBar.setVisibility(View.GONE);
        }
        else if(!passwordEditText.equals(confirmPasswordEditText)){
            Toast.makeText(SignUpActivity.this,"Las contraseñas ingresadas no coinciden",Toast.LENGTH_SHORT).show();
            loadingProgressBar.setVisibility(View.GONE);
        }
        else {
            mAuth.createUserWithEmailAndPassword(usernameEditText, passwordEditText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()) {

                                FirebaseUser user = task.getResult().getUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullnameText)
                                        .build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registro de usuario fallido: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            loadingProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }
}