package com.explore.xianhuaback.Mapper.admin;

import com.explore.xianhuaback.Entity.AdminSales.SalesExcelRowVO;
import com.explore.xianhuaback.Entity.AdminSales.SalesSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SalesExcelMapper {

    @Select("""
            SELECT
                oi.goods_id AS goodsId,
                oi.combo_id AS comboId,
                oi.type AS itemType,
                oi.item_name AS itemName,
                oi.item_name_title AS itemNameTitle,
                oi.item_price AS unitPrice,
                COALESCE(SUM(CASE WHEN oi.type = 1 THEN oi.quantity ELSE 0 END), 0) AS salesQuantity,
                COALESCE(SUM(CASE WHEN oi.type = 2 THEN oi.quantity ELSE 0 END), 0) AS comboQuantity,
                COALESCE(SUM(oi.quantity), 0) AS totalMarketingQuantity,
                COALESCE(SUM(oi.quantity * oi.item_price), 0) AS turnoverAmount
            FROM order_items oi
            LEFT JOIN orders o ON o.order_no = oi.order_no
            WHERE o.pay_status IN (0, 1)
            GROUP BY oi.goods_id, oi.combo_id, oi.type, oi.item_name, oi.item_name_title, oi.item_price
            ORDER BY turnoverAmount DESC
            """)
    List<SalesExcelRowVO> listSalesRows();

    @Select("""
            SELECT
                COALESCE(SUM(CASE WHEN oi.type = 1 THEN oi.quantity ELSE 0 END), 0) AS totalSalesQuantity,
                COALESCE(SUM(CASE WHEN oi.type = 2 THEN oi.quantity ELSE 0 END), 0) AS totalComboQuantity,
                COALESCE(SUM(oi.quantity), 0) AS totalMarketingQuantity,
                COALESCE(SUM(oi.quantity * oi.item_price), 0) AS totalTurnoverAmount
            FROM order_items oi
            LEFT JOIN orders o ON o.order_no = oi.order_no
            WHERE o.pay_status IN (0, 1)
            """)
    SalesSummaryVO getSalesSummary();
}
