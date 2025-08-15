package top.continew.constant;

import java.util.Arrays;
import top.continew.enums.FormTypeEnum;
import top.continew.enums.JavaTypeEnum;
import top.continew.enums.QueryTypeEnum;
import top.continew.enums.TableHeaderEnum;

/**
 * @author lww
 * @date 2025-06-30 19:00
 */
public interface GenerateConstant {

	String[] FORM_TYPE_OPTIONS = Arrays.stream(FormTypeEnum.values()).map(FormTypeEnum::getDescription).toArray(String[]::new);
	String[] JAVA_TYPE_OPTIONS = Arrays.stream(JavaTypeEnum.values()).map(JavaTypeEnum::getDescription).toArray(String[]::new);
	String[] QUERY_TYPE_OPTIONS = Arrays.stream(QueryTypeEnum.values()).map(QueryTypeEnum::getDescription).toArray(String[]::new);
	String[] COLUMN_LIST = Arrays.stream(TableHeaderEnum.values()).map(TableHeaderEnum::getDescription).toArray(String[]::new);
	String DEFAULT_TEXT = "请选择";

	String excludeTables = "'databasechangelog','databasechangeloglock','gen_config','gen_field_config'";
	String doExcludeFields = "id,createUser,createTime,updateUser,updateTime";
	String detailRespExcludeFields = "id,createUser,createTime,updateUser,updateTime";
	String respExcludeFields = "id,createUser,createTime";

	String formExcludeFields = "createUser,createTime,updateUser,updateTime,deleteUser,deleteTime,delFlag,isDeleted,deletedBy";
	String resExcludeFields = "id,createUser,createTime,updateUser,updateTime,deleteUser,deleteTime,delFlag,isDeleted,deletedBy";
	String requiredExcludeFields = "id,createUser,createTime,updateUser,updateTime,deleteUser,deleteTime,delFlag,isDeleted,deletedBy";
	String queryExcludeFields = "createUser,createTime,updateUser,updateTime,deleteUser,deleteTime,delFlag,isDeleted,deletedBy";

}
