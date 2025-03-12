package com.sivalabs.ft.features.api.controllers;

import com.sivalabs.ft.features.AbstractIT;
import org.junit.jupiter.api.Test;

public class RootControllerTests extends AbstractIT {

    @Test
    public void getVersion() {
        var result = mvc.get().uri("/api/version");
        result.assertThat().body().asString().isEqualTo("v1.0.0");
    }

    @Test
    public void getContact() {
        var result = mvc.get().uri("/api/contact");
        result.assertThat().body().asString().isEqualTo("{\"name\":\"SivaLabs\",\"email\":\"support@sivalabs.in\"}");
    }
}
