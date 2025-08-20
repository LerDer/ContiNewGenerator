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
import java.util.List;
import ${packageName}.model.entity.${classNamePrefix}DO;
//import top.continew.starter.data.mp.service.IService;
import com.baomidou.mybatisplus.extension.service.IService;
<#else>
import top.continew.starter.extension.crud.service.BaseService;
import ${packageName}.model.resp.${classNamePrefix}DetailResp;
</#if>
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import ${packageName}.model.query.${classNamePrefix}Query;
import ${packageName}.model.req.${classNamePrefix}Req;
import ${packageName}.model.resp.${classNamePrefix}Resp;
import jakarta.servlet.http.HttpServletResponse;

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
     * @param ${apiName}Req 创建请求参数
     * @return ${classNamePrefix}Resp
     */
    ${primaryType} create${className}(${classNamePrefix}Req ${apiName}Req);

    /**
     * 删除 ${businessName}
     *
     * @param ${primaryKey} 主键
     * @return Boolean
     */
    Boolean delete${className}(${primaryType} ${primaryKey});

    /**
     * 批量删除 ${businessName}
     *
     * @param ids 主键列表
     * @return Boolean
     */
    Boolean delete${className}s(List<Long> ids);

    /**
     * 更新 ${businessName}
     *
     * @param ${primaryKey} 主键
     * @param ${apiName}Req 更新请求参数
     * @return ${classNamePrefix}Resp
     */
    ${classNamePrefix}Resp update${className}(${primaryType} ${primaryKey}, ${classNamePrefix}Req ${apiName}Req);

    /**
     * 查询 ${businessName}
     *
     * @param ${primaryKey} 主键
     * @return ${classNamePrefix}Resp
     */
    ${classNamePrefix}Resp get${className}(${primaryType} ${primaryKey});

    /**
     * 查询列表 ${businessName}
     *
     * @param ${apiName}Query 查询请求参数
     * @return List<${classNamePrefix}Resp>
     */
	List<${classNamePrefix}Resp> list${className}(${classNamePrefix}Query ${apiName}Query);

    /**
     * 分页查询 ${businessName}
     *
     * @param ${apiName}Query 查询请求参数
     * @param pageQuery 分页请求参数
     * @return PageResp<${classNamePrefix}Resp>
     */
    PageResp<${classNamePrefix}Resp> page${className}(${classNamePrefix}Query ${apiName}Query, PageQuery pageQuery);

     /**
     * 导出 ${businessName}
     *
     * @param ${apiName}Query 查询请求参数
     */
    void export${className}(${classNamePrefix}Query ${apiName}Query, HttpServletResponse response);
</#if>
}