package com.lhz.netty.first;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by: hz.lai
 * Date: 2021/7/2
 * Description:
 */
public class UnixTime {
    private final long value;

    public UnixTime() {
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }

    public UnixTime(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String toString() {
        Date date = new Date((value() - 2208988800L) * 1000L);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

}
