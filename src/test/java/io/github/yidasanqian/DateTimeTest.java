package io.github.yidasanqian;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeTest {

    @Test
    public void testDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        System.out.println("DateTimeTest.testDateFormat currentDate: " + currentDate);
    }

    @Test
    public void testDateForma2t() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        System.out.println("DateTimeTest.testDateFormat2 currentDate: " + currentDate);
    }
}
