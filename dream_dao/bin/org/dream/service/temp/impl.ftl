package ${packages};

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import ${basePackages}.dao.${daoClassName};
import ${basePackages}.bean.${beanClassName};
import ${basePackages}.iface.${ifacClassName};
import ${basePackages}.util.DbUtils;
import ${basePackages}.util.Pagination;

@Component
public class ${className} implements ${ifacClassName}{


        private final Logger  logger    = LoggerFactory.getLogger(${className}.class);

        /**
         * 插入
         * @param ${beanClassName}对象
         * @return sysid
         */
        public int insert(${beanClassName} ${beanVarName}){
            
                if(${beanVarName} == null){
                        throw new IllegalArgumentException("arg ${beanVarName} should not be null");
                }
            
                try {
                        return ${daoVarName}.insert(${beanVarName}).getId();
                } catch (Exception e) {
                        logger.error("insert", e);
                }
            
                return -1;
        }
        
        
        /**
         * 删除:单个
         * @param sysid
         * @return false:失败；true:成功
         */
        public boolean delete(int sysid){
            
                if(sysid < 1){
                        throw new IllegalArgumentException("sysid should great 0");
                }
            
                try {
                        ${daoVarName}.delete(sysid);
                        return true;
                } catch (Exception e) {
                        logger.error("delete", e);
                }
                
                return false;
        }
        
        
        /**
         * 删除:批量
         * @param idArray id数组
         * @return 删除条数
         */
        public int batchdelete(int[] idArray){
            
                if(idArray == null || idArray.length == 0){
                        return 0;
                }
                
                String ides = "";
                
                for(int id : idArray){
                        ides +=","+id;
                }
                ides = ides.substring(1,ides.length());
                
                String sql = "delete from ${tableName} where sysid in (" + ides + ")";
                
                try {
                        return DbUtils.execute(sql);
                } catch (Exception e) {
                        logger.error("batchdelete", e);
                }
                
                return -1;
        }
        
        
        /**
         * 更新
         * @param ${beanClassName}对象
         * @return false:失败；true:成功
         */
        public boolean update(${beanClassName} ${beanVarName}){
        
                if(${beanVarName} == null){
                        throw new IllegalArgumentException("arg ${beanVarName} should not be null");
                }
                
                try {
                        ${daoVarName}.save(${beanVarName});
                        return true;
                } catch (Exception e) {
                        logger.error("update", e);
                }
                
                return false;
        }
        
        
        /**
         * 获取：单条
         * @param sysid
         * @return ${beanClassName}对象
         */
        public ${beanClassName} get(int sysid){
        
                try {
                        return ${daoVarName}.load(sysid);
                } catch (Exception e) {
                        logger.error("get", e);
                }
                
                return null;
        }
        
        
        /**
         * 获取：分页
         * @param pageno 页码
         * @param pagesize 每页条数
         * @return Pagination 分页对象
         */
        public Pagination getPage(int pageno,int pagesize){
        
                String sql="select * from ${tableName} ";
                
                Pagination page = Pagination.executePaginationQuery(sql, pageno, pagesize);
                
                return page;
        }
        
        
        /**
         * 获取：列表
         * @return List<HashMap<String,Object>>
         */
        public List<HashMap<String,Object>> getList(){
            
                String sql = "select * from ${tableName} ";
            
                try {
                        return DbUtils.getResultSetList(sql);
                } catch (Exception e) {
                        logger.error("getList", e);
                }
            
                return new ArrayList<HashMap<String, Object>>();
        }
        
        
        /**
         * 获取：列表
         * @param idArray id数组
         * @return List<HashMap<String,Object>> 类型的结果集
         */
        public List<HashMap<String,Object>> getListByIdArray(int[] idArray){
        
                if(idArray == null || idArray.length == 0){
                        return new ArrayList<HashMap<String, Object>>();
                }
                
                String ides = "";
                
                for(int id : idArray){
                        ides +=","+id;
                }
                ides = ides.substring(1,ides.length());
                
                String sql = "select * from ${tableName} where sysid in (" + ides + ")";
                
                try {
                        return DbUtils.getResultSetList(sql);
                } catch (Exception e) {
                        logger.error("getListByIdArray", e);
                }
                
                return new ArrayList<HashMap<String, Object>>();
        }
        
        @Autowired
        private ${daoClassName} ${daoVarName};
}
