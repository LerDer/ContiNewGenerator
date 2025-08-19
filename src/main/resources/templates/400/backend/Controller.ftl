package ${packageName}.controller;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

<#if NoApi>
import org.springframework.web.bind.annotation.RequestMapping;
<#else>
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.base.controller.BaseController;
import ${packageName}.model.query.${classNamePrefix}Query;
import ${packageName}.model.req.${classNamePrefix}Req;
import ${packageName}.model.resp.${classNamePrefix}DetailResp;
import ${packageName}.model.resp.${classNamePrefix}Resp;
</#if>
import ${packageName}.service.${classNamePrefix}Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ${businessName}管理 API
 *
 * @author ${author}
 * @since ${datetime}
 */
@Tag(name = "${businessName}管理 API")
@RestController
@Slf4j
@RequiredArgsConstructor
<#if NoApi>
@RequestMapping("/${apiModuleName}/${apiName}")
<#else>
@CrudRequestMapping(value = "/${apiModuleName}/${apiName}", api = {Api.PAGE, Api.DETAIL, Api.ADD, Api.UPDATE, Api.DELETE, Api.EXPORT})
</#if>
public class ${className}Controller<#if !NoApi> extends BaseController<${classNamePrefix}Service, ${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> </#if>{
		private final ${classNamePrefix}Service ${apiName}Service;
}