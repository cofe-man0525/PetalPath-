package com.explore.xianhuaback.Mapper.user;

import com.explore.xianhuaback.Entity.Order;
import com.explore.xianhuaback.Entity.OrderItem;
import com.explore.xianhuaback.Entity.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderPayUserMapper {

    OrderVO getByOrderId(Long orderNo);

    @Select("""
            SELECT
                id,
                order_no AS orderNo,
                user_id AS userId,
                address_id AS addressId,
                coupon_id AS couponId,
                remark,
                version,
                total_amount AS totalAmount,
                discount_amount AS discountAmount,
                pay_amount AS payAmount,
                pay_status AS payStatus,
                pay_time AS payTime,
                create_time AS createTime
            FROM orders
            WHERE order_no = #{orderNo}
            LIMIT 1
            """)
    OrderVO getByOrderNo(String orderNo);

    int insertOrder(Order order);

    void insertGoods(OrderItem orderItem);

    void insertComboId(OrderItem orderItem);
}
