package com.explore.xianhuaback.Utils;


import org.springframework.stereotype.Component;

@Component
public class StockLuaScript {

    //扣减对应的单品数量的库存
    public static final String DEDUCE_STOCK_LUA=
            "local sku_key = KEYS[1] " +                           // 商品规格库存 key
                    "local quantity = tonumber(ARGV[1]) " +                // 购买数量
                    "local current_stock = redis.call('get', sku_key) " +
                    "if current_stock and tonumber(current_stock) >= quantity then " +
                    "    redis.call('decrby', sku_key, quantity) " +
                    "    return 1 " +
                    "else " +
                    "    return 0 " +
                    "end";;

    //扣减对应的套餐的库存的数量
    public static final String DEDUCE_PACKAGE_STOCK_LUA =
            "local package_key = KEYS[1] " +                    // 套餐库存 key
                    "local quantity = tonumber(ARGV[1]) " +             // 购买数量
                    "local current_stock = redis.call('get', package_key) " +
                    "if current_stock and tonumber(current_stock) >= quantity then " +
                    "    redis.call('decrby', package_key, quantity) " +
                    "    return 1 " +
                    "else " +
                    "    return 0 " +
                    "end";
}
