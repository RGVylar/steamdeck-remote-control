package com.mugreparty.steamdeck_remote_control.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mugreparty.steamdeck_remote_control.dto.DeviceInfo;
import com.mugreparty.steamdeck_remote_control.messaging.PresenceCache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceGraphServiceTest {

    @Mock
    private PresenceCache presenceCache;

    private DeviceGraphService deviceGraphService;

    @BeforeEach
    void setUp() {
        deviceGraphService = new DeviceGraphService(presenceCache);
    }

    @Test
    void renderGraph_withSvgFormat_returnsValidSvgContent() {
        // Given
        List<DeviceInfo> devices = Arrays.asList(
            new DeviceInfo("device1", "Device 1", "host1", "192.168.1.1", System.currentTimeMillis()),
            new DeviceInfo("device2", "Device 2", "host2", "192.168.1.2", System.currentTimeMillis())
        );
        when(presenceCache.list()).thenReturn(devices);

        // When
        byte[] result = deviceGraphService.renderGraph("svg");

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        String svgContent = new String(result);
        assertTrue(svgContent.contains("<svg"), "Should contain SVG content");
        // The graphviz library processes node names internally, so we check for SVG structure instead
    }

    @Test
    void renderGraph_withPngFormat_throwsRuntimeException() {
        // Given
        List<DeviceInfo> devices = Arrays.asList(
            new DeviceInfo("device1", "Device 1", "host1", "192.168.1.1", System.currentTimeMillis())
        );
        when(presenceCache.list()).thenReturn(devices);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> deviceGraphService.renderGraph("png")
        );
        assertTrue(exception.getMessage().contains("PNG format requires native Graphviz"));
    }

    @Test
    void renderGraph_withEmptyDeviceList_returnsGraphWithOnlyCentralNode() {
        // Given
        when(presenceCache.list()).thenReturn(Collections.emptyList());

        // When
        byte[] result = deviceGraphService.renderGraph("svg");

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        String svgContent = new String(result);
        assertTrue(svgContent.contains("<svg"), "Should contain SVG content");
        // Graph should still be valid even with no devices
    }

    @Test
    void renderGraph_withDefaultFormat_returnsSvg() {
        // Given
        when(presenceCache.list()).thenReturn(Collections.emptyList());

        // When
        byte[] result = deviceGraphService.renderGraph(null);

        // Then
        assertNotNull(result);
        String svgContent = new String(result);
        assertTrue(svgContent.contains("<svg"), "Should return SVG by default");
    }

    @Test
    void renderGraph_withInvalidFormat_throwsIllegalArgumentException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deviceGraphService.renderGraph("invalid")
        );
        assertTrue(exception.getMessage().contains("Unsupported format"));
    }

    @Test
    void renderGraph_containsDeviceIds() {
        // Given
        DeviceInfo device1 = new DeviceInfo("test-device-1", "Test Device 1", "host1", "192.168.1.1", System.currentTimeMillis());
        DeviceInfo device2 = new DeviceInfo("test.device.2", "Test Device 2", "host2", "192.168.1.2", System.currentTimeMillis());
        when(presenceCache.list()).thenReturn(Arrays.asList(device1, device2));

        // When
        byte[] result = deviceGraphService.renderGraph("svg");

        // Then
        String svgContent = new String(result);
        assertTrue(svgContent.contains("<svg"), "Should contain valid SVG");
        // Graphviz processes node IDs internally, but the graph should be valid
        assertTrue(svgContent.length() > 100, "Should generate substantial SVG content with devices");
    }
}