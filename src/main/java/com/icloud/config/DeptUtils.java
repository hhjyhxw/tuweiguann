package com.icloud.config;

import com.icloud.annotation.DataFilter;
import com.icloud.modules.sys.entity.SysUserEntity;
import com.icloud.modules.sys.service.SysDeptService;
import com.icloud.modules.sys.service.SysRoleDeptService;
import com.icloud.modules.sys.service.SysUserRoleService;
import com.icloud.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DeptUtils {

    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleDeptService sysRoleDeptService;

    /**
     * 获取部门（企业）数据过滤的SQL
     */
    public String getSQLFilter(){
        SysUserEntity user = ShiroUtils.getUserEntity();
        //部门ID列表
        Set<Long> deptIdList = new HashSet<>();

        //用户拥有角色
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getUserId());
        if(roleIdList.size() > 0){
            //用户角色对应的部门ID列表
            List<Long> userDeptIdList = sysRoleDeptService.queryDeptIdList(roleIdList.toArray(new Long[roleIdList.size()]));
            deptIdList.addAll(userDeptIdList);
        }

        //用户子部门ID列表
        List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(user.getDeptId());
        deptIdList.addAll(subDeptIdList);

        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if(deptIdList.size() > 0){
            sqlFilter.append("t").append("dept_id").append(" in(").append(StringUtils.join(deptIdList, ",")).append(")");
        }
        sqlFilter.append(")");

        if(sqlFilter.toString().trim().equals("()")){
            return null;
        }

        return sqlFilter.toString();
    }

    /**
     * 获取部门（企业）id集合
     */
    public List<Long> getDeptIdList(){
        SysUserEntity user = ShiroUtils.getUserEntity();

        //部门ID列表,方便去重
        Set<Long> deptIdList = new HashSet<>();

        //用户拥有角色
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getUserId());
        if(roleIdList.size() > 0){
            //用户角色对应的部门ID列表
            List<Long> userDeptIdList = sysRoleDeptService.queryDeptIdList(roleIdList.toArray(new Long[roleIdList.size()]));
            deptIdList.addAll(userDeptIdList);
        }

        //用户子部门ID列表
        List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(user.getDeptId());
        deptIdList.addAll(subDeptIdList);

        List<Long> result = new ArrayList<>(deptIdList);
        return result;
    }


    /**
     * 获取部门（企业）id List
     */
    public List<Long> getDeptIdLists(Long deptId){
        List<Long> deptIdList = new ArrayList<>();
        deptIdList.add(deptId);

        //用户子部门ID列表
        List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(deptId);
        deptIdList.addAll(subDeptIdList);

        List<Long> result = new ArrayList<>(deptIdList);
        return result;
    }
    /**
     * 封装部门id 成 sql_filter
     */
    public String getDeptIdList(Long deptId){

        List<Long> deptIdList = new ArrayList<>();
        deptIdList.add(deptId);
        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if(deptIdList.size() > 0){
            sqlFilter.append("t.").append("dept_id").append(" in(").append(StringUtils.join(deptIdList, ",")).append(")");
        }
        sqlFilter.append(")");

        if(sqlFilter.toString().trim().equals("()")){
            return null;
        }
        return sqlFilter.toString();
    }

    /**
     * 封装部门id 和子id  成 sql_filter
     */
    public String getDeptIdListAndSon(Long deptId){

        List<Long> deptIdList = new ArrayList<>();
        deptIdList.add(deptId);
        //用户子部门ID列表
        List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(deptId);
        deptIdList.addAll(subDeptIdList);


        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if(deptIdList.size() > 0){
            sqlFilter.append("t").append("dept_id").append(" in(").append(StringUtils.join(deptIdList, ",")).append(")");
        }
        sqlFilter.append(")");

        if(sqlFilter.toString().trim().equals("()")){
            return null;
        }
        return sqlFilter.toString();
    }
}
