package com.hackerrank.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackerrank.api.model.Scan;
import com.hackerrank.api.service.ScanService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ScanController.class)
class ScanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScanService scanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllScan() throws Exception {

        Scan scan = new Scan();
        scan.setId(1L);
        scan.setDomainName("test.com");

        List<Scan> scans = Arrays.asList(scan);

        Mockito.when(scanService.getAllScan()).thenReturn(scans);

        mockMvc.perform(get("/scan"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateScan() throws Exception {

        Scan scan = new Scan();
        scan.setId(1L);
        scan.setDomainName("test.com");

        Mockito.when(scanService.createNewScan(any(Scan.class)))
                .thenReturn(scan);

        mockMvc.perform(post("/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scan)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetScanById() throws Exception {

        Scan scan = new Scan();
        scan.setId(1L);
        scan.setDomainName("test.com");

        Mockito.when(scanService.getScanById(1L))
                .thenReturn(scan);

        mockMvc.perform(get("/scan/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteScan() throws Exception {

        Mockito.doNothing().when(scanService).deleteById(1L);

        mockMvc.perform(delete("/scan/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchScan() throws Exception {

        Scan scan = new Scan();
        scan.setId(1L);
        scan.setDomainName("test.com");

        Mockito.when(scanService.search(eq("test.com"), eq("numPages")))
                .thenReturn(List.of(scan));

        mockMvc.perform(get("/scan/search/test.com")
                        .param("orderBy", "numPages"))
                .andExpect(status().isOk());
    }

}