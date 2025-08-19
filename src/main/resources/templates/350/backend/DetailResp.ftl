package ${packageName}.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
<#if noBase>
import java.io.Serializable;
<#else>
import top.continew.admin.common.model.resp.BaseDetailResp;
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
public class ${className}DetailResp<#if noBase> implements Serializable <#else> extends BaseDetailResp </#if>{

    @Serial
    private static final long serialVersionUID = 1L;
<#if fieldConfigs??>
  <#list fieldConfigs as fieldConfig>
  <#if !noBase && detailRespExcludeFields?seq_contains(fieldConfig.fieldName)>
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