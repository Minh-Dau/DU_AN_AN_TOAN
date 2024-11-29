package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    ProgressBar progressBar;
    EditText name,passsword;
    TextView nhan_dangky, nhan_quenmk;
    ImageView img_show1;
    String id,email,username,password,sdt;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://duan-dff9f-default-rtdb.firebaseio.com/");

    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nhan_dangnhap=findViewById(R.id.btn_dangnhap);
        progressBar = findViewById(R.id.progressBar);
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

                                        // Pass user details to the next activity
                                        verifyLogin(email);
//                                        Intent intent = new Intent(getApplicationContext(), show.class);
//                                        intent.putExtra("ID", id);
//                                        intent.putExtra("name", name_text);
//                                        intent.putExtra("email", email);
//                                        intent.putExtra("Sodienthoai", phone);
//                                        intent.putExtra("Password", pass);
//                                        startActivity(intent);
//                                        finish();
//                                        return;
                                    }
                                }
                                // If no password matches
                                Toast.makeText(login.this, "Tên tài khoản hoặc mật khẩu sai!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(login.this, "Tên tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                            }

//                            if(snapshot.hasChild(name_text)){
//                                String getpassword = snapshot.child(name_text).child("Password").getValue(String.class);
//                                String getEmail = snapshot.child(name_text).child("Email").getValue(String.class);
//                                if(getpassword.equals(password_text)){
//                                    getUserDetailsByUsername(name_text);
//                                    verifyLogin(getEmail);
////                                    Intent intent = new Intent(login.this,verify_otp2.class);
////                                    intent.putExtra("email", getEmail);
////                                    startActivity(intent);
////                                    finish();
//                                }
//                                else{
//                                    Toast.makeText(login.this,"Hãy kiểm tra lại mật khẩu.",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            else {
//                                Toast.makeText(login.this,"Không tồn tại user.",Toast.LENGTH_SHORT).show();
//                            }
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
                        email = childSnapshot.child("Email").getValue(String.class);
                        id = childSnapshot.getKey();
                        password = childSnapshot.child("Password").getValue(String.class);
                        sdt = childSnapshot.child("Sodienthoai").getValue(String.class);
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