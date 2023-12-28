package com.example.knightshift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knightshift.db.DatabaseHandle;

import java.util.regex.Pattern;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView resetBanner;
    private EditText resetEmailEdit, resetPasswordEdit;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetBanner = (TextView) findViewById(R.id.resetBanner);
        resetEmailEdit = (EditText) findViewById(R.id.resetEmailEdit);
        resetPasswordEdit = (EditText) findViewById(R.id.resetPassword);
        resetButton = (Button) findViewById(R.id.resetButton);

        resetButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.resetBanner:
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.resetButton:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {

        String email = resetEmailEdit.getText().toString();
        String password = resetPasswordEdit.getText().toString();

        if (email.isEmpty()) {
            resetEmailEdit.setError("Email Required");
            resetEmailEdit.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            resetEmailEdit.setError("Valid Email Required");
            resetEmailEdit.requestFocus();
            return;
        }

        if (password.length() < 6) {
            resetPasswordEdit.setError("Minimum of 6 Characters Required");
            resetPasswordEdit.requestFocus();
            return;
        }

        DatabaseHandle.getApp().getEmailPassword().callResetPasswordFunctionAsync(email, password, null, it -> {
            if (it.isSuccess()) {
                Toast.makeText(this, "Reset Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Reset Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
