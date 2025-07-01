package top.continew.entity;

import java.io.Serializable;
import java.math.BigInteger;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import top.continew.utils.CommonUtil;

/**
 * <p>
 * 列对象
 * </p>
 *
 * @author lww
 * @since 2021-04-02
 */
@Data
public class SqlColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 列名
	 */
	private String columnName;

	/**
	 * 列序号
	 */
	private Long ordinalPosition;

	/**
	 * 列默认值
	 */
	private String columnDefault;

	/**
	 * 是否可空 YES/NO
	 */
	private String isNullable;

	/**
	 * 数据类型 varchar,int,datetime,text等
	 */
	private String dataType;

	/**
	 * 字节类型最大长度
	 */
	private Long characterMaximumLength;

	/**
	 * 数据类型最大字节数
	 */
	private Long characterOctetLength;

	/**
	 * 数字类型最大精度
	 */
	private BigInteger numericPrecision;

	/**
	 * 数字类型最大小数位数
	 */
	private BigInteger numericScale;

	/**
	 * 字符集名称 如 utf8mb4
	 */
	private String characterSetName;

	/**
	 * 字符集排序规则 如 utf8mb4_0900_ai_ci
	 */
	private String collationName;

	/**
	 * 列类型 如 varchar(255)，int，datetime, int(1) unsigned zerofill等
	 */
	private String columnType;

	/**
	 * 列是否是主键 值为PRI是主键
	 */
	private String columnKey;

	/**
	 * 列额外信息 如 auto_increment
	 */
	private String extra;

	/**
	 * 列注释
	 */
	private String columnComment;

	private String javaType;

	private String javaField;

	private Boolean number;

	private Boolean string;

	private Boolean date;

	private Boolean datetime;

	private Boolean bool;

	public Boolean isBool() {
		return CommonUtil.isBooleanType(this.dataType);
	}

	public Boolean isString() {
		return CommonUtil.isStringType(this.dataType);
	}

	public Boolean isDate() {
		return CommonUtil.isDateType(this.dataType);
	}

	public Boolean isDatetime() {
		return CommonUtil.isDateTimeType(this.dataType);
	}

	public Boolean isNumber() {
		return CommonUtil.isNumberType(this.dataType);
	}

	public String getJavaType() {
		return CommonUtil.type2JavaType(this.dataType);
	}

	public String getJavaField() {
		return CommonUtil.underlineToHump(this.columnName);
	}

	public String getColumnNameAndComments() {
		return this.columnName + (StringUtils.isBlank(this.columnComment) ? "" : " _ " + this.columnComment);
	}

	public boolean isPrimaryKey() {
		return "PRI".equals(this.columnKey);
	}

	public boolean isIndex() {
		return StringUtils.isNotBlank(this.columnKey) && !isPrimaryKey();
	}

	public boolean isNULLAble() {
		return "YES".equals(this.isNullable);
	}

	@Override
	public String toString() {
		return "SqlColumn{" +
				"columnName='" + columnName + '\'' +
				", columnComment='" + columnComment + '\'' +
				'}';
	}
}
