package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class verify_otp extends AppCompatActivity {

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private String sentOtp; // Mã OTP nhận từ send_otp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        TextView textMobile = findViewById(R.id.textMobile);
        textMobile.setText(String.format(
                "+84-%s", getIntent().getStringExtra("mobile")
        ));

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setupOTPInputs();

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Button btnVerify = findViewById(R.id.btnVerifyOTP);

        // Lấy mã OTP từ send_otp qua Intent
        sentOtp = getIntent().getStringExtra("verificationId");

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputCode1.getText().toString().trim().isEmpty() ||
                        inputCode2.getText().toString().trim().isEmpty() ||
                        inputCode3.getText().toString().trim().isEmpty() ||
                        inputCode4.getText().toString().trim().isEmpty() ||
                        inputCode5.getText().toString().trim().isEmpty() ||
                        inputCode6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(verify_otp.this, "Vui lòng nhập mã", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ghép mã người dùng nhập từ 6 ô input
                String enteredCode = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();

                // So sánh mã nhập với mã OTP đã gửi
                if (enteredCode.equals(sentOtp)) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnVerify.setVisibility(View.INVISIBLE);

                    // Mã chính xác -> Chuyển đến màn hình tiếp theo
                    Toast.makeText(verify_otp.this, "Mã chính xác", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),update_pass.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("mobile", getIntent().getStringExtra("mobile")); // Truyền số điện thoại
                    startActivity(intent);

                } else {
                    // Mã sai -> Thông báo lỗi
                    Toast.makeText(verify_otp.this, "Mã sai.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new OTPTextWatcher(inputCode2));
        inputCode2.addTextChangedListener(new OTPTextWatcher(inputCode3));
        inputCode3.addTextChangedListener(new OTPTextWatcher(inputCode4));
        inputCode4.addTextChangedListener(new OTPTextWatcher(inputCode5));
        inputCode5.addTextChangedListener(new OTPTextWatcher(inputCode6));
    }

    // TextWatcher helper class to handle OTP input focus
    private class OTPTextWatcher implements TextWatcher {
        private final EditText nextInput;

        public OTPTextWatcher(EditText nextInput) {
            this.nextInput = nextInput;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().trim().isEmpty() && nextInput != null) {
                nextInput.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
