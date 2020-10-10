package com.icloud.common;

import java.math.BigDecimal;

public class PayUtil {
    public static String getFinalmoney(String money){
        float sessionmoney = Float.parseFloat(money);
        String finalmoney = String.format("%.2f", sessionmoney);
        finalmoney = finalmoney.replace(".", "");
        return String.valueOf(Integer.parseInt(finalmoney));
    }

    /**
     * 元转成分
     * @param money
     * @return
     */
    public static Integer getFinalmoneyInt(String money){
        float sessionmoney = Float.parseFloat(money);
        String finalmoney = String.format("%.2f", sessionmoney);
        finalmoney = finalmoney.replace(".", "");
        return Integer.parseInt(finalmoney);
    }

    /**
     * 分转成元,并保留两位小数
     * @param money
     * @return
     */
    public static BigDecimal fenTransYuan(Integer money){
        return new BigDecimal( money*0.01).setScale(2,BigDecimal.ROUND_HALF_UP);
    }
}
