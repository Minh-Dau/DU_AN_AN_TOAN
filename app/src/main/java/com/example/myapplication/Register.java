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
    EditText name,email,sodienthoai,pass1,pass2;
    Button nhan_dangky;
    TextView nhan_dangnhap;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://duan-dff9f-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=findViewById(R.id.edit_name);
        email=findViewById(R.id.edit_email);
        pass1=findViewById(R.id.edit_pass1);
        pass2=findViewById(R.id.edit_pass2);
        nhan_dangky=findViewById(R.id.btn_dangky);
        nhan_dangnhap=findViewById(R.id.edit_dangnhap);
        sodienthoai=findViewById(R.id.edit_sdt);
        nhan_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();
            }
        });
        nhan_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_text=name.getText().toString();
                String email_text=email.getText().toString();
                String pass1_text=pass1.getText().toString();
                String pass2_text=pass2.getText().toString();
                String sodienthoai_text=sodienthoai.getText().toString();
                if(name_text.isEmpty()||email_text.isEmpty()||pass1_text.isEmpty()||pass2_text.isEmpty()){
                    Toast.makeText(Register.this,"Dien Thong Tin",Toast.LENGTH_SHORT).show();
                }
                else if(!pass1_text.equals(pass2_text)){
                    Toast.makeText(Register.this,"Mat Khau Khong Trung",Toast.LENGTH_SHORT).show();
                }
                else{

                    databaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(name_text)){
                                Toast.makeText(Register.this,"Ten da dang ky",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                databaseReference.child("users").child(name_text).child("Name").setValue(name_text);
                                databaseReference.child("users").child(name_text).child("Email").setValue(email_text);
                                databaseReference.child("users").child(name_text).child("Password").setValue(pass1_text);
                                databaseReference.child("users").child(name_text).child("Sodienthoai").setValue(sodienthoai_text);
                                Toast.makeText(Register.this,"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this, Register.class); // Restart the same activity
                                startActivity(intent);
                                finish(); // Close the current instance to prevent back navigation

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }
}