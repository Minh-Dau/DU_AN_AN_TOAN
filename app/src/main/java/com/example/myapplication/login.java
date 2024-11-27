package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    Button nhan_dangnhap;
    EditText name,passsword;
    TextView nhan_dangky, nhan_quenmk;
    ImageView img_show1;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://duan-dff9f-default-rtdb.firebaseio.com/");

    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nhan_dangnhap=findViewById(R.id.btn_dangnhap);
        name=findViewById(R.id.edit_name);
        passsword=findViewById(R.id.etPassword);
        nhan_dangky=findViewById(R.id.edit_dangky);
        nhan_quenmk=findViewById(R.id.edit_quenmk);
        img_show1=findViewById(R.id.img_show1);
        fAuth = FirebaseAuth.getInstance();

        img_show1.setOnClickListener(v -> {
            if (passsword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                // Hiện mật khẩu
                passsword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                img_show1.setImageResource(R.drawable.eye_open);
            } else {
                // Ẩn mật khẩu
                passsword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                img_show1.setImageResource(R.drawable.eye_close);
            }
            // Đặt lại con trỏ về cuối chuỗi
            passsword.setSelection(passsword.getText().length());
        });

        nhan_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
                finish();
            }
        });
        nhan_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_text=name.getText().toString();
                String password_text=passsword.getText().toString();
                if(name_text.isEmpty()||password_text.isEmpty()){
                    Toast.makeText(login.this,"Dien Thong Tin",Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(name_text)){
                                String getpassword=snapshot.child(name_text).child("Password").getValue(String.class);
                                if(getpassword.equals(password_text)){
                                    Toast.makeText(login.this,"Dang nhap thanh cong",Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(getApplicationContext(),send_otp2.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(login.this,"Dang nhap that bai",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(login.this,"Dang nhap that bai",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        nhan_quenmk.setOnClickListener(view -> {
            EditText resetName = new EditText(view.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Enter your UserName To Received Reset Link.");
            passwordResetDialog.setView(resetName);

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                String username = resetName.getText().toString();

                // Gọi phương thức để lấy thông tin người dùng từ Firebase
                getUserDetailsByUsername(username).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult();

                        // Truyền tất cả thông tin qua Intent
                        Intent intent = new Intent(login.this, resetPassword.class);
                        intent.putExtra("id", user.getId());
                        intent.putExtra("email", user.getEmail());
                        intent.putExtra("password", user.getPassword());
                        intent.putExtra("name", user.getName());
                        intent.putExtra("sdt", user.getSDT());
                        startActivity(intent);
                    } else {
                        // Xử lý lỗi
                        Toast.makeText(getApplicationContext(), "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
                // Close the dialog
            });

            passwordResetDialog.create().show();
        });
    }

    public Task<User> getUserDetailsByUsername(String username) {
        TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String name = childSnapshot.child("Name").getValue(String.class);
                    if (username.equals(name)) {
                        String email = childSnapshot.child("Email").getValue(String.class);
                        String id = childSnapshot.getKey();
                        String password = childSnapshot.child("Password").getValue(String.class);
                        String sdt = childSnapshot.child("Sodienthoai").getValue(String.class);

                        if (email != null && id != null && password != null && sdt != null) {
                            taskCompletionSource.setResult(new User(id, email, password, name, sdt));
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    taskCompletionSource.setException(new Exception("Username không tồn tại"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });
        return taskCompletionSource.getTask();
    }



}