package com.upc.applerta.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.upc.applerta.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar loadingProgressBar = null;
    private Button forgotButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        final EditText usernameEditText = findViewById(R.id.txtForgotUsername);
        forgotButton = findViewById(R.id.btnForgot);
        loadingProgressBar = findViewById(R.id.forgotLoading);

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                forgotPassword(usernameEditText.getText().toString());
            }
        });
    }

    private void forgotPassword(String usernameEditText) {
        mAuth.sendPasswordResetEmail(usernameEditText)
            .addOnCompleteListener(this, new OnCompleteListener<Void>(){
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Recuperación de contraseña fallida.",Toast.LENGTH_SHORT).show();
                    }
                    loadingProgressBar.setVisibility(View.GONE);
                }
            });

    }
}