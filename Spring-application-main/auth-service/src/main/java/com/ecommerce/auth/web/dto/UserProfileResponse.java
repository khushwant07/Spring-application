package com.ecommerce.auth.web.dto;

import java.util.List;

public record UserProfileResponse(
        Long id, String email, String firstName, String lastName, List<String> roles) {}
