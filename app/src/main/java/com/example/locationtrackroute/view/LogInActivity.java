package com.example.locationtrackroute.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.locationtrackroute.R;
import com.example.locationtrackroute.databinding.ActivityLogInBinding;
import com.example.locationtrackroute.presenter.LogInPresenter;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class LogInActivity extends MvpAppCompatActivity implements LogInInterface {
    @InjectPresenter
    LogInPresenter logInPresenter;
    private ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in);

        binding.singUpButton.setOnClickListener(v -> {
            if (!(validateEmail()) | !(validatePassword())) {
                return;
            }
            logInPresenter.singUpUser(getEmail(), getPassword());
        });

        binding.loginButton.setOnClickListener(v ->
        {
            if (!(validateEmail()) | !(validatePassword())) {
                return;
            }
            logInPresenter.loginUser(getEmail(), getPassword());
        });
    }


    public String getEmail() {
        return binding.textInputEmail.getEditText().getText().toString().trim();
    }

    public boolean validateEmail() {
        if (getEmail().isEmpty()) {
            binding.textInputEmail.setError("Enter email");
            return false;
        } else binding.textInputEmail.setError("");
        return true;
    }

    public String getPassword() {
        return binding.textInputPassword.getEditText().getText().toString().trim();
    }

    public boolean validatePassword() {
        if (getPassword().isEmpty()) {
            binding.textInputPassword.setError("Enter password");
            return false;
        } else binding.textInputPassword.setError("");
        return true;
    }


    @Override
    public void makeToast(String toast) {
        Toast.makeText(LogInActivity.this, toast, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessAuth() {
        startActivity(new Intent(LogInActivity.this, MapsActivity.class));
    }
}
