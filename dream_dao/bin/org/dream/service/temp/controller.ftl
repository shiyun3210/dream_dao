package ${packages};

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ${basePackages}.bean.${beanClassName};
import ${basePackages}.client.${clientClassName};
import ${basePackages}.util.Pagination;

@Controller
public class ${className} {

    
        /**
         * 首页
         * @param session
         * @return ModelAndView
         */
        @RequestMapping("${baseURL}/index")
        public ModelAndView index(HttpSession session, int pageno) {
    
                HashMap<String,Object> data  = new HashMap<String,Object>();
                Pagination page = ${clientVarName}.getPage(pageno, 10);
                data.put("page", page);
                
                return new ModelAndView("/views/${baseViewPath}/index", data);
        }
        
        
        /**
         * 添加
         * @param session
         * @return ModelAndView
         */
        @RequestMapping(value="${baseURL}/add", method=RequestMethod.GET)
        public ModelAndView add(HttpSession session){
            
                Map<String, Object> data = new HashMap<String, Object>();
                
                return new ModelAndView("/views/${baseViewPath}/add", data);
        }
        
        
        /**
         * 编辑
         * @param session
         * @param id
         * @return ModelAndView
         */
        @RequestMapping(value="${baseURL}/edit", method=RequestMethod.GET)
        public ModelAndView edit(HttpSession session,int id){
            
                Map<String, Object> data = new HashMap<String, Object>();
                ${beanClassName} ${beanVarName}= ${clientVarName}.get(id);
                data.put("${beanVarName}",${beanVarName});
                
                return new ModelAndView("/views/${baseViewPath}/edit", data);
        }
        
        
        /**
         * 保存
         * 
         * @param request
         * @return
         */
        @RequestMapping(value="${baseURL}/save", method=RequestMethod.POST)
        public @ResponseBody Object save(${beanClassName} ${beanVarName}){
            
                Map<String,Object> map  = new HashMap<String,Object>();
                map.put("status", 0);
                map.put("statusText", "删除成功");
                
                //TODO 权限校验
                
                if(${beanVarName}.getId() > 0){//编辑
                    ${beanClassName} old${beanVarName} = ${clientVarName}.get(${beanVarName}.getId());
                    //TODO 字段拷贝
                    ${clientVarName}.update(${beanVarName});
                }else{//新建
                    ${clientVarName}.insert(${beanVarName});
                }
                
                return map;
        }
        
        
        /**
         * 删除
         * @param session
         * @param id
         * @return Object JSON
         */
        @RequestMapping("${baseURL}/del")
        public @ResponseBody Object del(HttpSession session, int id) {
            
                ${beanClassName} ${beanVarName} = ${clientVarName}.get(id);
                
                //TODO 权限校验
                
                ${clientVarName}.delete(id);
                
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("status", 0);
                map.put("statusText", "删除成功");
                
                return map;
        }
        
        
        /**
         * 删除
         * @param session
         * @param id Array
         * @return Object JSON
         */
        @RequestMapping("${baseURL}/batchdel")
        public @ResponseBody Object batchdel(HttpSession session, int[] id) {
            
                List<HashMap<String, Object>> ${beanVarName}List = ${clientVarName}.getListByIdArray(id);
                
                //TODO 权限校验
                
                ${clientVarName}.batchdelete(id);
                
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("status", 0);
                map.put("statusText", "删除成功");
                
                return map;
        }
        
        
        @Autowired
        public ${clientClassName} ${clientVarName};
    
}
