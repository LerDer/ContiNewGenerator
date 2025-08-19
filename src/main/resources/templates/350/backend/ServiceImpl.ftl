package ${packageName}.service.impl;
<#list fieldConfigs as field>
<#if field.isPrimary?? && field.isPrimary>
<#assign primaryKey = field.fieldName />
<#assign primaryComment = field.comment />
<#assign primaryType = field.fieldType />
<#break>
</#if>
</#list>
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

<#if mpService>
import top.continew.starter.data.mp.service.impl.ServiceImpl;
<#else>
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import ${packageName}.model.resp.${classNamePrefix}DetailResp;
</#if>
import ${packageName}.model.query.${classNamePrefix}Query;
import ${packageName}.model.req.${classNamePrefix}Req;
import ${packageName}.model.resp.${classNamePrefix}Resp;
import ${packageName}.mapper.${classNamePrefix}Mapper;
import ${packageName}.model.entity.${classNamePrefix}DO;
import ${packageName}.service.${classNamePrefix}Service;
import lombok.extern.slf4j.Slf4j;

/**
 * ${businessName}业务实现
 *
 * @author ${author}
 * @since ${datetime}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ${className}ServiceImpl extends <#if mpService>ServiceImpl<${classNamePrefix}Mapper, ${classNamePrefix}DO> implements ${classNamePrefix}Service<#else>BaseServiceImpl<${classNamePrefix}Mapper, ${classNamePrefix}DO, ${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> implements ${classNamePrefix}Service </#if>{

	private final ${classNamePrefix}Mapper ${apiName}Mapper;

	<#if mpService>
    @Override
    public ${classNamePrefix}Resp create${className}(${classNamePrefix}Req ${apiName}Req){
    }

    @Override
    public Boolean delete${className}(${primaryType} ${primaryKey}){
    }

    @Override
    public ${classNamePrefix}Resp update${className}(${classNamePrefix}Req ${apiName}Req){
    }

    @Override
    public ${classNamePrefix}Resp get${className}(${primaryType} ${primaryKey}){
    }

    @Override
    public PageResp<${classNamePrefix}Resp> page${className}(${classNamePrefix}Query ${apiName}Query){
    }

     @Override
    public void export${className}(${classNamePrefix}Query ${apiName}Query){
    }
    </#if>
}