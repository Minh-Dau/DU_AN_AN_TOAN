package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    EditText name, email, sodienthoai, pass1, pass2;
    Button nhan_dangky;
    TextView nhan_dangnhap;
    ImageView img_show1,img_show2;
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
        img_show1 = findViewById(R.id.img_show1);
        img_show2 = findViewById(R.id.img_show2);
        nhan_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });
        if (img_show1 != null) {
            img_show1.setOnClickListener(v -> {
                if (pass1.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    pass1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    img_show1.setImageResource(R.drawable.eye_open);
                } else {
                    pass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    img_show1.setImageResource(R.drawable.eye_close);
                }
            });
        }

        if (img_show2 != null) {
            img_show2.setOnClickListener(v -> {
                if (pass2.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    pass2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    img_show2.setImageResource(R.drawable.eye_open);
                } else {
                    pass2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    img_show2.setImageResource(R.drawable.eye_close);
                }
            });
        }

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

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                    firebaseAuth.createUserWithEmailAndPassword(email_text,pass1_text).addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    // Gửi email xác thực
                                    if (user != null) {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(verifyTask -> {
                                                    // Email xác thực đã được gửi
                                                    Toast.makeText(Register.this, "Email xác thực đã được gửi, vui lòng kiểm tra email của bạn", Toast.LENGTH_LONG).show();

                                                    // Chuyển đến màn hình xác nhận email
                                                    Intent intent = new Intent(Register.this, verify_email.class);
//                                                    intent.putExtra("userID", user.getUid());  // Chuyển ID người dùng để kiểm tra xác thực sau này
                                                    intent.putExtra("name_text", name_text);
                                                    intent.putExtra("email_text", email_text);
                                                    intent.putExtra("pass1_text", pass1_text);
                                                    intent.putExtra("sodienthoai_text", sodienthoai_text);
                                                    startActivity(intent);
                                                    finish();
                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(Register.this, "Không thể gửi email xác thực: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                }
                            }
                    );
                }
            }
        });
    }
}