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

public class Register extends AppCompatActivity {
    EditText name, email, sodienthoai, pass1, pass2;
    Button nhan_dangky;
    TextView nhan_dangnhap;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://duan-dff9f-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.edit_name);
        email = findViewById(R.id.edit_email);
        pass1 = findViewById(R.id.edit_pass1);
        pass2 = findViewById(R.id.edit_pass2);
        nhan_dangky = findViewById(R.id.btn_dangky);
        nhan_dangnhap = findViewById(R.id.edit_dangnhap);
        sodienthoai = findViewById(R.id.edit_sdt);

        nhan_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        nhan_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_text = name.getText().toString();
                String email_text = email.getText().toString();
                String pass1_text = pass1.getText().toString();
                String pass2_text = pass2.getText().toString();
                String sodienthoai_text = sodienthoai.getText().toString();

                String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
                String phonePattern = "^(03|05|07|08|09)\\d{8}$";

                if (name_text.isEmpty() || email_text.isEmpty() || pass1_text.isEmpty() || pass2_text.isEmpty()) {
                    Toast.makeText(Register.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (!pass1_text.equals(pass2_text)) {
                    Toast.makeText(Register.this, "Mật khẩu không trùng", Toast.LENGTH_SHORT).show();
                } else if (!sodienthoai_text.matches(phonePattern)) {
                    Toast.makeText(Register.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                    Toast.makeText(Register.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                } else if (!pass1_text.matches(passwordPattern)) {
                    Toast.makeText(Register.this, "Mật khẩu phải chứa ít nhất 1 chữ in hoa, 1 số và 1 ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                } else {
                    // Get the current last ID used from Firebase
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
                                        Toast.makeText(Register.this, "Email đã đăng ký", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Set user data along with the generated ID (No ID input from user)
                                        usersRef.child(newUserIdStr).child("ID").setValue(newUserIdStr); // Store the generated ID
                                        usersRef.child(newUserIdStr).child("Name").setValue(name_text);
                                        usersRef.child(newUserIdStr).child("Email").setValue(email_text);
                                        usersRef.child(newUserIdStr).child("Password").setValue(pass1_text);
                                        usersRef.child(newUserIdStr).child("Sodienthoai").setValue(sodienthoai_text);

                                        // Update the lastUserId to the new ID
                                        idRef.setValue(newUserId);

                                        Toast.makeText(Register.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                        // Navigate to login page
                                        Intent intent = new Intent(Register.this, login.class);
                                        startActivity(intent);
                                        finish(); // Close the current activity
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(Register.this, "Lỗi kết nối cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Register.this, "Lỗi kết nối cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}