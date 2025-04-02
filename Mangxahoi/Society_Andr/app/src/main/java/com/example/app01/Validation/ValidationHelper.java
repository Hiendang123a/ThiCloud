package com.example.app01.Validation;

import android.util.Patterns;

import java.util.Calendar;
import java.util.Date;

public class ValidationHelper {

    public static final String EMPTY_USERNAME = "Vui lòng điền username.";
    public static final String EMPTY_PASSWORD = "Vui lòng điền password.";
    public static final String EMPTY_FULLNAME = "Vui lòng điền fullname.";
    public static final String EMPTY_ADDRESS = "Vui lòng điền address.";
    public static final String EMPTY_HOMETOWN = "Vui lòng điền hometown.";
    public static final String UNDER_AGE = "Tuổi phải lớn hơn 18 và nhỏ hơn 150";
    public static final String INVALID_EMAIL = "Username không phải dạng Email.";
    public static final String INVALID_PASSWORD = "Mật khẩu phải có ít nhất 8 kí tự, 1 kí tự viết thường, 1 ký tự viết hoa";

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 8 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*");
    }
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidAge(int age) {
        return age >= 18 && age < 150;
    }

    // Tính tuổi từ ngày sinh (dob: Date)
    public static int calculateAge(Date dob) {
        if (dob == null) return -1; // Tránh lỗi nếu ngày sinh null

        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dob);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public static boolean isValidDob(Date dob) {
        int age = calculateAge(dob);
        return isValidAge(age);
    }
}
