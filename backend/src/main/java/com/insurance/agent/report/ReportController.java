package com.insurance.agent.report;
import com.insurance.agent.common.dto.ApiResponse;
import com.insurance.agent.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api/v1/reports") @RequiredArgsConstructor
public class ReportController {
    private final PolicyRepository policies;
    @GetMapping("/expiry-list") ResponseEntity<?> expiry(@RequestParam LocalDate from,@RequestParam LocalDate to){
        var data=policies.expiringBetween(from,to).stream().map(p->Map.of("customerName",p.getCustomer().getName(),"phone",p.getCustomer().getPhone(),"policyNumber",p.getPolicyNumber(),"company",p.getCompany(),"planName",p.getPlanName(),"expiryDate",p.getEndDate())).toList();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
    @GetMapping("/commission") ResponseEntity<?> commission(@RequestParam LocalDate from,@RequestParam LocalDate to){
        var grouped=policies.expiringBetween(from,to).stream().collect(java.util.stream.Collectors.groupingBy(p->p.getCompany().name(),java.util.stream.Collectors.reducing(java.math.BigDecimal.ZERO,p->Optional.ofNullable(p.getCommissionAmount()).orElse(java.math.BigDecimal.ZERO),java.math.BigDecimal::add)));
        return ResponseEntity.ok(ApiResponse.ok(grouped));
    }
    @GetMapping("/monthly") ResponseEntity<?> monthly(@RequestParam int month,@RequestParam int year){
        var data=policies.findAll().stream().filter(p -> p.getCreatedAt()!=null && p.getCreatedAt().getMonthValue()==month && p.getCreatedAt().getYear()==year)
            .collect(java.util.stream.Collectors.groupingBy(p -> p.getCompany().name(),java.util.stream.Collectors.counting()));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("month",month,"year",year,"policiesByCompany",data)));
    }
    @GetMapping("/export/excel") ResponseEntity<byte[]> excel(@RequestParam LocalDate from,@RequestParam LocalDate to) throws Exception {
        try(var book=new XSSFWorkbook();var out=new ByteArrayOutputStream()){var sheet=book.createSheet("Expiry report");var head=sheet.createRow(0);String[] cols={"Customer","Phone","Policy No","Company","Plan","Expiry"};
            for(int i=0;i<cols.length;i++)head.createCell(i).setCellValue(cols[i]);int row=1;for(var p:policies.expiringBetween(from,to)){var r=sheet.createRow(row++);r.createCell(0).setCellValue(p.getCustomer().getName());r.createCell(1).setCellValue(p.getCustomer().getPhone());r.createCell(2).setCellValue(p.getPolicyNumber());r.createCell(3).setCellValue(p.getCompany().name());r.createCell(4).setCellValue(p.getPlanName());r.createCell(5).setCellValue(p.getEndDate().toString());}book.write(out);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=insurance-report.xlsx").contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(out.toByteArray());}
    }
    @GetMapping("/export/pdf") ResponseEntity<byte[]> pdf(@RequestParam LocalDate from,@RequestParam LocalDate to) throws Exception {
        try(var out=new ByteArrayOutputStream()){
            var pdf=new PdfDocument(new PdfWriter(out)); var document=new Document(pdf);
            document.add(new Paragraph("InsureDesk · Expiry report").setBold().setFontSize(18));
            document.add(new Paragraph(from+" to "+to));
            var table=new Table(4); table.addHeaderCell("Customer");table.addHeaderCell("Policy");table.addHeaderCell("Company");table.addHeaderCell("Expiry");
            for(var p:policies.expiringBetween(from,to)){table.addCell(p.getCustomer().getName());table.addCell(p.getPolicyNumber());table.addCell(p.getCompany().name());table.addCell(p.getEndDate().toString());}
            document.add(table);document.close();
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=insurance-report.pdf").contentType(MediaType.APPLICATION_PDF).body(out.toByteArray());
        }
    }
}
