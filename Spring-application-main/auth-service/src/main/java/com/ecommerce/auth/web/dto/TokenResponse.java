package com.ecommerce.auth.web.dto;

import java.util.List;

public record TokenResponse(String accessToken, String tokenType, long expiresInMs) {}
