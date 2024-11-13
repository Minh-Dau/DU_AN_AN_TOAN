package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
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

                // Chạy AsyncTask để gửi OTP
                new SendOTPAsyncTask(email, otp, progressBar, btnGetOTP).execute();
            }
        });
    }

    // Tạo mã OTP 6 chữ số
    public static String generateOTP() {
        int otp = (int) (Math.random() * 900000) + 100000; // Mã OTP 6 chữ số
        return String.valueOf(otp);
    }

    // AsyncTask để gửi OTP trong background
    private static class SendOTPAsyncTask extends AsyncTask<Void, Void, Void> {
        private String email;
        private String otp;
        private ProgressBar progressBar;
        private Button btnGetOTP;

        SendOTPAsyncTask(String email, String otp, ProgressBar progressBar, Button btnGetOTP) {
            this.email = email;
            this.otp = otp;
            this.progressBar = progressBar;
            this.btnGetOTP = btnGetOTP;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Gửi OTP email
            EmailUtil.sendOTPEmail(email, otp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Ẩn ProgressBar và hiển thị lại nút
            progressBar.setVisibility(View.GONE);
            btnGetOTP.setVisibility(View.VISIBLE);

            // Hiển thị thông báo gửi OTP thành công
            Toast.makeText(btnGetOTP.getContext(), "OTP sent to " + email, Toast.LENGTH_SHORT).show();

            // Chuyển đến màn hình xác thực OTP
            Intent intent = new Intent(btnGetOTP.getContext(), verify_otp2.class);
            intent.putExtra("email", email);
            intent.putExtra("verificationId", otp);
            btnGetOTP.getContext().startActivity(intent);
        }
    }
}
