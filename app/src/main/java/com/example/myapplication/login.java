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

public class login extends AppCompatActivity {
    Button nhan_dangnhap;
    EditText name,passsword;
    TextView nhan_dangky;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://duan-dff9f-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nhan_dangnhap=findViewById(R.id.btn_dangnhap);
        name=findViewById(R.id.edit_name);
        passsword=findViewById(R.id.etPassword);
        nhan_dangky=findViewById(R.id.edit_dangky);
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
    }
}