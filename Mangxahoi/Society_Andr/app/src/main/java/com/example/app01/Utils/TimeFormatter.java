package com.example.app01.Utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFormatter {
    public static String formatTime(Date sentTime) {
        Calendar sentCalendar = Calendar.getInstance();
        sentCalendar.setTime(sentTime);

        Calendar now = Calendar.getInstance();
        long diffMillis = now.getTimeInMillis() - sentCalendar.getTimeInMillis();
        long diffHours = diffMillis / (1000 * 60 * 60);
        long diffDays = diffHours / 24;
        long diffMonths = diffDays / 30;
        long diffYears = diffDays / 365;

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (diffHours < 24) {
            return timeFormat.format(sentTime); // Hiển thị dạng HH:mm nếu trong ngày
        } else if (diffDays < 30) {
            return diffDays + " ngày trước";
        } else if (diffMonths < 12) {
            return diffMonths + " tháng trước";
        } else {
            return diffYears + " năm trước";
        }
    }
    public static String formatDate(Date date) {
        if (date == null) return null;  // Nếu `cursor` là null thì return null
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Chỉnh múi giờ VN (+07:00)
        return sdf.format(date);
    }
}