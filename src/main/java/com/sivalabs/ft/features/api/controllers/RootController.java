package com.sivalabs.ft.features.api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class RootController {

    @Value("${ft.openapi.version}")
    private String apiVersion;

    @Value("${ft.openapi.contact.name}")
    private String contactName;

    @Value("${ft.openapi.contact.email}")
    private String contactEmail;

    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok(apiVersion);
    }

    @GetMapping(path = "/contact", produces = "application/json")
    public ResponseEntity<ContactDto> getContact() {
        return ResponseEntity.ok(new ContactDto(contactName, contactEmail));
    }

    record ContactDto(String name, String email) {}
}
