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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import top.continew.starter.data.util.QueryWrapperHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import top.continew.starter.data.mp.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
<#else>
import top.continew.admin.common.base.service.BaseServiceImpl;
</#if>
import ${packageName}.model.resp.${classNamePrefix}DetailResp;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import ${packageName}.model.query.${classNamePrefix}Query;
import ${packageName}.model.req.${classNamePrefix}Req;
import ${packageName}.model.resp.${classNamePrefix}Resp;
import ${packageName}.mapper.${classNamePrefix}Mapper;
import ${packageName}.model.entity.${classNamePrefix}DO;
import ${packageName}.service.${classNamePrefix}Service;
import lombok.extern.slf4j.Slf4j;
import top.continew.starter.core.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import top.continew.starter.excel.util.ExcelUtils;

/**
 * ${businessName}业务实现
 *
 * @author ${author}
 * @since ${datetime}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ${className}ServiceImpl extends <#if mpService>ServiceImpl<${classNamePrefix}Mapper, ${classNamePrefix}DO> implements ${classNamePrefix}Service<#else>BaseServiceImpl<${classNamePrefix}Mapper, ${classNamePrefix}DO, ${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> implements ${classNamePrefix}Service </#if> {

	private final ${classNamePrefix}Mapper ${apiName}Mapper;

<#if mpService>
    @Override
    public ${primaryType} create${className}(${classNamePrefix}Req ${apiName}Req){
        ${className}DO convert = ${classNamePrefix}Req.convert(${apiName}Req);
        int insert = ${apiName}Mapper.insert(convert);
        if (insert == 0) {
            throw new BusinessException("新增${businessName}失败");
        }
        return convert.get${primaryKey?cap_first}();
    }

    @Override
    public Boolean delete${className}(${primaryType} ${primaryKey}){
        int delete = ${apiName}Mapper.deleteById(${primaryKey});
        if (delete == 0) {
            throw new BusinessException("删除${businessName}失败");
        }
        return true;
    }

    @Override
    public ${classNamePrefix}Resp update${className}(${classNamePrefix}Req ${apiName}Req){
        ${primaryType} ${primaryKey} = ${apiName}Req.get${primaryKey?cap_first}();
        if (${primaryKey} == null) {
            throw new BusinessException("${businessName}${primaryKey}不能为空");
        }
        ${className}DO convert = ${classNamePrefix}Req.convert(${apiName}Req);
        int update = ${apiName}Mapper.updateById(convert);
        if (update == 0) {
            throw new BusinessException("更新${businessName}失败");
        }
        return ${classNamePrefix}Resp.convert(convert);
    }

    @Override
    public ${classNamePrefix}Resp get${className}(${primaryType} ${primaryKey}){
        ${className}DO ${apiName}DO = ${apiName}Mapper.selectById(${primaryKey});
        if (${apiName}DO == null) {
            throw new BusinessException(${primaryKey} + " ${businessName}不存在");
        }
        return ${classNamePrefix}Resp.convert(${apiName}DO);
    }

    @Override
    public PageResp<${classNamePrefix}Resp> page${className}(${classNamePrefix}Query ${apiName}Query, PageQuery pageQuery){
        QueryWrapper<${className}DO> build = QueryWrapperHelper.build(${apiName}Query);
        Page<${className}DO> ${apiName}DOPage = ${apiName}Mapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), build);
        PageResp<${classNamePrefix}Resp> pageResp = new PageResp<>();
        pageResp.setTotal(${apiName}DOPage.getTotal());
        pageResp.setList(${apiName}DOPage.getRecords().stream().map(${classNamePrefix}Resp::convert).toList());
        return pageResp;
    }

    @Override
    public void export${className}(${classNamePrefix}Query ${apiName}Query, HttpServletResponse response){
        QueryWrapper<${className}DO> build = QueryWrapperHelper.build(${apiName}Query);
        List<${className}DO> ${apiName}DOList = ${apiName}Mapper.selectList(build);
        List<${classNamePrefix}DetailResp> list = ${apiName}DOList.stream().map(${classNamePrefix}DetailResp::convert).toList();
		ExcelUtils.export(list, "导出数据", ${classNamePrefix}DetailResp.class, response);
    }
    </#if>
}