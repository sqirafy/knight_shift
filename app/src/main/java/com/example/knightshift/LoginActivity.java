package com.example.knightshift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knightshift.db.DatabaseHandle;


import io.realm.mongodb.Credentials;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText loginEmailEdit, loginPasswordEdit;
    private Button loginButton;
    private TextView loginResetView, loginRegisterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailEdit = (EditText) findViewById(R.id.loginEmailEdit);
        loginPasswordEdit = (EditText) findViewById(R.id.loginPasswordEdit);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginResetView = (TextView) findViewById(R.id.loginResetView);
        loginRegisterView = (TextView) findViewById(R.id.loginRegisterView);

        loginButton.setOnClickListener(this);
        loginResetView.setOnClickListener(this);
        loginRegisterView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.loginButton:
                userLogin();
                break;

            case R.id.loginResetView:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;

            case R.id.loginRegisterView:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void userLogin() {

        String email = loginEmailEdit.getText().toString().trim();
        String password = loginPasswordEdit.getText().toString();

        Credentials credentials = Credentials.emailPassword(email, password);

        DatabaseHandle.getApp().loginAsync(credentials, it -> {
            if (it.isSuccess()) {
                DatabaseHandle.user = DatabaseHandle.getApp().currentUser();
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DatabaseGui.class));
            } else {
                Log.e("", "");
                Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
