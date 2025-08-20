package ${packageName}.controller;
<#list fieldConfigs as field>
<#if field.isPrimary?? && field.isPrimary>
<#assign primaryKey = field.fieldName />
<#assign primaryComment = field.comment />
<#assign primaryType = field.fieldType />
<#break>
</#if>
</#list>

<#if NoApi>
import java.util.List;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import org.springdoc.core.annotations.ParameterObject;
<#else>
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.base.controller.BaseController;
import ${packageName}.model.resp.${classNamePrefix}DetailResp;
</#if>
import ${packageName}.model.query.${classNamePrefix}Query;
import ${packageName}.model.req.${classNamePrefix}Req;
import ${packageName}.model.resp.${classNamePrefix}Resp;
import org.springframework.web.bind.annotation.RestController;
import ${packageName}.service.${classNamePrefix}Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletResponse;

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

		<#if NoApi>
	@Operation(summary = "新增${businessName}")
	@SaCheckPermission(value = "${apiModuleName}:${apiName}:create")
	@PostMapping("/create")
	public ${primaryType} create${className}(@RequestBody @Validated ${classNamePrefix}Req ${apiName}Req) {
		return ${apiName}Service.create${className}(${apiName}Req);
	}

	@Operation(summary = "删除${businessName}")
	@SaCheckPermission(value = "${apiModuleName}:${apiName}:delete")
	@Parameters({
			@Parameter(name = "${primaryKey!''}", description = "${primaryComment!''}", required = true)
	})
	@PostMapping("/delete")
	public Boolean delete${className}(${primaryType} ${primaryKey}) {
		return ${apiName}Service.delete${className}(${primaryKey});
	}

	@Operation(summary = "修改${businessName}")
	@SaCheckPermission(value = "${apiModuleName}:${apiName}:update")
	@PostMapping("/update")
	public ${classNamePrefix}Resp update${className}(@RequestBody @Validated ${classNamePrefix}Req ${apiName}Req) {
		return ${apiName}Service.update${className}(${apiName}Req);
	}

	@Operation(summary = "获取${businessName}详情")
	@SaCheckPermission(value = "${apiModuleName}:${apiName}:query")
	@Parameters({
			@Parameter(name = "${primaryKey}", description = "${primaryComment}", required = true)
	})
	@GetMapping("/detail")
	public ${classNamePrefix}Resp get${className}(${primaryType} ${primaryKey}) {
		return ${apiName}Service.get${className}(${primaryKey});
	}

	@Operation(summary = "查询${businessName}列表")
	@SaCheckPermission(value = "${apiModuleName}:${apiName}:query")
	@PostMapping("/list")
	public List<${classNamePrefix}Resp> list${className}(@ParameterObject ${classNamePrefix}Query ${apiName}Query) {
		return ${apiName}Service.list${className}(${apiName}Query);
	}

	@Operation(summary = "分页查询${businessName}列表")
	@SaCheckPermission(value = "${apiModuleName}:${apiName}:query")
	@GetMapping("/page")
	public PageResp<${classNamePrefix}Resp> page${className}(@ParameterObject ${classNamePrefix}Query ${apiName}Query, @ParameterObject PageQuery pageQuery) {
		return ${apiName}Service.page${className}(${apiName}Query, pageQuery);
	}

	@Operation(summary = "导出${businessName}列表")
	@SaCheckPermission(value = "${apiModuleName}:${apiName}:query")
	@GetMapping("/export")
	public void export${className}(@ParameterObject ${classNamePrefix}Query ${apiName}Query, HttpServletResponse response) {
		${apiName}Service.export${className}(${apiName}Query, response);
	}
	</#if>
}