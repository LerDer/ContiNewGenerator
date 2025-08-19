package ${packageName}.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

<#if noBase>
import java.io.Serializable;
<#else>
import top.continew.admin.common.base.model.resp.BaseResp;
</#if>
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
 * ${businessName}信息
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
@Schema(description = "${businessName}信息")
public class ${className}Resp<#if noBase> implements Serializable <#else> extends BaseResp </#if>{

    @Serial
    private static final long serialVersionUID = 1L;
<#if fieldConfigs??>
  <#list fieldConfigs as fieldConfig>
    <#if fieldConfig.showInList>

     <#if !noBase && respExcludeFields?seq_contains(fieldConfig.fieldName)>
          <#continue>
     </#if>

    @Schema(description = "${fieldConfig.comment}")
    private ${fieldConfig.fieldType} ${fieldConfig.fieldName};
    </#if>
  </#list>
</#if>

	public static ${className}Resp convert(${className}DO ${apiName}DO) {
		${className}Resp ${apiName}Resp = new ${className}Resp();
		<#if fieldConfigs??>
		  <#list fieldConfigs as fieldConfig>
			<#if fieldConfig.showInList>
		  		<#if respExcludeFields?seq_contains(fieldConfig.fieldName)>
	  			<#continue>
	 			</#if>
		${apiName}Resp.set${fieldConfig.fieldName?cap_first}(${apiName}DO.get${fieldConfig.fieldName?cap_first}());
			</#if>
		  </#list>
		</#if>
		return ${apiName}Resp;
	}
}