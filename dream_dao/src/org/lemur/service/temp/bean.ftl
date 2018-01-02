<#function castType type>
	<#local type = type?lower_case>
	<#if type?index_of("int") gte 0 || type?index_of("float") gte 0 || type?index_of("bit") gte 0 || type?index_of("double") gte 0>
		<#return "int">
	<#else>
		<#return "String">
	</#if>
</#function>
package ${daoPath};

import org.lemur.dao.annotation.Id;

public class ${className} {
	<#list table as t>
	<#if t.primary>
	@Id
	</#if>
	private ${castType(t.type)} ${t.columnName};
	</#list>
	
	<#list table as t>
	public ${castType(t.type)} get${t.columnName?cap_first}() {
		return ${t.columnName};
	}
	public void set${t.columnName?cap_first}(${castType(t.type)} ${t.columnName}) {
		this.${t.columnName} = ${t.columnName};
	}
	</#list>
	
}
