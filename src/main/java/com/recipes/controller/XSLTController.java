package com.recipes.controller;

import com.recipes.service.XSLTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class XSLTController {

    @Autowired
    private XSLTService xsltService;


    @GetMapping("/recipes/xsl")
    public ResponseEntity<String> getRecipesAsHTML(
            @RequestParam(required = false) String userId) {
        String html = xsltService.transformRecipesToHTML(userId);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + "; charset=utf-8")
                .body(html);
    }


    @GetMapping("/recipes/xsl-view")
    public ResponseEntity<String> getXSLView(
            @RequestParam(required = false) String userId) {
        String html = xsltService.transformRecipesToHTML(userId);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + "; charset=utf-8")
                .body(html);
    }
}

