package com.example.vulnwatchssedemo.service;

import com.example.vulnwatchssedemo.model.Scan;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScanService {

    private final Map<String, Scan> scans = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public Scan startScan() {

        String scanId = UUID.randomUUID().toString();

        Scan scan = new Scan(scanId, "PENDING");
        scans.put(scanId, scan);

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                simulateScan(scan);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return scan;
    }

    public SseEmitter subscribe(String scanId) {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(scanId, emitter);

        emitter.onCompletion(() -> emitters.remove(scanId));
        emitter.onTimeout(() -> emitters.remove(scanId));

        return emitter;
    }

    private void simulateScan(Scan scan) {

        new Thread(() -> {

            try {

                updateStatus(scan, "PENDING");
                Thread.sleep(6000);

                updateStatus(scan, "RUNNING");
                Thread.sleep(6000);

                updateStatus(scan, "PROCESSING");
                Thread.sleep(6000);

                updateStatus(scan, "COMPLETED");

            } catch (Exception e) {
                updateStatus(scan, "FAILED");
            }

        }).start();
    }

    private void updateStatus(Scan scan, String status) {

        scan.setStatus(status);

        SseEmitter emitter = emitters.get(scan.getId());

        if (emitter != null) {

            try {

                emitter.send(SseEmitter.event()
                        .name("scan-status")
                        .data(status));

                if (status.equals("COMPLETED") || status.equals("FAILED")) {
                    emitter.complete();
                }

            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }
}