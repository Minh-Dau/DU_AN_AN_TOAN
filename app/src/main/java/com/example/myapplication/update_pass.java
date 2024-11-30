package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.text.TextUtils;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class update_pass extends AppCompatActivity {
    EditText nhappass, nhappass2;
    Button update;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass);

        nhappass = findViewById(R.id.edit_matkhaumoi);
        nhappass2 = findViewById(R.id.edit_nhapmatkhaumoi);
        update = findViewById(R.id.btn_update);

        // Lấy số điện thoại từ Intent
        String phoneNumber = getIntent().getStringExtra("mobile");

        // Kết nối Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        update.setOnClickListener(v -> {
            String password1 = nhappass.getText().toString().trim();
            String password2 = nhappass2.getText().toString().trim();
            String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
            // Kiểm tra mật khẩu hợp lệ
            if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
                Toast.makeText(update_pass.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password1.equals(password2)) {
                Toast.makeText(update_pass.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password1.length() < 6) {
                Toast.makeText(update_pass.this, "Mật khẩu có ít nhất 6 k ý tự", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password1.matches(passwordPattern)) {
                Toast.makeText(update_pass.this, "Mật khẩu phải chứa ít nhất 1 chữ in hoa, 1 số và 1 ký tự đặc biệt, và dài tối thiểu 8 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra số điện thoại trong Firebase trước khi cập nhật
            databaseReference.orderByChild("Sodienthoai").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Nếu số điện thoại tồn tại, cập nhật mật khẩu
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("Password").setValue(password1)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(update_pass.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            Intent intent =new Intent(getApplicationContext(),login.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(update_pass.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Nếu số điện thoại không tồn tại
                        Toast.makeText(update_pass.this, "Số ko tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(update_pass.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
