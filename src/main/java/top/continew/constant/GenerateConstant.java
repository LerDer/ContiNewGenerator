package top.continew.constant;

/**
 * @author lww
 * @date 2025-06-30 19:00
 */
public interface GenerateConstant {

	String excludeTables = "DATABASECHANGELOG,DATABASECHANGELOGLOCK,gen_config,gen_field_config";

	String doTemplatePath = "backend/Entity.ftl";

	String doPackageName = "model.entity";

	String doExcludeFields = "id,createUser,createTime,updateUser,updateTime";

	String queryTemplatePath = "backend/Query.ftl";

	String queryPackageName = "model.query";

	String reqTemplatePath = "backend/Req.ftl";

	String reqPackageName = "model.req";

	String respTemplatePath = "backend/Res.ftl";

	String respPackageName = "model.resp";

	String respExcludeFields = "id,createUser,createTime";

	String detailRespTemplatePath = "backend/DetailResp.ftl";

	String detailRespPackageName = "model.resp";

	String detailRespExcludeFields = "id,createUser,createTime,updateUser,updateTime";

	String mapperTemplatePath = "backend/Mapper.ftl";

	String mapperPackageName = "mapper";

	String mapperXmlTemplatePath = "backend/MapperXml.ftl";

	String mapperXmlPackageName = "mapper";

	String mapperXmlExtenstion = "xml";

	String mapperXmlSuffex = "Mapper";

	String serviceTemplatePath = "backend/Service.ftl";

	String servicePackageName = "service";

	String serviceImplTemplatePath = "backend/ServiceImpl.ftl";

	String serviceImplPackageName = "service.impl";

	String controllerTemplatePath = "backend/Controller.ftl";

	String controllerPackageName = "controller";

	String apiTemplatePath = "frontend/api.ftl";

	String apiPackageName = "src/apis";

	String apiExtenstion = ".ts";

	Boolean apiBackend = false;

	String indexTemplatePath = "frontend/index.ftl";

	String indexPackageName = "src/views";

	String indexExtenstion = ".vue";

	Boolean indexBackend = false;

	String addModelTemplatePath = "frontend/AddModal.ftl";

	String addModelPackageName = "src/views";

	String addModelExtenstion = ".vue";

	Boolean addModelBackend = false;

	String detailDrawerTemplatePath = "frontend/DetailDrawer.ftl";

	String detailDrawerPackageName = "src/views";

	String detailDrawerExtenstion = ".vue";

	Boolean detailDrawerBackend = false;

	String menuTemplatePath = "backend/Menu.ftl";

	String menuPackageName = "sql";

	String menuExtenstion = ".sql";

	Boolean menuBackend = true;

	String formExcludeFields = "id,createUser,createTime,updateUser,updateTime";
	String requiredExcludeFields = "id,createUser,createTime,updateUser,updateTime,description";
	String queryExcludeFields = "createUser,createTime,updateUser,updateTime,description";
	
}
