package com.icloud.common;

import cn.hutool.core.lang.Snowflake;

public class SnowflakeUtils {

    public static int  dataCenterIds = JvmUtils.getSysinfo();
    public static int  workerId = JvmUtils.jvmPid();

   /* workerId ：不同应用
     datacenterId ： 区分不同服务器
    */
    public static String getOrderNo(Long workerId, Long datacenterId){
        if(workerId==null){
            workerId =1L;
        }
        if(datacenterId==null){
            datacenterId =dataCenterIds%31l;
        }
        Snowflake snowFlake = new Snowflake(workerId,datacenterId);
        return String.valueOf(snowFlake.nextId());
    }

    public static String getSingleOrderNo(){
        Snowflake snowFlake = new Snowflake(workerId%31L,dataCenterIds%31l);
        return String.valueOf(snowFlake.nextId());
    }

    public static String getOrderNoByWordId(Long workerId){
        Snowflake snowFlake = new Snowflake(workerId,dataCenterIds%31l);
        return String.valueOf(snowFlake.nextId());
    }
}
