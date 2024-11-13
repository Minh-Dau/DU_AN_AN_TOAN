package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class verifyResetPassword extends AppCompatActivity {

    Button btnCode1, btnCode2, btnCode3;
    TextView textEmail;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_reset_password);

        btnCode1 = findViewById(R.id.btnCode1);
        btnCode2 = findViewById(R.id.btnCode2);
        btnCode3 = findViewById(R.id.btnCode3);

        textEmail = findViewById(R.id.textEmail);

        // gửi xác thực qua điện thoại

        email = getIntent().getStringExtra("email");


        String otp = generateOTP();


        // Chọn ngẫu nhiên một nút để đặt mã OTP
        Button[] buttons = {btnCode1, btnCode2, btnCode3};
        int randomIndex = (int) (Math.random() * buttons.length);

        // Gán mã OTP vào nút được chọn ngẫu nhiên
        buttons[randomIndex].setText(otp);

        // Đặt số ngẫu nhiên từ 1 đến 99 cho các nút còn lại
        for (int i = 0; i < buttons.length; i++) {
            if (i != randomIndex) {
                int randomValue = (int) (Math.random() * 90) + 10; // Số ngẫu nhiên từ 1 đến 99
                buttons[i].setText(String.valueOf(randomValue));
            }
        }

        // Gửi OTP qua email
        new verifyResetPassword.SendOTPAsyncTask(email, otp).execute();

        btnCode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCode1.getText().toString().equals(otp)) {
                    // Chuyển sang resetPassword.class nếu đúng
                    Intent intent = new Intent(getApplicationContext(), resetPassword.class);
                    startActivity(intent);
                } else {
                    // Thông báo sai và chuyển sang màn hình đăng nhập
                    Toast.makeText(getApplicationContext(), "Bạn đã chọn sai. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                }
            }
        });

        btnCode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCode2.getText().toString().equals(otp)) {
                    Intent intent = new Intent(getApplicationContext(), resetPassword.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Bạn đã chọn sai. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                }
            }
        });

        btnCode3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCode3.getText().toString().equals(otp)) {
                    Intent intent = new Intent(getApplicationContext(), resetPassword.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Bạn đã chọn sai. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                }
            }
        });


    }


    public static String generateOTP() {
        int otp = (int) (Math.random() * 90) + 10; // Mã OTP 2 chữ số
        return String.valueOf(otp);
    }

    private static class SendOTPAsyncTask extends AsyncTask<Void, Void, Void> {
        private String email;
        private String otp;
//        private ProgressBar progressBar;
//        private Button btnGetOTP;

        SendOTPAsyncTask(String email, String otp) {
            this.email = email;
            this.otp = otp;
//            this.progressBar = progressBar;
//            this.btnGetOTP = btnGetOTP;
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
//            progressBar.setVisibility(View.GONE);
//            btnGetOTP.setVisibility(View.VISIBLE);

            // Hiển thị thông báo gửi OTP thành công
//            Toast.makeText(btnGetOTP.getContext(), "OTP sent to " + email, Toast.LENGTH_SHORT).show();

            // Chuyển đến màn hình xác thực OTP
//            Intent intent = new Intent(btnGetOTP.getContext(), verify_otp2.class);
//            intent.putExtra("email", email);
//            intent.putExtra("verificationId", otp);
//            btnGetOTP.getContext().startActivity(intent);
        }
    }

}