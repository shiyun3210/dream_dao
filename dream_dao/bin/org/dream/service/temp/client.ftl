package ${packages};

import java.util.List;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import ${basePackages}.bean.${beanClassName};
import ${basePackages}.iface.${ifacClassName};
import ${basePackages}.impl.${implClassName};
import ${basePackages}.util.Pagination;

@Component
public class ${className} {

    
        private ${ifacClassName} ${ifacVarName} = null;
        
        public ${className}(){
        
                ${ifacVarName} = new ${implClassName}();
        }
        
        /**
         * 插入
         * @param ${beanClassName}对象
         * @return sysid
         */
        public int insert(${beanClassName} ${beanVarName}){
            
                return ${ifacVarName}.insert(${beanVarName});
        }
        
        
        /**
         * 删除:单个
         * @param sysid
         * @return false:失败；true:成功
         */
        public boolean delete(int sysid){
        
                return ${ifacVarName}.delete(sysid);
        }
        
        
        /**
         * 删除:批量
         * @param idArray id数组
         * @return 删除条数
         */
        public int batchdelete(int[] idArray){
        
                return ${ifacVarName}.batchdelete(idArray);
        }
        
        
        /**
         * 更新
         * @param ${beanClassName}对象
         * @return false:失败；true:成功
         */
        public boolean update(${beanClassName} ${beanVarName}){
        
                return ${ifacVarName}.update(${beanVarName});
        }
        
        
        /**
         * 获取：单条
         * @param sysid
         * @return ${beanClassName}对象
         */
        public ${beanClassName} get(int sysid){
        
                return ${ifacVarName}.get(sysid);
        }
        
        
        /**
         * 获取：分页
         * @param pageno 页码
         * @param pagesize 每页条数
         * @return Pagination 分页对象
         */
        public Pagination getPage(int pageno,int pagesize){
        
                return ${ifacVarName}.getPage(pageno, pagesize);
        }
        
        
        /**
         * 获取：列表
         * @return List<HashMap<String,Object>> 类型的结果集
         */
        public List<HashMap<String,Object>> getList(){
        
                return ${ifacVarName}.getList();
        }
        
        
        /**
         * 获取：列表
         * @param idArray id数组
         * @return List<HashMap<String,Object>> 类型的结果集
         */
        public List<HashMap<String,Object>> getListByIdArray(int[] idArray){
        
                return ${ifacVarName}.getListByIdArray(idArray);
        }
    
}
