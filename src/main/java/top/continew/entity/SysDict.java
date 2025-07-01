package top.continew.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @author lww
 * @date 2025-07-01 11:45
 */
@Data
public class SysDict implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 编码
	 */
	private String code;

}
