package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class verify_email extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String userID, name_text, email_text, pass1_text, sodienthoai_text;
    private Button dangkylai;
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
//        userID = getIntent().getStringExtra("userID");
        name_text = getIntent().getStringExtra("name_text");
        email_text = getIntent().getStringExtra("email_text");
        pass1_text = getIntent().getStringExtra("pass1_text");
        sodienthoai_text = getIntent().getStringExtra("sodienthoai_text");
        dangkylai=findViewById(R.id.edit_dangkylai);
        startTime = System.currentTimeMillis();
        startEmailVerificationCheck();
        dangkylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(verify_email.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
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
                                DatabaseReference idRef = databaseReference.child("lastUserId");
                                idRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int lastUserId = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
                                        int newUserId = lastUserId + 1; // Increment ID
                                        String newUserIdStr = String.format("%05d", newUserId); // Format as 5 digits

                                        // Create a new user entry with the generated ID
                                        DatabaseReference usersRef = databaseReference.child("users");
                                        usersRef.orderByChild("Email").equalTo(email_text).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Toast.makeText(verify_email.this, "Email đã đăng ký", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Set user data along with the generated ID (No ID input from user)
                                                    usersRef.child(newUserIdStr).child("ID").setValue(newUserIdStr); // Store the generated ID
                                                    usersRef.child(newUserIdStr).child("Name").setValue(name_text);
                                                    usersRef.child(newUserIdStr).child("Email").setValue(email_text);
                                                    usersRef.child(newUserIdStr).child("Password").setValue(pass1_text);
                                                    usersRef.child(newUserIdStr).child("Sodienthoai").setValue(sodienthoai_text);

                                                    // Update the lastUserId to the new ID
                                                    idRef.setValue(newUserId);

                                                    Toast.makeText(verify_email.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                                    // Navigate to login page
                                                    Intent intent = new Intent(verify_email.this, login.class);
                                                    startActivity(intent);
                                                    finish(); // Close the current activity
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(verify_email.this, "Lỗi kết nối cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(verify_email.this, "Lỗi kết nối cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                                    }
                                });

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