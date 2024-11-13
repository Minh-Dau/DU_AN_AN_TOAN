package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class verify_email extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String userID, name_text, email_text, pass1_text, sodienthoai_text;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://duan-dff9f-default-rtdb.firebaseio.com/");
    private Handler handler = new Handler();
    private Runnable verificationCheckRunnable;

    private long startTime;
    private final long TIME_LIMIT = 5 * 60 * 1000; // 5 phút


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);


        firebaseAuth = FirebaseAuth.getInstance();
        userID = getIntent().getStringExtra("userID");
        name_text = getIntent().getStringExtra("name_text");
        email_text = getIntent().getStringExtra("email_text");
        pass1_text = getIntent().getStringExtra("pass1_text");
        sodienthoai_text = getIntent().getStringExtra("sodienthoai_text");

        startTime = System.currentTimeMillis();
        startEmailVerificationCheck();



    }


    private void startEmailVerificationCheck() {
        verificationCheckRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= TIME_LIMIT) {
                    // Nếu đã quá 5 phút, dừng kiểm tra và thông báo người dùng
                    Toast.makeText(verify_email.this, "Đã hết thời gian chờ xác thực email.", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.reload().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (user.isEmailVerified()) {
                                // Lưu thông tin người dùng vào Firebase Database
                                databaseReference.child("users").child(user.getUid()).child("Name").setValue(name_text);
                                databaseReference.child("users").child(user.getUid()).child("Email").setValue(email_text);
                                databaseReference.child("users").child(user.getUid()).child("Password").setValue(pass1_text);
                                databaseReference.child("users").child(user.getUid()).child("Sodienthoai").setValue(sodienthoai_text);

                                // Chuyển đến màn hình show
                                Intent intent = new Intent(verify_email.this, show.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Nếu chưa xác thực, tiếp tục kiểm tra sau 2 giây
                                handler.postDelayed(verificationCheckRunnable, 2000);
                            }
                        } else {
                            Toast.makeText(verify_email.this, "Lỗi khi tải lại thông tin người dùng.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        // Kiểm tra lần đầu tiên ngay lập tức
        handler.post(verificationCheckRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dừng kiểm tra khi activity bị hủy
        handler.removeCallbacks(verificationCheckRunnable);
    }

}