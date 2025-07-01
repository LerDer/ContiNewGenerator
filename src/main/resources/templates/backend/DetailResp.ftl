package ${packageName}.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;
<#if imports??>
    <#list imports as className>
import ${className};
    </#list>
</#if>
import java.io.Serial;
<#if hasTimeField>
import java.time.*;
</#if>
<#if hasBigDecimalField>
import java.math.BigDecimal;
</#if>

/**
 * ${businessName}详情信息
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "${businessName}详情信息")
public class ${className}DetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;
<#if fieldConfigs??>
  <#list fieldConfigs as fieldConfig>
     <#if detailRespExcludeFields?seq_contains(fieldConfig.fieldName)>
          <#continue>
     </#if>

    /**
     * ${fieldConfig.comment}
     */
    @Schema(description = "${fieldConfig.comment}")
    @ExcelProperty(value = "${fieldConfig.comment}")
    private ${fieldConfig.fieldType} ${fieldConfig.fieldName};
  </#list>
</#if>
}