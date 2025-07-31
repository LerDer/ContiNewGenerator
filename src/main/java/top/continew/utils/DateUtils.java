package top.continew.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lww
 * @date 2025-07-31 13:14
 */
public class DateUtils {

	public static String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

}
