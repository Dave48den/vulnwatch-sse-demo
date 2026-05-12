package com.example.vulnwatchssedemo.controller;

import com.example.vulnwatchssedemo.model.Scan;
import com.example.vulnwatchssedemo.service.ScanService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/scans")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/start")
    public Scan startScan() {
        return scanService.startScan();
    }

    @GetMapping("/{scanId}/status-stream")
    public SseEmitter streamScanStatus(@PathVariable String scanId) {
        return scanService.subscribe(scanId);
    }
}