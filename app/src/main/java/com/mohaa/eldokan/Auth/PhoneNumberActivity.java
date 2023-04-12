package com.mohaa.eldokan.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.R;

public class PhoneNumberActivity extends BaseActivity {

    private AppCompatEditText etPhoneNumber;
    private AppCompatButton btnSendConfirmationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSendConfirmationCode = findViewById(R.id.btnSendConfirmationCode);

        btnSendConfirmationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile = etPhoneNumber.getText().toString().trim();

                if(mobile.isEmpty() || mobile.length() < 11){
                    etPhoneNumber.setError("Enter a valid mobile");
                    etPhoneNumber.requestFocus();
                    return;
                }

                Intent intent = new Intent(PhoneNumberActivity.this, VerificationCodeActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        });

    }


}
