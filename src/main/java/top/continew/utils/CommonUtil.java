package top.continew.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * 通用工具
 *
 * @author lww
 */
public class CommonUtil {

	/**
	 * 驼峰转下划线
	 */
	public static String humpToUnderline(String str) {
		String regex = "([A-Z])";
		Matcher matcher = Pattern.compile(regex).matcher(str);
		while (matcher.find()) {
			String target = matcher.group();
			str = str.replaceAll(target, "_" + target.toLowerCase());
		}
		return str;
	}

	/**
	 * 下划线转驼峰
	 */
	public static String underlineToHump(String str) {
		str = str.toLowerCase();
		String regex = "_(.)";
		Matcher matcher = Pattern.compile(regex).matcher(str);
		while (matcher.find()) {
			String target = matcher.group(1);
			str = str.replaceAll("_" + target, target.toUpperCase());
		}
		return str;
	}

	/**
	 * 下划线转驼峰,首字母大写
	 */
	public static String underlineToHump1(String str) {
		str = str.toLowerCase();
		String regex = "_(.)";
		Matcher matcher = Pattern.compile(regex).matcher(str);
		while (matcher.find()) {
			String target = matcher.group(1);
			str = str.replaceAll("_" + target, target.toUpperCase());
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * 驼峰取首字母
	 */
	public static String getFirstWords(String str) {
		StringBuilder sb = new StringBuilder();
		sb.append(str.charAt(0));
		for (int i = 0; i < str.length(); i++) {
			char charAt = str.charAt(i);
			if (Character.isUpperCase(charAt)) {
				sb.append(charAt);
			}
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * 下划线取首字母
	 */
	public static String getFirstWords1(String str) {
		StringBuilder sb = new StringBuilder();
		String[] split = str.split("_");
		for (String string : split) {
			sb.append(string, 0, 1);
		}
		return sb.toString().toLowerCase();
	}

	public static String getNowDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static String dateFormat(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdf.format(date);
		if (format.contains("00:00:00")) {
			format = format.replace("00:00:00", "").trim();
		}
		return format;
	}

	public static String dateFormat(LocalDateTime value) {
		return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public static String getSenstiveString(String value) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		int length = value.length();
		if (length == 1) {
			return "*";
		} else if (length <= 3) {
			return value.charAt(0) + "*" + value.charAt(length - 1);
		} else if (length <= 6) {
			return value.charAt(0) + "**" + value.charAt(length - 1);
		} else {
			return value.substring(0, 2) + "**" + value.substring(length - 2);
		}
	}

	public static String type2JavaType(String type) {
		if (List.of(
				"int",
				"tinyint",
				"smallint",
				"mediumint",
				"integer"
		).contains(type.toLowerCase())) {
			return "Integer";
		} else if (List.of(
				"varchar",
				"char",
				"text",
				"mediumtext",
				"longtext",
				"tinytext",
				"json"
		).contains(type.toLowerCase())) {
			return "String";
		} else if (List.of(
				"datetime",
				"timestamp"
		).contains(type.toLowerCase())) {
			return "LocalDateTime";
		} else if (type.equalsIgnoreCase("date")) {
			return "LocalDate";
		} else if (type.equalsIgnoreCase("bigint")) {
			return "Long";
		} else if (type.equalsIgnoreCase("decimal")) {
			return "BigDecimal";
		} else if (type.equalsIgnoreCase("float")) {
			return "Float";
		} else if (type.equalsIgnoreCase("double")) {
			return "Double";
		} else if (type.equalsIgnoreCase("bit")) {
			return "Boolean";
		} else {
			return "未知类型：" + type;
		}
	}
}
