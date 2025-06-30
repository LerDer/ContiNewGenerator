package top.continew.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 表对象
 * </p>
 *
 * @author lww
 * @since 2021-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SqlTable implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 说明
	 */
	private String tableComment;

	public String getTableNameComment() {
		return tableName + (StringUtils.isBlank(tableComment) ? "" : "-" + tableComment);
	}
}
