package com.icloud.api.small.shopkeeper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.LoginUser;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.shop.entity.ShopBank;
import com.icloud.modules.shop.service.ShopBankService;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api("银行卡")
@RestController
@RequestMapping("/api/shopkeeper/shopBank")
public class ShopApiBankController {

    @Autowired
    private ShopBankService shopBankService;

    /**
     * 银行卡列表
     * @return
     */
    @ApiOperation(value="银行卡列表", notes="")
    @RequestMapping(value = "/bankList",method = {RequestMethod.GET})
    public R bankList(@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        List<ShopBank> list = shopBankService.list(new QueryWrapper<ShopBank>().eq("shop_id",user.getShopMan().getShopId()));
        return R.ok().put("list",list);
    }

    /**
     * 保存银行卡
     * @return
     */
    @ApiOperation(value="保存银行卡", notes="")
    @RequestMapping(value = "/saveBank",method = {RequestMethod.GET})
    public R saveBank(@LoginUser WxUser user,@RequestBody ShopBank shopBank) {
        ValidatorUtils.validateEntityForFront(shopBank);
        shopBank.setCreatedTime(new Date());
        shopBank.setShopId(user.getShopMan().getShopId());
        shopBank.setCreatedBy(user.getShopMan().getAccountNo());
        boolean result =shopBankService.saveOrUpdate(shopBank);
        return result==true?R.ok().put("shopBank",shopBank):R.error("添加失败");
    }
    /**
     * 更新银行卡
     * @return
     */
    @ApiOperation(value="更新银行卡", notes="")
    @RequestMapping(value = "/updatBank",method = {RequestMethod.GET})
    public R updatBank(@LoginUser WxUser user,@RequestBody ShopBank shopBank) {
        ValidatorUtils.validateEntityForFront(shopBank);
        if(user.getShopMan().getShopId().longValue()!=shopBank.getShopId().longValue()){
            return R.error("店主所属店铺与操作店铺不一致");
        }
        shopBank.setCreatedTime(new Date());
        shopBank.setCreatedBy(user.getShopMan().getAccountNo());
        boolean result =shopBankService.updateById(shopBank);
        return result==true?R.ok().put("shopBank",shopBank):R.error("更新失败");
    }

    /**
     * 银行卡信息
     * @return
     */
    @ApiOperation(value="银行卡信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "银行卡id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/bankInfo",method = {RequestMethod.GET})
    public R bankInfo(@RequestParam Long id,@LoginUser WxUser user) {
        ShopBank  shopBank = (ShopBank) shopBankService.getById(id);
        return R.ok().put("shopBank",shopBank);
    }

    /**
     * 删除银行卡
     * @return
     */
    @ApiOperation(value="删除银行卡", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "银行卡id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delBank",method = {RequestMethod.GET})
    public R delBank(@RequestParam Long id,@LoginUser WxUser user) {
       boolean result = shopBankService.removeById(id);
        return result==true?R.ok():R.error("删除失败");
    }



}
