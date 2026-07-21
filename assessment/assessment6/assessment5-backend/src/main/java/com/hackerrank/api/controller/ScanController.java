package com.hackerrank.api.controller;

import com.hackerrank.api.model.Scan;
import com.hackerrank.api.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scan")
@CrossOrigin("*")
public class ScanController {

    private final ScanService scanService;

    @Autowired
    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Scan> getAllScan() {
        return scanService.getAllScan();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Scan createScan(@RequestBody Scan scan) {
        return scanService.createNewScan(scan);
    }

    // GET /scan/{id}
    @GetMapping("/{id}")
    public Scan getScanById(@PathVariable Long id) {
        return scanService.getScanById(id);
    }

    // DELETE /scan/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteScan(@PathVariable Long id) {
        scanService.deleteById(id);
    }

    // GET /scan/search/{domainName}?orderBy=numPages
    @GetMapping("/search/{domainName}")
    public List<Scan> searchScan(
            @PathVariable String domainName,
            @RequestParam String orderBy) {

        return scanService.search(domainName, orderBy);
    }
}