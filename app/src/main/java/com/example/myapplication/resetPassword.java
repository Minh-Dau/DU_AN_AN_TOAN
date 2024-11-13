package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class resetPassword extends AppCompatActivity {

    TextView textUsername, textError;
    EditText editMatKhau, editCheckPass;
    CheckBox chkShowPass;
    Button btnLuuPass;

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://duan-dff9f-default-rtdb.firebaseio.com/");

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        textUsername = findViewById(R.id.textUsername);
        textError = findViewById(R.id.textError);
        editMatKhau = findViewById(R.id.editMatKhau);
        editCheckPass = findViewById(R.id.editCheckPass);
        chkShowPass = findViewById(R.id.chkShowPass);
        btnLuuPass = findViewById(R.id.btnLuuPass);

        String id = getIntent().getStringExtra("id");

        chkShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Hiển thị mật khẩu
                    editMatKhau.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editCheckPass.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Ẩn mật khẩu
                    editMatKhau.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editCheckPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Đặt con trỏ ở cuối văn bản
                editMatKhau.setSelection(editMatKhau.getText().length());
                editCheckPass.setSelection(editCheckPass.getText().length());
            }
        });


        btnLuuPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matKhau = editMatKhau.getText().toString();
                String checkPass = editCheckPass.getText().toString();

                if (!matKhau.equals(checkPass)) {
                    // Hiển thị lỗi khi mật khẩu không khớp
                    textError.setText("Mật khẩu đã nhập không khớp. Hãy thử lại.");
                    textError.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else if (matKhau.length() < 8) {
                    // Hiển thị lỗi khi mật khẩu không đủ 8 ký tự
                    textError.setText("Sử dụng 8 ký tự trở lên cho mật khẩu của bạn.");
                    textError.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    // Mật khẩu hợp lệ
                    textError.setText(""); // Xóa thông báo lỗi nếu có

                    // Cập nhật mật khẩu vào Firebase
                    databaseReference.child("users").child(id).child("Password").setValue(matKhau)
                            .addOnSuccessListener(aVoid -> {
                                // Thông báo thành công
                                textError.setText("Mật khẩu đã được cập nhật thành công.");
                                textError.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            })
                            .addOnFailureListener(e -> {
                                // Thông báo lỗi khi cập nhật không thành công
                                textError.setText("Cập nhật mật khẩu thất bại: " + e.getMessage());
                                textError.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            });
                }
            }
        });





    }
}