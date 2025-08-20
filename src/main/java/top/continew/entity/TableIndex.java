package top.continew.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @author lww
 * @date 2025-07-16 11:59
 */
@Data
public class TableIndex implements Serializable {

	private static final long serialVersionUID = 1L;

	//Table ：索引所在的表名。
	//Non_unique ：表示索引是否唯一。0 表示该索引是唯一索引（UNIQUE），1 表示该索引不是唯一索引。
	//Key_name ：索引的名称。
	//Seq_in_index ：列在索引中的顺序位置。
	//Column_name ：索引列的名称。
	//Collation ：排序规则，如 A 表示升序，D 表示降序。
	//Cardinality ：索引基数，即索引中不同值的数量估计。
	//Sub_part ：索引前缀长度，如果是 NULL 则表示没有使用前缀索引。
	//Packed ：指示键如何压缩，通常为 NULL。
	//Null ：如果列可能包含 NULL 值，则为 YES，否则为 NO。
	//Index_type ：索引类型，如 B-tree、Hash 等。
	//Comment ：其他注释信息。
	private String tableName;
	private Long nonUnique;
	private String indexName;
	private Long seqInIndex;
	private String columnName;
	private String collation;
	private Long cardinality;
	private String nullable;
	private String indexType;
	private String comment;
	private String indexComment;
	private String isVisible;
	private String expression;
}
