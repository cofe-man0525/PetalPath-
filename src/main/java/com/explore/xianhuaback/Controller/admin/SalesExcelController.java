package com.explore.xianhuaback.Controller.admin;

import com.explore.xianhuaback.Service.admin.SalesExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/sales")
public class SalesExcelController {

    private final SalesExcelService salesExcelService;

    public SalesExcelController(SalesExcelService salesExcelService) {
        this.salesExcelService = salesExcelService;
    }

    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) {
        salesExcelService.exportSalesExcel(response);
    }
}
