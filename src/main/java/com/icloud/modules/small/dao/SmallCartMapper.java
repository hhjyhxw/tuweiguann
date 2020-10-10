package com.icloud.modules.small.dao;

import com.icloud.modules.small.entity.SmallCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icloud.modules.small.vo.CartVo;

import java.util.List;
import java.util.Map;

/**
 * 购物车
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
public interface SmallCartMapper extends BaseMapper<SmallCart> {

	List<SmallCart> queryMixList(Map<String,Object> map);

    List<CartVo> getCartVoList(Map map);
}
