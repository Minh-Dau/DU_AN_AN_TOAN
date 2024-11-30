package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class verify_otp3 extends AppCompatActivity {

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private String sentOtp; // Mã OTP nhận từ send_otp
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp3);
        // Retrieve data from Intent
        String userID = getIntent().getStringExtra("ID");
        String userName = getIntent().getStringExtra("name");
        String userEmail = getIntent().getStringExtra("email");
        String userPhone = getIntent().getStringExtra("Sodienthoai");
        String userPass = getIntent().getStringExtra("Password");
        TextView textMobile = findViewById(R.id.textMobile);
        textMobile.setText(String.format(
                "+84-%s", getIntent().getStringExtra("Sodienthoai")
        ));
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setupOTPInputs();

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Button btnVerify = findViewById(R.id.btnVerifyOTP);

        // Lấy mã OTP từ send_otp qua Intent
        sentOtp = getIntent().getStringExtra("verificationId");

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputCode1.getText().toString().trim().isEmpty() ||
                        inputCode2.getText().toString().trim().isEmpty() ||
                        inputCode3.getText().toString().trim().isEmpty() ||
                        inputCode4.getText().toString().trim().isEmpty() ||
                        inputCode5.getText().toString().trim().isEmpty() ||
                        inputCode6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(verify_otp3.this, "Please enter a valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Combine the OTP digits entered by the user
                String enteredCode = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();

                // Compare the entered code with the sent OTP
                if (enteredCode.equals(sentOtp)) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnVerify.setVisibility(View.INVISIBLE);

                    // If the OTP is correct, update the user's information
                    Toast.makeText(verify_otp3.this, "Mã chính xác ", Toast.LENGTH_SHORT).show();

                    // Directly update the specific user data in Firebase by their phone number
                    databaseReference.orderByChild("ID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // If the user exists, update their data
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().child("Email").setValue(userEmail);
                                    snapshot.getRef().child("Name").setValue(userName);
                                    snapshot.getRef().child("Sodienthoai").setValue(userPhone); // Optionally update the phone if needed
                                    snapshot.getRef().child("Password").setValue(userPass)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(verify_otp3.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                                    Intent intent =new Intent(getApplicationContext(), show.class);
                                                    intent.putExtra("ID", userID);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("email", userEmail);
                                                    intent.putExtra("Sodienthoai", userPhone);
                                                    intent.putExtra("Password", userPass);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(verify_otp3.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // User not found, handle accordingly
                                Toast.makeText(verify_otp3.this, "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(verify_otp3.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // If the OTP is incorrect, show an error message
                    Toast.makeText(verify_otp3.this, "Mã sai.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new OTPTextWatcher(inputCode2));
        inputCode2.addTextChangedListener(new OTPTextWatcher(inputCode3));
        inputCode3.addTextChangedListener(new OTPTextWatcher(inputCode4));
        inputCode4.addTextChangedListener(new OTPTextWatcher(inputCode5));
        inputCode5.addTextChangedListener(new OTPTextWatcher(inputCode6));
    }

    // TextWatcher helper class to handle OTP input focus
    private class OTPTextWatcher implements TextWatcher {
        private final EditText nextInput;

        public OTPTextWatcher(EditText nextInput) {
            this.nextInput = nextInput;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().trim().isEmpty() && nextInput != null) {
                nextInput.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
