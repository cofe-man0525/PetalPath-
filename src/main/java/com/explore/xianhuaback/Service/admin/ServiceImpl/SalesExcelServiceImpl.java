package com.explore.xianhuaback.Service.admin.ServiceImpl;

import com.explore.xianhuaback.Entity.AdminSales.SalesExcelRowVO;
import com.explore.xianhuaback.Entity.AdminSales.SalesSummaryVO;
import com.explore.xianhuaback.Mapper.admin.SalesExcelMapper;
import com.explore.xianhuaback.Service.admin.SalesExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class SalesExcelServiceImpl implements SalesExcelService {

    private final SalesExcelMapper salesExcelMapper;

    public SalesExcelServiceImpl(SalesExcelMapper salesExcelMapper) {
        this.salesExcelMapper = salesExcelMapper;
    }

    @Override
    public void exportSalesExcel(HttpServletResponse response) {
        List<SalesExcelRowVO> rows = salesExcelMapper.listSalesRows();
        SalesSummaryVO summary = salesExcelMapper.getSalesSummary();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("销售数据");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

            writeHeader(sheet, headerStyle);
            int rowIndex = 1;
            for (SalesExcelRowVO item : rows) {
                Row row = sheet.createRow(rowIndex++);
                writeDataRow(row, item, moneyStyle);
            }
            writeSummaryRow(sheet.createRow(rowIndex), summary, moneyStyle);

            for (int index = 0; index < 10; index++) {
                sheet.autoSizeColumn(index);
                sheet.setColumnWidth(index, Math.min(sheet.getColumnWidth(index) + 1200, 16000));
            }

            String fileName = "销售数据报表-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error("export sales excel failed", e);
            throw new RuntimeException("导出销售数据失败");
        }
    }

    private void writeHeader(Sheet sheet, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        String[] titles = {
                "商品ID",
                "套餐ID",
                "类型",
                "商品/套餐名称",
                "副标题/分类",
                "单价",
                "商品销量",
                "套餐数量",
                "总销售数量",
                "营业额度"
        };
        for (int index = 0; index < titles.length; index++) {
            Cell cell = header.createCell(index);
            cell.setCellValue(titles[index]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeDataRow(Row row, SalesExcelRowVO item, CellStyle moneyStyle) {
        row.createCell(0).setCellValue(defaultLong(item.getGoodsId()));
        row.createCell(1).setCellValue(defaultInteger(item.getComboId()));
        row.createCell(2).setCellValue(typeName(item.getItemType()));
        row.createCell(3).setCellValue(defaultString(item.getItemName()));
        row.createCell(4).setCellValue(defaultString(item.getItemNameTitle()));

        Cell priceCell = row.createCell(5);
        priceCell.setCellValue(defaultBigDecimal(item.getUnitPrice()).doubleValue());
        priceCell.setCellStyle(moneyStyle);

        row.createCell(6).setCellValue(defaultInteger(item.getSalesQuantity()));
        row.createCell(7).setCellValue(defaultInteger(item.getComboQuantity()));
        row.createCell(8).setCellValue(defaultInteger(item.getTotalMarketingQuantity()));

        Cell turnoverCell = row.createCell(9);
        turnoverCell.setCellValue(defaultBigDecimal(item.getTurnoverAmount()).doubleValue());
        turnoverCell.setCellStyle(moneyStyle);
    }

    private void writeSummaryRow(Row row, SalesSummaryVO summary, CellStyle moneyStyle) {
        row.createCell(0).setCellValue("汇总");
        row.createCell(6).setCellValue(defaultInteger(summary == null ? null : summary.getTotalSalesQuantity()));
        row.createCell(7).setCellValue(defaultInteger(summary == null ? null : summary.getTotalComboQuantity()));
        row.createCell(8).setCellValue(defaultInteger(summary == null ? null : summary.getTotalMarketingQuantity()));
        Cell turnoverCell = row.createCell(9);
        turnoverCell.setCellValue(defaultBigDecimal(summary == null ? null : summary.getTotalTurnoverAmount()).doubleValue());
        turnoverCell.setCellStyle(moneyStyle);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private String typeName(Integer type) {
        if (type == null) {
            return "未知";
        }
        return type == 2 ? "套餐" : "商品";
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private int defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
