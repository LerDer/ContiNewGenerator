package ${packageName}.service;


<#if mpService>
import ${packageName}.model.entity.${classNamePrefix}DO;
import top.continew.starter.data.mp.service.IService;
<#else>
import top.continew.admin.common.base.service.BaseService;
import ${packageName}.model.entity.${classNamePrefix}DO;
import ${packageName}.model.query.${classNamePrefix}Query;
import ${packageName}.model.req.${classNamePrefix}Req;
import ${packageName}.model.resp.${classNamePrefix}DetailResp;
import ${packageName}.model.resp.${classNamePrefix}Resp;
import top.continew.starter.data.service.IService;
</#if>

/**
 * ${businessName}业务接口
 *
 * @author ${author}
 * @since ${datetime}
 */
public interface ${className}Service extends <#if mpService>IService<${classNamePrefix}DO><#else>BaseService<${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req>, IService<${classNamePrefix}DO> </#if>{}