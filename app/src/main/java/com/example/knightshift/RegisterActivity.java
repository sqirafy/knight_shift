package com.example.knightshift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knightshift.db.DatabaseHandle;
import com.google.android.material.textfield.TextInputEditText;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView registerBanner;
    private EditText registerEmailEdit, registerPasswordEdit1, registerPasswordEdit2;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBanner = (TextView) findViewById(R.id.registerBanner);
        registerEmailEdit = (EditText) findViewById(R.id.registerEmailEdit);
        registerPasswordEdit1 = (EditText) findViewById(R.id.registerPasswordEdit1);
        registerPasswordEdit2 = (EditText) findViewById(R.id.registerPasswordEdit2);
        registerButton = (Button) findViewById(R.id.registerButton);

        registerBanner.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.registerBanner:
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.registerButton:
                registerUser();
                break;
        }
    }

    private void registerUser() {

        String email = registerEmailEdit.getText().toString().trim();
        String password1 = registerPasswordEdit1.getText().toString().trim();
        String password2 = registerPasswordEdit2.getText().toString().trim();

        if (email.isEmpty()) {
            registerEmailEdit.setError("Email Required");
            registerEmailEdit.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerEmailEdit.setError("Valid Email Required");
            registerEmailEdit.requestFocus();
            return;
        }
        // password1
        if (password1.isEmpty()) {
            registerPasswordEdit1.setError("Password Required");
            registerPasswordEdit1.requestFocus();
            return;
        }
        if (password1.length() < 6) {
            registerPasswordEdit1.setError("Minimum Password Length is 6");
            registerPasswordEdit1.requestFocus();
            return;
        }
        // password2
        if (password2.isEmpty()) {
            registerPasswordEdit2.setError("Password Required");
            registerPasswordEdit2.requestFocus();
            return;
        }
        if (password2.length() < 6) {
            registerPasswordEdit2.setError("Minimum Password Length is 6");
            registerPasswordEdit2.requestFocus();
            return;
        }
        // equal passwords
        if (!password1.equals(password2)) {
            registerPasswordEdit1.setError("Passwords Do Not Match");
            registerPasswordEdit1.requestFocus();
            return;
        }

        DatabaseHandle.getApp().getEmailPassword().registerUserAsync(email, password1, it -> {
            if (it.isSuccess()) {
                Toast.makeText(this, "Registration Success", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Registration Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
