package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class show extends AppCompatActivity {
    EditText email, name, sdt, pass,id;
    Button update;
    CheckBox showpass;
    DatabaseReference databaseReference;
    private static final String CHANNEL_ID = "OTP_Channel";
    private static final String CHANNEL_NAME = "OTP Notifications";
    private static final String CHANNEL_DESC = "This channel is used for OTP notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        // Ánh xạ các view
        email = findViewById(R.id.edit_email);
        name = findViewById(R.id.edit_name);
        sdt = findViewById(R.id.edit_sdt);
        pass = findViewById(R.id.edit_pass);
        update = findViewById(R.id.btn_updateall);
        showpass = findViewById(R.id.check);
        id=findViewById(R.id.edit_ID);
        // Kết nối Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        // Tạo kênh thông báo
        createNotificationChannel();

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String userID= intent.getStringExtra("ID");
        String userName = intent.getStringExtra("name");
        String userEmail = intent.getStringExtra("email");
        String userPhone = intent.getStringExtra("Sodienthoai");
        String userPass = intent.getStringExtra("Password");


        // Hiển thị dữ liệu lên giao diện
        id.setText(userID);
        name.setText(userName);
        email.setText(userEmail);
        sdt.setText(userPhone);
        pass.setText(userPass);

        // Hiển thị hoặc ẩn mật khẩu
        showpass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                pass.setTransformationMethod(null);
            } else {
                pass.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
            }
            pass.setSelection(pass.getText().length());
        });

        // Xử lý sự kiện nút "Cập nhật"
        update.setOnClickListener(view -> {
            String mail = email.getText().toString().trim();
            String ten = name.getText().toString().trim();
            String password = pass.getText().toString().trim();
            String sodienthoai = sdt.getText().toString().trim();
            String phonePattern = "^(03|05|07|08|09)\\d{8}$";
            String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
                    if (ten.isEmpty() || mail.isEmpty() || sodienthoai.isEmpty() || password.isEmpty()) {
                        Toast.makeText(show.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                        Toast.makeText(show.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else if (!sodienthoai.matches(phonePattern)) {
                        Toast.makeText(show.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else if (!password.matches(passwordPattern)) {
                        Toast.makeText(show.this, "Mật khẩu phải chứa ít nhất 8 ký tự, gồm 1 chữ in hoa, 1 số, và 1 ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                    } else {
                        update.setVisibility(View.INVISIBLE);
                        // Tạo mã OTP ngẫu nhiên
                        String randomOTP = OTPngaunhien();
                        // Gửi mã OTP qua Notification
                        sendOTPNotification(randomOTP);

                        // Chuyển sang màn hình verify_otp3
                        Intent otpIntent = new Intent(getApplicationContext(), verify_otp3.class);
                        otpIntent.putExtra("ID", id.getText().toString());
                        otpIntent.putExtra("Sodienthoai", sdt.getText().toString());
                        otpIntent.putExtra("name", name.getText().toString());
                        otpIntent.putExtra("email", email.getText().toString());
                        otpIntent.putExtra("Password", pass.getText().toString());
                        otpIntent.putExtra("verificationId", randomOTP); // This is your OTP
                        startActivity(otpIntent);
                        finish();
                    }
        });
    }
    // Phương thức tạo mã OTP ngẫu nhiên
    private String OTPngaunhien() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Phương thức gửi thông báo OTP
    private void sendOTPNotification(String otp) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Your OTP Code")
                .setContentText("Your OTP is: " + otp)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    // Phương thức tạo kênh thông báo
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}