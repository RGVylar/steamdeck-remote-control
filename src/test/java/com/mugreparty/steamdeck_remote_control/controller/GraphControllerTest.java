package com.mugreparty.steamdeck_remote_control.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.mugreparty.steamdeck_remote_control.service.DeviceGraphService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GraphController.class)
class GraphControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceGraphService deviceGraphService;

    @Test
    void getDeviceGraph_withDefaultFormat_returnsSvg() throws Exception {
        // Given
        byte[] svgContent = "<svg>test</svg>".getBytes();
        when(deviceGraphService.renderGraph("svg")).thenReturn(svgContent);

        // When & Then
        mockMvc.perform(get("/api/v1/graph"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/svg+xml"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(svgContent));

        verify(deviceGraphService).renderGraph("svg");
    }

    @Test
    void getDeviceGraph_withSvgFormat_returnsSvg() throws Exception {
        // Given
        byte[] svgContent = "<svg>test</svg>".getBytes();
        when(deviceGraphService.renderGraph("svg")).thenReturn(svgContent);

        // When & Then
        mockMvc.perform(get("/api/v1/graph").param("format", "svg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/svg+xml"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(svgContent));

        verify(deviceGraphService).renderGraph("svg");
    }

    @Test
    void getDeviceGraph_withPngFormat_returnsInternalServerErrorWhenNotSupported() throws Exception {
        // Given
        when(deviceGraphService.renderGraph("png"))
                .thenThrow(new RuntimeException("PNG format requires native Graphviz installation"));

        // When & Then
        mockMvc.perform(get("/api/v1/graph").param("format", "png"))
                .andExpect(status().isInternalServerError());

        verify(deviceGraphService).renderGraph("png");
    }

    @Test
    void getDeviceGraph_withInvalidFormat_returnsBadRequest() throws Exception {
        // Given
        when(deviceGraphService.renderGraph("invalid"))
                .thenThrow(new IllegalArgumentException("Unsupported format: invalid"));

        // When & Then
        mockMvc.perform(get("/api/v1/graph").param("format", "invalid"))
                .andExpect(status().isBadRequest());

        verify(deviceGraphService).renderGraph("invalid");
    }

    @Test
    void getDeviceGraph_whenServiceThrowsException_returnsInternalServerError() throws Exception {
        // Given
        when(deviceGraphService.renderGraph("svg"))
                .thenThrow(new RuntimeException("Rendering failed"));

        // When & Then
        mockMvc.perform(get("/api/v1/graph"))
                .andExpect(status().isInternalServerError());

        verify(deviceGraphService).renderGraph("svg");
    }
}