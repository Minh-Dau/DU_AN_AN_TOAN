package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class verify_otp2 extends AppCompatActivity {

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private String verificationId;
    private TextView textResendOTP, txtDangNhap;
    private Button btnVerify;
    private ProgressBar progressBar;
    private String otp; // Lưu mã OTP đã gửi
    private int wrongAttempts = 0; // Số lần nhập sai
    private long lastAttemptTime = 0; // Thời gian thử lần cuối

    private static final String PREF_NAME = "OTPPreferences";
    private static final String KEY_WRONG_ATTEMPTS = "wrongAttempts";
    private static final String KEY_LAST_ATTEMPT_TIME = "lastAttemptTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp2);

        TextView textEmail = findViewById(R.id.textEmail);

        inputCode1 =findViewById(R.id.inputCode1);
        inputCode2 =findViewById(R.id.inputCode2);
        inputCode3 =findViewById(R.id.inputCode3);
        inputCode4 =findViewById(R.id.inputCode4);
        inputCode5 =findViewById(R.id.inputCode5);
        inputCode6 =findViewById(R.id.inputCode6);
        textResendOTP=findViewById(R.id.textResendOTP);
        setupOTPInputs();

        progressBar = findViewById(R.id.progressBar);
        btnVerify = findViewById(R.id.btnVerifyOTP);
        txtDangNhap = findViewById(R.id.txtDangNhap);

        otp = getIntent().getStringExtra("verificationId");

        Intent intent = getIntent();
        String userID= intent.getStringExtra("ID");
        String userName = intent.getStringExtra("name");
        String userEmail = intent.getStringExtra("email");
        String userPhone = intent.getStringExtra("Sodienthoai");
        String userPass = intent.getStringExtra("Password");

        textEmail.setText(userEmail);
        // Load thông tin từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        wrongAttempts = prefs.getInt(KEY_WRONG_ATTEMPTS, 0);
        lastAttemptTime = prefs.getLong(KEY_LAST_ATTEMPT_TIME, 0);

        txtDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra số lần thử và thời gian giữa các lần thử
                long currentTime = System.currentTimeMillis();
                if (wrongAttempts >= 2) {
                    long waitTime = (30000 - (currentTime - lastAttemptTime)) / 1000; // Thời gian còn phải chờ
                    if (waitTime > 0) {
                        Toast.makeText(verify_otp2.this, "Thử lại sau " + waitTime + " giây", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // trả về login
                    finish();
                }

                if (inputCode1.getText().toString().trim().isEmpty()
                        || inputCode2.getText().toString().trim().isEmpty()
                        || inputCode3.getText().toString().trim().isEmpty()
                        || inputCode4.getText().toString().trim().isEmpty()
                        || inputCode5.getText().toString().trim().isEmpty()
                        || inputCode6.getText().toString().trim().isEmpty()){
                    Toast.makeText(verify_otp2.this, "Hãy nhập đủ các ô.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (code.equals(otp)) {
                    // Reset số lần nhập sai khi OTP chính xác
                    wrongAttempts = 0;
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), show.class);
                    intent.putExtra("ID", userID);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("Sodienthoai", userPhone);
                    intent.putExtra("Password", userPass);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else { // nhập không đúng
                    // Cập nhật số lần nhập sai
                    wrongAttempts++;
                    lastAttemptTime = currentTime; // Cập nhật thời gian nhập sai

                    // Lưu lại số lần nhập sai và thời gian nhập sai
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(KEY_WRONG_ATTEMPTS, wrongAttempts);
                    editor.putLong(KEY_LAST_ATTEMPT_TIME, lastAttemptTime);
                    editor.apply();

                    Toast.makeText(verify_otp2.this, "OTP nhập không đúng, hãy nhập lại!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        textResendOTP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long currentSendTime = System.currentTimeMillis();

                // Giới hạn thời gian thử lại (30s)
                if (currentSendTime - lastAttemptTime < 30000) {
                    long waitTime = (30000 - (currentSendTime - lastAttemptTime)) / 1000;
                    Toast.makeText(verify_otp2.this, "Thử lại sau " + waitTime + " giây", Toast.LENGTH_LONG).show();
                    return;
                }
                // Giới hạn số lần gửi lại OTP
                else if (wrongAttempts > 2) {
                    Toast.makeText(verify_otp2.this, "Đã quá số lần gửi. Hãy đăng nhập lại", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                verifyLogin.sendEmailOTP(userEmail, new verifyLogin.OTPCallback() {
                    @Override
                    public void onResult(boolean success, String otp) {
                        if (success) {
                            verify_otp2.this.otp = otp; // Lưu mã OTP đã gửi
                            Toast.makeText(verify_otp2.this, "Mã OTP đã được gửi lại.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(verify_otp2.this, "Gửi OTP thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }



    private void setupOTPInputs(){
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Kiểm tra Backspace
                if (s.length() == 1 && count == 1 && after == 0) {
                    // Nếu người dùng nhấn Backspace, chuyển sang ô trước
                    inputCode1.setSelection(inputCode1.length()); // Đưa con trỏ về cuối
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 1 && count == 1 && after == 0) {
                    inputCode1.requestFocus();  // Di chuyển về inputCode1 khi Backspace
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 1 && count == 1 && after == 0) {
                    inputCode2.requestFocus();  // Di chuyển về inputCode2 khi Backspace
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 1 && count == 1 && after == 0) {
                    inputCode3.requestFocus();  // Di chuyển về inputCode3 khi Backspace
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 1 && count == 1 && after == 0) {
                    inputCode4.requestFocus();  // Di chuyển về inputCode4 khi Backspace
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 1 && count == 1 && after == 0) {
                    inputCode5.requestFocus();  // Di chuyển về inputCode5 khi Backspace
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Logic tự động chuyển sang nút xác nhận khi điền đầy đủ mã OTP
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Kiểm tra nếu tất cả các trường OTP đã được nhập đầy đủ
                if (!inputCode1.getText().toString().trim().isEmpty() &&
                        !inputCode2.getText().toString().trim().isEmpty() &&
                        !inputCode3.getText().toString().trim().isEmpty() &&
                        !inputCode4.getText().toString().trim().isEmpty() &&
                        !inputCode5.getText().toString().trim().isEmpty() &&
                        !inputCode6.getText().toString().trim().isEmpty()) {

                    // Tự động nhấn nút xác nhận
                    btnVerify.performClick();
                }
            }
        });
    }

}