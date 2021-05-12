package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.letschat.databinding.ActivityOTPBinding;

public class OTPActivity extends AppCompatActivity {

    ActivityOTPBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String phoneNumber=getIntent().getStringExtra("phoneNumber");
        binding.phoneLbl.setText("Verify "+phoneNumber);
    }
}