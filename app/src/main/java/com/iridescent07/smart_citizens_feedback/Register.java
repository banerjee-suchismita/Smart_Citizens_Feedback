package com.iridescent07.smart_citizens_feedback;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private Button register;
    private EditText email, password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.buttonregister);
        email = findViewById(R.id.emailtext);
        password = findViewById(R.id.passtext);

    }

    public void login(View view)
    {
        Intent i=new Intent(Register.this,Login.class);
        startActivity(i);
    }

    public void register(View view)
    {
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

        firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener((task)-> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, Profile.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Could not register, please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
