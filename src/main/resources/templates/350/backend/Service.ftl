package ${packageName}.service;

<#if mpService>
import ${packageName}.model.entity.${classNamePrefix}DO;
import top.continew.starter.data.mp.service.IService;
<#else>
import top.continew.starter.extension.crud.service.BaseService;
import ${packageName}.model.query.${classNamePrefix}Query;
import ${packageName}.model.req.${classNamePrefix}Req;
import ${packageName}.model.resp.${classNamePrefix}DetailResp;
import ${packageName}.model.resp.${classNamePrefix}Resp;
</#if>

/**
 * ${businessName}业务接口
 *
 * @author ${author}
 * @since ${datetime}
 */
public interface ${className}Service extends <#if mpService>IService<${classNamePrefix}DO><#else>BaseService<${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> </#if>{}