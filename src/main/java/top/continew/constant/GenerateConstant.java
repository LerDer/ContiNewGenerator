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

	//DO
	String doTemplatePath = "backend/Entity.ftl";
	String doPackageName = "model.entity";
	String doExcludeFields = "id,createUser,createTime,updateUser,updateTime";

	//Query
	String queryTemplatePath = "backend/Query.ftl";
	String queryPackageName = "model.query";

	//Req
	String reqTemplatePath = "backend/Req.ftl";
	String reqPackageName = "model.req";

	//Resp
	String respTemplatePath = "backend/Resp.ftl";
	String respPackageName = "model.resp";
	String respExcludeFields = "id,createUser,createTime";

	//DetailResp
	String detailRespTemplatePath = "backend/DetailResp.ftl";
	String detailRespPackageName = "model.resp";
	String detailRespExcludeFields = "id,createUser,createTime,updateUser,updateTime";

	//Mapper
	String mapperTemplatePath = "backend/Mapper.ftl";
	String mapperPackageName = "mapper";

	//MapperXml
	String mapperXmlTemplatePath = "backend/MapperXml.ftl";
	String mapperXmlPackageName = "mapper";
	String mapperXmlExtenstion = ".xml";
	String mapperXmlSuffex = "Mapper";

	//Service
	String serviceTemplatePath = "backend/Service.ftl";
	String servicePackageName = "service";

	//ServiceImpl
	String serviceImplTemplatePath = "backend/ServiceImpl.ftl";
	String serviceImplPackageName = "service.impl";

	//Controller
	String controllerTemplatePath = "backend/Controller.ftl";
	String controllerPackageName = "controller";

	//Api
	String apiTemplatePath = "frontend/api.ftl";
	String apiPackageName = "src/apis";
	String apiExtenstion = ".ts";
	Boolean apiBackend = false;

	//Index
	String indexTemplatePath = "frontend/index.ftl";
	String indexPackageName = "src/views";
	String indexExtenstion = ".vue";
	Boolean indexBackend = false;

	//Modal
	String addModelTemplatePath = "frontend/AddModal.ftl";
	String addModelPackageName = "src/views";
	String addModelExtenstion = ".vue";
	Boolean addModelBackend = false;

	//DetailDrawer
	String detailDrawerTemplatePath = "frontend/DetailDrawer.ftl";
	String detailDrawerPackageName = "src/views";
	String detailDrawerExtenstion = ".vue";
	Boolean detailDrawerBackend = false;

	//Menu
	String menuTemplatePath = "backend/Menu.ftl";
	String menuPackageName = "sql";
	String menuExtenstion = ".sql";
	Boolean menuBackend = true;

	String formExcludeFields = "id,createUser,createTime,updateUser,updateTime";
	String requiredExcludeFields = "id,createUser,createTime,updateUser,updateTime,description";
	String queryExcludeFields = "createUser,createTime,updateUser,updateTime,description";

}
