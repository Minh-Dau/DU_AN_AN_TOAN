package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class send_otp2 extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp2);

        final EditText inputEmail = findViewById(R.id.inputEmail); // Thay đổi từ inputMobile thành inputEmail
        Button btnGetOTP = findViewById(R.id.btnGetOTP); // Đổi tên nút từ btnGetOTP thành btnSignUp

        final ProgressBar progressBar = findViewById(R.id.progressBar);

        btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(send_otp2.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }

                String otp = generateOTP(); // Tạo mã OTP

                progressBar.setVisibility(View.VISIBLE);
                btnGetOTP.setVisibility(View.INVISIBLE);

                // Sử dụng Firebase Auth để đăng ký người dùng mới
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                EmailUtil.sendOTPEmail(email, otp);

                progressBar.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);

                Toast.makeText(send_otp2.this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();

                // Lưu OTP vào SharedPreferences hoặc biến tạm thời để xác thực sau này
                // Bạn cần lưu OTP để kiểm tra khi người dùng nhập mã OTP
                Intent intent = new Intent(getApplicationContext(), verify_otp2.class);
                intent.putExtra("email", inputEmail.getText().toString());
                intent.putExtra("verificationId", otp);
                startActivity(intent);


            }
        });
    }

    public static String generateOTP() {
        int otp = (int) (Math.random() * 900000) + 100000; // Mã OTP 6 chữ số
        return String.valueOf(otp);
    }

}