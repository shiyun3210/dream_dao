<#function castType type>
	<#local type = type?lower_case>
	<#if type?index_of("int") gte 0 || type?index_of("float") gte 0 || type?index_of("bit") gte 0 || type?index_of("double") gte 0>
		<#return "int">
	<#else>
		<#return "String">
	</#if>
</#function>
package ${packages};

import java.io.Serializable;

public class ${className} implements Serializable {

	private static final long serialVersionUID = 1L;
	
    	<#list table as t>
    	/**
         * ${t.description}
         */
    	private ${castType(t.type)} ${t.columnName?uncap_first};
    	</#list>
    	
    	
    	<#list table as t>
    	public ${castType(t.type)} get${t.columnName?cap_first}() {
    	
	    	return ${t.columnName?uncap_first};
    	}
    	
    	
    	public void set${t.columnName?cap_first}(${castType(t.type)} ${t.columnName?uncap_first}) {
    	
                this.${t.columnName?uncap_first} = ${t.columnName?uncap_first};
    	}

    	</#list>
}
