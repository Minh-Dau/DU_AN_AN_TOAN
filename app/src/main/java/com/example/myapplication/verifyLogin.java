package com.example.myapplication;

import android.os.AsyncTask;

public class verifyLogin {
    public static void sendEmailOTP(String emailInput, OTPCallback callback) {
        String email = emailInput.trim();

        String otp = generateOTP(); // Tạo mã OTP

        // Chạy AsyncTask để gửi OTP
        new SendOTPAsyncTask(email, otp, callback).execute();
    }

    // Tạo mã OTP 6 chữ số
    private static String generateOTP() {
        int otp = (int) (Math.random() * 900000) + 100000; // Mã OTP 6 chữ số
        return String.valueOf(otp);
    }

    // AsyncTask để gửi OTP trong background
    private static class SendOTPAsyncTask extends AsyncTask<Void, Void, Result> {
        private String email;
        private String otp;
        private OTPCallback callback;

        SendOTPAsyncTask(String email, String otp, OTPCallback callback) {
            this.email = email;
            this.otp = otp;
            this.callback = callback;
        }

        @Override
        protected Result doInBackground(Void... voids) {
            try {
                // Gửi OTP email (giả sử EmailUtil trả về true nếu thành công, ngược lại là false)
                boolean success = EmailUtil.sendOTPEmail(email, otp);
                return new Result(success, otp); // Trả về cả kết quả và mã OTP
            } catch (Exception e) {
                return new Result(false, otp); // Gửi thất bại
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            if (callback != null) {
                callback.onResult(result.success, result.otp); // Trả kết quả và mã OTP về thông qua callback
            }
        }
    }

    // Callback để trả kết quả
    public interface OTPCallback {
        void onResult(boolean success, String otp); // Thêm OTP vào callback
    }

    // Lớp Result để chứa kết quả và mã OTP
    private static class Result {
        boolean success;
        String otp;

        Result(boolean success, String otp) {
            this.success = success;
            this.otp = otp;
        }
    }
}
