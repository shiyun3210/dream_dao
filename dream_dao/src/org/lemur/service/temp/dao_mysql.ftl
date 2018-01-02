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
package ${daoPath};

import java.sql.*;

public class ${className} extends DAO{
	private static final long serialVersionUID = 1L; 
	public  static final String TABLENAME = "${className}"; 
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
	
	
	public ${className}(){
	}
	<#if prilist??>
	<#list prilist as t>
		<#assign parms=castType(t.type)+" "+t.columnName+",">
	</#list>
	
	public ${className}(${parms?substring(0, parms?length-1)}) throws Exception{
	<#list prilist as t>
		this.${t.columnName} = ${t.columnName};
	</#list>
	}
	</#if>
	
	<#list table as t>
	/** 
     *${t.description} 
	 */
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
	
	public ${className} load() throws Exception{
		Connection con = this.getConnection();
		${className} obj=load(con);
		this.close(con);
		return obj;
	}

	public ${className} load(Connection con) throws Exception{  
		PreparedStatement pstmt = con.prepareStatement(LOAD_BY_KEY);
		<#if prilist??>
			<#list prilist as c>
		pstmt.set${castType(c.type)?cap_first}(${c_index+1}, this.${c.columnName});
			</#list>
		</#if>
		
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			return null;
		<#list table as c>
		${c.columnName} = rs.get${castType(c.type)?cap_first}("${c.columnName}");
		</#list>
		
		pstmt.close();
		return this;
	}
	
	
	
	public void insert() throws Exception{	
		Connection con = this.getConnection();
		insert(con);
		this.close(con); 
	}
	
	public void insert(Connection con) throws Exception{
		<#--
		<#if prilist??>
		<#list prilist as c >
		<#if c.autoId>
		if(this.${c.columnName}==null||"".equals(this.${c.columnName}))this.${c.columnName} =this.createId();
		</#if>
		</#list>
		</#if>
		--> 
		PreparedStatement pstmt = con.prepareStatement(INSERT_INTO_DB);
		<#list table as c>
		pstmt.set${castType(c.type)?cap_first}(${c_index+1}, this.${c.columnName});
		</#list>
						
		pstmt.execute(); 
		pstmt.close();
		pstmt = null; 
	}
	

	public int insertReturnID() throws Exception{	
		Connection con = this.getConnection();
		int auto_increment =insertReturnID(con);
		this.close(con);
		return auto_increment;
	}
	
	public int insertReturnID(Connection con) throws Exception{
		<#--
		<#if prilist??>
		<#list prilist as c >
		<#if c.autoId>
		if(this.${c.columnName}==null||"".equals(this.${c.columnName}))this.${c.columnName} =this.createId();
		</#if>
		</#list>
		</#if>
		-->
		Object auto_increment = null;
		PreparedStatement pstmt = con.prepareStatement(INSERT_INTO_DB,Statement.RETURN_GENERATED_KEYS);
		<#list table as c>
		pstmt.set${castType(c.type)?cap_first}(${c_index+1}, this.${c.columnName});
		</#list>
						
		pstmt.executeUpdate();
		
		ResultSet rs = pstmt.getGeneratedKeys();   
        if( rs.next() )
            auto_increment = rs.getObject(1);
       
        rs.close();
        rs = null;
		pstmt.close();
		pstmt = null;
		return Integer.parseInt(auto_increment.toString());
	}

	public void save() throws Exception{
		Connection con = this.getConnection();
		save(con);
		this.close(con);
	}

	public void save(Connection con) throws Exception{
		PreparedStatement pstmt = con.prepareStatement(UPDATE_TO_DB);
		<#if normallist??>
		<#list normallist as c>
		pstmt.set${castType(c.type)?cap_first}(${c_index+1}, this.${c.columnName});
		</#list>
		</#if>
		<#if prilist??>
			<#list prilist as c>
		pstmt.set${castType(c.type)?cap_first}(${normallist?size+c_index+1}, this.${c.columnName});
			</#list>
		</#if>
		int ret=pstmt.executeUpdate();
		pstmt.close();	
		if(ret==0)insert();	
	}
	
	public void delete() throws Exception{
		Connection con = this.getConnection();
		delete(con);
		this.close(con);
	}
	
	public void delete(Connection con) throws Exception{
		PreparedStatement pstmt = con.prepareStatement(DELETE_FROM_DB);
		<#if prilist??>
			<#list prilist as c>
		pstmt.set${castType(c.type)?cap_first}(${c_index+1}, this.${c.columnName});
			</#list>
		</#if>
		pstmt.execute();
		pstmt.close(); 
	}
}
