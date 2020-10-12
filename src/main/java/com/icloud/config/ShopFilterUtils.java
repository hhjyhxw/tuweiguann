package com.icloud.config;

import com.alibaba.fastjson.JSON;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.sys.entity.SysUserEntity;
import com.icloud.modules.sys.service.SysDeptService;
import com.icloud.modules.sys.service.SysRoleDeptService;
import com.icloud.modules.sys.service.SysRoleShopService;
import com.icloud.modules.sys.service.SysUserRoleService;
import com.icloud.modules.sys.shiro.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ShopFilterUtils {

    @Autowired
    private ShopService shopService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleShopService sysRoleShopService;

    /**
     * 获取部门（企业）数据过滤的SQL(店铺本身数据过滤需要)
     */
    public String getSQLFilterForshopsell(){
        SysUserEntity user = ShiroUtils.getUserEntity();
        //部门ID列表
        Set<Long> shopIdList = new HashSet<>();

        //用户拥有角色
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getUserId());
        if(roleIdList.size() > 0){
            //用户角色对应的店铺ID列表
            List<Long> userShopIdList = sysRoleShopService.queryShopIdList(roleIdList.toArray(new Long[roleIdList.size()]));
            shopIdList.addAll(userShopIdList);
        }

        //用户子店铺ID列表
        List<Long> subShopIdList = shopService.getSubShopIdList(user.getShopId());
        shopIdList.addAll(subShopIdList);

        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if(shopIdList.size() > 0){
            sqlFilter.append("t.").append("id").append(" in(").append(StringUtils.join(shopIdList, ",")).append(")");
        }
        sqlFilter.append(")");

        if(sqlFilter.toString().trim().equals("()")){
            return null;
        }
        log.info("getSQLFilterForshopsell==="+ JSON.toJSONString(sqlFilter.toString()));
        return sqlFilter.toString();
    }

    /**
     * 获取部门（企业）数据过滤的SQL(分店铺数据过滤需要)
     */
    public String getSQLFilter(){
        SysUserEntity user = ShiroUtils.getUserEntity();
        //部门ID列表
        Set<Long> shopIdList = new HashSet<>();

        //用户拥有角色
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getUserId());
        if(roleIdList.size() > 0){
            //用户角色对应的店铺ID列表
            List<Long> userShopIdList = sysRoleShopService.queryShopIdList(roleIdList.toArray(new Long[roleIdList.size()]));
            shopIdList.addAll(userShopIdList);
        }

        //用户子店铺ID列表
        List<Long> subShopIdList = shopService.getSubShopIdList(user.getShopId());
        shopIdList.addAll(subShopIdList);

        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if(shopIdList.size() > 0){
            sqlFilter.append("t.").append("shop_id").append(" in(").append(StringUtils.join(shopIdList, ",")).append(")");
        }
        sqlFilter.append(")");

        if(sqlFilter.toString().trim().equals("()")){
            return null;
        }
        log.info("getSQLFilter==="+ JSON.toJSONString(sqlFilter.toString()));
        return sqlFilter.toString();
    }

    /**
     * 获取用户包含角色所在店铺id集合(包含子店铺)
     */
    public List<Long> getShopIdAndSubList(){
        SysUserEntity user = ShiroUtils.getUserEntity();

        //部门ID列表,方便去重
        Set<Long> shopIdList = new HashSet<>();
        shopIdList.add(user.getShopId());
        //用户拥有角色
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getUserId());
        if(roleIdList.size() > 0){
            //用户角色对应的部门ID列表
            List<Long> userShopIdList = sysRoleShopService.queryShopIdList(roleIdList.toArray(new Long[roleIdList.size()]));
            if(userShopIdList!=null && userShopIdList.size()>0){
                userShopIdList.forEach(p->{
                    shopIdList.add(p);
                });
            }
        }

        //用户子店铺ID列表
        List<Long> subShopIdList = shopService.getSubShopIdList(user.getShopId());
        if(subShopIdList!=null && subShopIdList.size()>0){
            subShopIdList.forEach(p->{
                shopIdList.add(p);
            });
        }

        List<Long> result = new ArrayList<>(shopIdList);
        log.info("getShopIdAndSubList==="+ JSON.toJSONString(result));
        return result;
    }


    /**
     * 根据店铺id获取自身和子店铺id集合 List
     */
    public List<Long> getShopIdListByShopId(Long shopId){
        List<Long> shopIdList = new ArrayList<>();
        shopIdList.add(shopId);

        //子店铺ID列表
        List<Long> subShopIdList = shopService.getSubShopIdList(shopId);
        if(subShopIdList!=null && subShopIdList.size()>0){
            subShopIdList.forEach(p->{
                shopIdList.add(p);
            });
        }
        log.info("getShopIdListByShopId==="+ JSON.toJSONString(shopIdList));
        return shopIdList;
    }
    /**
     * 封装店铺id 成 sql_filter
     */
    public String getShopIdsqlFilter(Long shopId){

        List<Long> shopIdList = new ArrayList<>();
        shopIdList.add(shopId);

        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if(shopIdList.size() > 0){
            sqlFilter.append("t.").append("shop_id").append(" in(").append(StringUtils.join(shopIdList, ",")).append(")");
        }
        sqlFilter.append(")");

        if(sqlFilter.toString().trim().equals("()")){
            return null;
        }
        log.info("getShopIdsqlFilter==="+ JSON.toJSONString(sqlFilter.toString()));
        return sqlFilter.toString();
    }

    /**
     * 封装店铺id 和子店铺id  成 sql_filter
     */
    public String getShopIdsqlFilterAndSon(Long shopId){

        List<Long> shopIdList = new ArrayList<>();
        shopIdList.add(shopId);
        //子店铺ID列表
        List<Long> subShopIdList = shopService.getSubShopIdList(shopId);
        if(subShopIdList!=null && subShopIdList.size()>0){
            subShopIdList.forEach(p->{
                shopIdList.add(p);
            });
        }


        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if(shopIdList.size() > 0){
            sqlFilter.append("t.").append("shop_id").append(" in(").append(StringUtils.join(shopIdList, ",")).append(")");
        }
        sqlFilter.append(")");

        if(sqlFilter.toString().trim().equals("()")){
            return null;
        }
        log.info("getShopIdsqlFilterAndSon==="+ JSON.toJSONString(sqlFilter.toString()));
        return sqlFilter.toString();
    }
}
