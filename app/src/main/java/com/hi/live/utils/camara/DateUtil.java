package com.hi.live.utils.camara;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getTimeAgo(String dateString, Context context) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            long timeInMillis = date.getTime();
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - timeInMillis;

            long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference);

            if (hours > 0) {
                return String.format(Locale.getDefault(), "%d %s ago", hours, hours == 1 ? "hour" : "hours");
            } else {
                return String.format(Locale.getDefault(), "%d %s ago", minutes, minutes == 1 ? "minute" : "minutes");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
