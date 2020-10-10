package com.icloud.common;

import com.icloud.api.vo.MonthVo;
import com.icloud.api.vo.YearVo;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class GetYearUtil {

    public static List<YearVo> getYearList() {
        List<YearVo> list = new ArrayList<YearVo>();

        for (int i=10;i>=1;i--){
            YearVo vo = new YearVo();
            vo.setYear(DateUtil.getBeforeNYear(-1*i));
            vo.setCheck(false);
            list.add(vo);
        }
        YearVo vo = new YearVo();
        int nowyear = DateUtil.getYear();
        vo.setYear(nowyear);
        vo.setCheck(true);
        list.add(vo);
        for (int i=1;i<=10;i++){
            vo = new YearVo();
            vo.setYear(DateUtil.getBeforeNYear(i));
            vo.setCheck(false);
            list.add(vo);
        }
        return list;
    }

    public static List<MonthVo> getMonthList() {
        List<MonthVo> list = new ArrayList<MonthVo>();
        int nowmoth = DateUtil.getMonth();
        for (int i=1;i<=12;i++){
           MonthVo vo = new MonthVo();
            vo.setMonth(i);
            if(nowmoth==i){
                vo.setCheck(true);
            }else {
                vo.setCheck(false);
            }
            list.add(vo);
        }
        return list;
    }
}