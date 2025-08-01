package top.continew.version;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JavaTemplateEnum implements TemplateEnum {

	Controller400("400/backend/Controller.ftl", "controller", "%sController.java", "4.0.0"),
	Service400("400/backend/Service.ftl", "service", "%sService.java", "4.0.0"),
	ServiceImpl400("400/backend/ServiceImpl.ftl", "service.impl", "%sServiceImpl.java", "4.0.0"),
	Mapper400("400/backend/Mapper.ftl", "mapper", "%sMapper.java", "4.0.0"),
	Entity400("400/backend/Entity.ftl", "model.entity", "%sDO.java", "4.0.0"),
	Req400("400/backend/Req.ftl", "model.req", "%sReq.java", "4.0.0"),
	Query400("400/backend/Query.ftl", "model.query", "%sQuery.java", "4.0.0"),
	DetailResp400("400/backend/DetailResp.ftl", "model.resp", "%sDetailResp.java", "4.0.0"),
	Resp400("400/backend/Resp.ftl", "model.resp", "%sResp.java", "4.0.0"),

	Controller370("370/backend/Controller.ftl", "controller", "%sController.java", "3.7.0"),
	Service370("370/backend/Service.ftl", "service", "%sService.java", "3.7.0"),
	ServiceImpl370("370/backend/ServiceImpl.ftl", "service.impl", "%sServiceImpl.java", "3.7.0"),
	Mapper370("370/backend/Mapper.ftl", "mapper", "%sMapper.java", "3.7.0"),
	Entity370("370/backend/Entity.ftl", "model.entity", "%sDO.java", "3.7.0"),
	Req370("370/backend/Req.ftl", "model.req", "%sReq.java", "3.7.0"),
	Query370("370/backend/Query.ftl", "model.query", "%sQuery.java", "3.7.0"),
	DetailResp370("370/backend/DetailResp.ftl", "model.resp", "%sDetailResp.java", "3.7.0"),
	Resp370("370/backend/Resp.ftl", "model.resp", "%sResp.java", "3.7.0"),

	Controller360("360/backend/Controller.ftl", "controller", "%sController.java", "3.6.0"),
	Service360("360/backend/Service.ftl", "service", "%sService.java", "3.6.0"),
	ServiceImpl360("360/backend/ServiceImpl.ftl", "service.impl", "%sServiceImpl.java", "3.6.0"),
	Mapper360("360/backend/Mapper.ftl", "mapper", "%sMapper.java", "3.6.0"),
	Entity360("360/backend/Entity.ftl", "model.entity", "%sDO.java", "3.6.0"),
	Req360("360/backend/Req.ftl", "model.req", "%sReq.java", "3.6.0"),
	Query360("360/backend/Query.ftl", "model.query", "%sQuery.java", "3.6.0"),
	DetailResp360("360/backend/DetailResp.ftl", "model.resp", "%sDetailResp.java", "3.6.0"),
	Resp360("360/backend/Resp.ftl", "model.resp", "%sResp.java", "3.6.0"),

	Controller350("350/backend/Controller.ftl", "controller", "%sController.java", "3.5.0"),
	Service350("350/backend/Service.ftl", "service", "%sService.java", "3.5.0"),
	ServiceImpl350("350/backend/ServiceImpl.ftl", "service.impl", "%sServiceImpl.java", "3.5.0"),
	Mapper350("350/backend/Mapper.ftl", "mapper", "%sMapper.java", "3.5.0"),
	Entity350("350/backend/Entity.ftl", "model.entity", "%sDO.java", "3.5.0"),
	Req350("350/backend/Req.ftl", "model.req", "%sReq.java", "3.5.0"),
	Query350("350/backend/Query.ftl", "model.query", "%sQuery.java", "3.5.0"),
	DetailResp350("350/backend/DetailResp.ftl", "model.resp", "%sDetailResp.java", "3.5.0"),
	Resp350("350/backend/Resp.ftl", "model.resp", "%sResp.java", "3.5.0"),
	//
	;

	private final String templatePath;
	private final String packageName;
	private final String fileName;
	private final String version;

	@Override
	public boolean firstToLowerCase() {
		return false;
	}

	@Override
	public boolean className2Folder() {
		return false;
	}
}
