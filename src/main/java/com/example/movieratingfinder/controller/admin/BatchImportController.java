package com.example.movieratingfinder.controller.admin;

import com.example.movieratingfinder.service.BatchImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/admin/batch-import")
public class BatchImportController {

    @Autowired
    private BatchImportService batchImportService;

    @GetMapping
    public String batchImportForm(Model model) {
        model.addAttribute("importStatus", batchImportService.getStatus());
        return "admin/batch-import";
    }

    @PostMapping("/start")
    public String startBatchImport(
            @RequestParam String query,
            @RequestParam int pages,
            @RequestParam(required = false) Integer year,
            Model model) {
        CompletableFuture<String> future = batchImportService.startBatchImport(query, pages, year);
        return "redirect:/admin/batch-import";
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<BatchImportService.BatchImportStatus> getStatus() {
        return ResponseEntity.ok(batchImportService.getStatus());
    }
}
