package ${packages};

import java.util.List;
import java.util.HashMap;

import ${basePackages}.bean.${beanClassName};
import ${basePackages}.util.Pagination;

public interface ${className} {

    
        /**
         * 插入
         * @param ${beanClassName}对象
         * @return sysid
         */
        public int insert(${beanClassName} ${beanVarName});
        
        
        /**
         * 删除:单个
         * @param sysid
         * @return false:失败；true:成功
         */
        public boolean delete(int sysid);
        
        
        /**
         * 删除:批量
         * @param idArray id数组
         * @return 删除条数
         */
        public int batchdelete(int[] idArray);
        
        
        /**
         * 更新
         * @param ${beanClassName}对象
         * @return false:失败；true:成功
         */
        public boolean update(${beanClassName} ${beanVarName});
        
        
        /**
         * 获取：单条
         * @param sysid
         * @return ${beanClassName}对象
         */
        public ${beanClassName} get(int sysid);
        
        
        /**
         * 获取：分页
         * @param pageno 页码
         * @param pagesize 每页条数
         * @return Pagination 分页对象
         */
        public Pagination getPage(int pageno,int pagesize);
        
        
        /**
         * 获取：列表
         * @return List<HashMap<String,Object>> 类型的结果集
         */
        public List<HashMap<String,Object>> getList();
        
        
        /**
         * 获取：列表
         * @param idArray id数组
         * @return List<HashMap<String,Object>> 类型的结果集
         */
        public List<HashMap<String,Object>> getListByIdArray(int[] idArray);
	
}
