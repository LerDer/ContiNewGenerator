package ${packageName}.service;
<#list fieldConfigs as field>
<#if field.isPrimary?? && field.isPrimary>
<#assign primaryKey = field.fieldName />
<#assign primaryComment = field.comment />
<#assign primaryType = field.fieldType />
<#break>
</#if>
</#list>
<#if mpService>
import ${packageName}.model.entity.${classNamePrefix}DO;
import top.continew.starter.data.mp.service.IService;
<#else>
import top.continew.starter.extension.crud.service.BaseService;
import ${packageName}.model.resp.${classNamePrefix}DetailResp;
</#if>
import ${packageName}.model.query.${classNamePrefix}Query;
import ${packageName}.model.req.${classNamePrefix}Req;
import ${packageName}.model.resp.${classNamePrefix}Resp;

/**
 * ${businessName}业务接口
 *
 * @author ${author}
 * @since ${datetime}
 */
public interface ${className}Service extends <#if mpService>IService<${classNamePrefix}DO><#else>BaseService<${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> </#if>{
<#if mpService>
    /**
     * 创建 ${businessName}
     *
     * @param ${apiName}Req
     * @return ${classNamePrefix}Resp
     */
    ${classNamePrefix}Resp create${className}(${classNamePrefix}Req ${apiName}Req);

    /**
     * 删除 ${businessName}
     *
     * @param ${primaryKey}
     * @return Boolean
     */
    Boolean delete${className}(${primaryType} ${primaryKey});

    /**
     * 更新 ${businessName}
     *
     * @param ${apiName}Req
     * @return ${classNamePrefix}Resp
     */
    ${classNamePrefix}Resp update${className}(${classNamePrefix}Req ${apiName}Req);

    /**
     * 查询 ${businessName}
     *
     * @param ${primaryKey}
     * @return ${classNamePrefix}Resp
     */
    ${classNamePrefix}Resp get${className}(${primaryType} ${primaryKey});

    /**
     * 分页查询 ${businessName}
     *
     * @param ${apiName}Query
     * @return PageResp<${classNamePrefix}Resp>
     */
    PageResp<${classNamePrefix}Resp> page${className}(${classNamePrefix}Query ${apiName}Query);

     /**
     * 导出 ${businessName}
     *
     * @param ${apiName}Query
     */
    void export${className}(${classNamePrefix}Query ${apiName}Query);
</#if>
}