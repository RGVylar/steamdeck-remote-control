package com.mugreparty.steamdeck_remote_control.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.mugreparty.steamdeck_remote_control.dto.DeviceInfo;
import com.mugreparty.steamdeck_remote_control.messaging.PresenceCache;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import lombok.RequiredArgsConstructor;

import static guru.nidi.graphviz.model.Factory.*;

@Service
@RequiredArgsConstructor
public class DeviceGraphService {

    private final PresenceCache presenceCache;

    /**
     * Renders a graph of connected devices in the specified format.
     * 
     * @param format the output format ("svg" or "png")
     * @return byte array containing the rendered graph
     * @throws IllegalArgumentException if format is not supported
     * @throws RuntimeException if rendering fails
     */
    public byte[] renderGraph(String format) {
        if (format == null) {
            format = "svg";
        }
        
        Format graphvizFormat;
        switch (format.toLowerCase()) {
            case "svg":
                graphvizFormat = Format.SVG;
                break;
            case "png":
                graphvizFormat = Format.PNG;
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format + ". Supported formats: svg, png");
        }

        try {
            Graph graph = buildGraph();
            String content = Graphviz.fromGraph(graph).render(graphvizFormat).toString();
            
            // The JS engine fallback always returns SVG content, even for PNG requests
            // Check if we got SVG when PNG was requested
            if (graphvizFormat == Format.PNG && content.contains("<svg")) {
                throw new RuntimeException("PNG format requires native Graphviz installation. Current environment only supports SVG format via JavaScript engine fallback.");
            }
            
            return content.getBytes();
        } catch (RuntimeException e) {
            // Re-throw runtime exceptions as-is
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to render graph", e);
        }
    }

    private Graph buildGraph() {
        Collection<DeviceInfo> devices = presenceCache.list();
        
        // Create central node
        Node centralNode = node("remote-control")
            .with(Style.FILLED, Color.BLUE);
        
        Graph graph = graph("DeviceGraph").directed();
        
        // Add central node
        graph = graph.with(centralNode);
        
        // Add device nodes and edges
        for (DeviceInfo device : devices) {
            boolean isOnline = isDeviceOnline(device);
            Color nodeColor = isOnline ? Color.GREEN : Color.GRAY;
            
            Node deviceNode = node(sanitizeNodeId(device.id()))
                .with(Style.FILLED, nodeColor);
            
            // Add device node and edge from central node
            graph = graph.with(
                deviceNode,
                centralNode.link(to(deviceNode))
            );
        }
        
        return graph;
    }

    private boolean isDeviceOnline(DeviceInfo device) {
        long now = System.currentTimeMillis();
        // Consider a device online if it was seen in the last 15 seconds
        return (now - device.lastSeenMs()) <= 15000;
    }

    private String sanitizeNodeId(String id) {
        // Replace characters that might cause issues in DOT format
        return id.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}