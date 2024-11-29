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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Random;

public class send_otp extends AppCompatActivity {
    EditText inputMobile;
    Button btnGetOTP;
    ProgressBar progressBar;

    private static final String CHANNEL_ID = "OTP_Channel";
    private static final String CHANNEL_NAME = "OTP Notifications";
    private static final String CHANNEL_DESC = "This channel is used for OTP notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        inputMobile = findViewById(R.id.inputMobile);
        btnGetOTP = findViewById(R.id.btnGetOTP);
        progressBar = findViewById(R.id.progressBar);

        createNotificationChannel();

        btnGetOTP.setOnClickListener(v -> {
            String phoneNumber = inputMobile.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            btnGetOTP.setVisibility(View.INVISIBLE);

            // Tạo mã OTP ngẫu nhiên
            String randomOTP = generateOTP();

            // Gửi mã OTP qua Notification
            sendOTPNotification(randomOTP);

            // Chuyển đến màn hình verify_otp
            Intent intent = new Intent(send_otp.this, verify_otp.class);
            intent.putExtra("mobile", phoneNumber);
            intent.putExtra("verificationId", randomOTP); // Truyền mã OTP qua Intent
            startActivity(intent);
        });
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOTPNotification(String otp) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Icon cho thông báo
                .setContentTitle("Your OTP Code")
                .setContentText("Your OTP is: " + otp)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
