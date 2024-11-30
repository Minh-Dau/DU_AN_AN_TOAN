package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    Button nhan_dangnhap;
    EditText name, password;
    TextView nhan_dangky, quenmatkhau;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://duan-dff9f-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nhan_dangnhap = findViewById(R.id.btn_dangnhap);
        name = findViewById(R.id.edit_name);
        password = findViewById(R.id.etPassword);
        nhan_dangky = findViewById(R.id.edit_dangky);
        quenmatkhau = findViewById(R.id.edit_quenmatkhau);

        quenmatkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), send_otp.class);
                startActivity(intent);
                finish();
            }
        });

        nhan_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        nhan_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_text = name.getText().toString();
                String password_text = password.getText().toString();
                if (name_text.isEmpty() || password_text.isEmpty()) {
                    Toast.makeText(login.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("users").orderByChild("Name").equalTo(name_text).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Loop through all users with the same name (if any) and check the password
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String getPassword = userSnapshot.child("Password").getValue(String.class);
                                    if (getPassword != null && getPassword.equals(password_text)) {
                                        Toast.makeText(login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                        // Retrieve user details
                                        String id = userSnapshot.child("ID").getValue(String.class);
                                        String email = userSnapshot.child("Email").getValue(String.class);
                                        String phone = userSnapshot.child("Sodienthoai").getValue(String.class);
                                        String pass = userSnapshot.child("Password").getValue(String.class);

                                        verifyLogin(email);
                                        Intent intent = new Intent(getApplicationContext(), show.class);
                                        intent.putExtra("ID", id);
                                        intent.putExtra("name", name_text);
                                        intent.putExtra("email", email);
                                        intent.putExtra("Sodienthoai", phone);
                                        intent.putExtra("Password", pass);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }
                                // If no password matches
                                Toast.makeText(login.this, "Tên tài khoản hoặc mật khẩu sai!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(login.this, "Tên tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(login.this, "Lỗi kết nối cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Tạo mã OTP 6 chữ số
        public static String generateOTP() {
            int otp = (int) (Math.random() * 900000) + 100000; // Mã OTP 6 chữ số
            return String.valueOf(otp);
        }

        // AsyncTask để gửi OTP trong background
        private static class SendOTPAsyncTask extends AsyncTask<Void, Void, Void> {
            private String email;
            private String otp;
            private Button nhan_dangnhap;
            private ProgressBar progressBar;


            SendOTPAsyncTask(String email, String otp, ProgressBar progressBar, Button nhan_dangnhap) {
                this.email = email;
                this.otp = otp;
                this.progressBar = progressBar;
                this.nhan_dangnhap = nhan_dangnhap;
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

                progressBar.setVisibility(View.GONE);
                nhan_dangnhap.setVisibility(View.VISIBLE);

                // Hiển thị thông báo gửi OTP thành công
                Toast.makeText(nhan_dangnhap.getContext(), "OTP sent to " + email, Toast.LENGTH_SHORT).show();

                // Chuyển đến màn hình xác thực OTP
                Intent intent = new Intent(nhan_dangnhap.getContext(), verify_otp2.class);
                intent.putExtra("email", email);
                intent.putExtra("verificationId", otp);
                nhan_dangnhap.getContext().startActivity(intent);
            }
        }


        public void verifyLogin(String emailInput){
            String email = emailInput.trim();

            String otp = generateOTP(); // Tạo mã OTP

            nhan_dangnhap.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);



            // Chạy AsyncTask để gửi OTP
            new SendOTPAsyncTask(email, otp, progressBar, nhan_dangnhap).execute();
        }

    }
}
