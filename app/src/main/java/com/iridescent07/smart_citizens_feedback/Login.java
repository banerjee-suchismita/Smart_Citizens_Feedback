package com.iridescent07.smart_citizens_feedback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button login;
    private EditText email, password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailtext);
        password = findViewById(R.id.passtext);
        login = findViewById(R.id.buttonlogin);
        login.setOnClickListener(this);
    }

    public void login() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Please enter email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(Login.this, "Could not login. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void register(View V)
    {
        Intent i=new Intent(Login.this,MainActivity.class);
        startActivity(i);
    }

    public void onClick(View view){
        if(view == login){
            login();
        }
    }

}