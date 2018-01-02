<#function castType type>
	<#local type = type?lower_case>
	<#if type?index_of("int") gte 0 || type?index_of("bit") gte 0>
		<#return "int">
	<#elseif type?index_of("float") gte 0>
		<#return "float">
	<#elseif type?index_of("double") gte 0>
		<#return "double">
	<#else>
		<#return "String">
	</#if>
</#function>
package ${packages};

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.springframework.stereotype.Component;
import ${basePackages}.bean.${classBeanName};

@Component
public class ${className}DAO{

        public  static final String TABLENAME = "${tableName}"; 
        <#compress>
        <#assign prikey = "">
        <#assign updatekey = "">
        <#assign insertkey1 = "">
        <#assign insertkey2 = "">
        <#list table as t>
        	<#if t.primary>
        		<#assign prikey = prikey + t.columnName+" = ?" + "and">
        	<#else>
        		<#assign updatekey = updatekey + t.columnName+"=?,">
        	</#if>
        
        	<#assign insertkey1 = insertkey1 + t.columnName+",">
        	<#assign insertkey2 = insertkey2 + "?,">
        </#list>
        
        <#if prilist??>
        	<#assign prikeys = "where "+prikey?substring(0, prikey?length-3)>
        </#if>
        <#assign insertkey1 = insertkey1?substring(0, insertkey1?length-1)>
        <#assign insertkey2 = insertkey2?substring(0, insertkey2?length-1)>
        <#assign updatekey = updatekey?substring(0, updatekey?length-1)>
        </#compress>
        private static final String LOAD_BY_KEY ="select * from ${tableName} ${prikeys} ";
        private static final String INSERT_INTO_DB ="insert into ${tableName}(${insertkey1}) values (${insertkey2})";
        private static final String UPDATE_TO_DB ="update ${tableName} set ${updatekey} ${prikeys} ";
        private static final String DELETE_FROM_DB ="delete from ${tableName} ${prikeys} "; 
        
        public ${classBeanName} load(int id) throws Exception{
            	Connection conn = ConnectionManager.getConnection(ConnectionManager.READ_DB);
            	${classBeanName} obj = null;
		try{
			obj = load(id, conn);
		} finally{
			ConnectionManager.returnConnection(conn);
		}
            	return obj;
        }
        
        public ${classBeanName} load(int id, Connection conn) throws Exception{  
            	PreparedStatement pstmt = conn.prepareStatement(LOAD_BY_KEY);
            	pstmt.setInt(1, id);
            	
            	ResultSet rs = pstmt.executeQuery();
            	if (!rs.next())
            		return null;
            		
            	${classBeanName} bean = new ${classBeanName}();
            	<#list table as c>
    		bean.set${c.columnName?cap_first}(rs.get${castType(c.type)?cap_first}("${c.columnName}"));
            	</#list>
            	
            	pstmt.close();
            	return bean;
        }
        
        
        
        public ${classBeanName} insert(${classBeanName} bean) throws Exception{	
            	Connection conn = ConnectionManager.getConnection(ConnectionManager.WRITE_DB);
            	try{
			bean = insert(bean, conn);
		} finally{
			ConnectionManager.returnConnection(conn);
		}
            	return bean;
        }
        
        public ${classBeanName} insert(${classBeanName} bean, Connection conn) throws Exception{
            	if (bean == null){
			throw new NullPointerException("bean(${classBeanName}) is null");
		}
		
            	PreparedStatement pstmt = conn.prepareStatement(INSERT_INTO_DB,Statement.RETURN_GENERATED_KEYS);
            	<#list table as c>
    		pstmt.set${castType(c.type)?cap_first}(${c_index+1}, bean.get${c.columnName?cap_first}());
            	</#list>
            					
            	pstmt.executeUpdate();

		ResultSet rs = pstmt.getGeneratedKeys();

		if (rs.next())
			bean.setId(rs.getInt(1));

		rs.close();
		rs = null;
		pstmt.close();
		pstmt = null;
		return bean;
        }
        
        
        public void save(${classBeanName} bean) throws Exception{
            	Connection conn = ConnectionManager.getConnection(ConnectionManager.WRITE_DB);
            	try{
			save(bean, conn);
		} finally{
			ConnectionManager.returnConnection(conn);
		}
        }
        
        public void save(${classBeanName} bean, Connection conn) throws Exception{
            	PreparedStatement pstmt = conn.prepareStatement(UPDATE_TO_DB);
            	<#if normallist??>
            	<#list normallist as c>
            	pstmt.set${castType(c.type)?cap_first}(${c_index+1}, bean.get${c.columnName?cap_first}());
            	</#list>
            	</#if>
            	<#if prilist??>
            		<#list prilist as c>
		pstmt.set${castType(c.type)?cap_first}(${normallist?size+c_index+1}, bean.get${c.columnName?cap_first}());
            		</#list>
            	</#if>
            	int ret=pstmt.executeUpdate();
            	pstmt.close();	
            	if(ret==0) insert(bean);	
        }
        
        public void delete(int id) throws Exception{
            	Connection conn = ConnectionManager.getConnection(ConnectionManager.WRITE_DB);
            	try{
			delete(id, conn);
		} finally{
			ConnectionManager.returnConnection(conn);
		}
        }
        
        public void delete(int id, Connection conn) throws Exception{
            	PreparedStatement pstmt = conn.prepareStatement(DELETE_FROM_DB);
            	pstmt.setInt(1, id);
            	pstmt.execute();
            	pstmt.close(); 
        }
}
