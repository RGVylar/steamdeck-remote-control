package com.mugreparty.steamdeck_remote_control.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mugreparty.steamdeck_remote_control.service.DeviceGraphService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GraphController {

    private final DeviceGraphService deviceGraphService;

    @GetMapping("/graph")
    public ResponseEntity<byte[]> getDeviceGraph(
            @RequestParam(value = "format", defaultValue = "svg") String format) {
        
        try {
            byte[] graphData = deviceGraphService.renderGraph(format);
            
            // Determine content type based on format
            MediaType contentType;
            switch (format.toLowerCase()) {
                case "svg":
                    contentType = MediaType.valueOf("image/svg+xml");
                    break;
                case "png":
                    contentType = MediaType.IMAGE_PNG;
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body("Unsupported format. Use 'svg' or 'png'".getBytes());
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType);
            headers.setCacheControl("no-store");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(graphData);
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to generate graph".getBytes());
        }
    }
}