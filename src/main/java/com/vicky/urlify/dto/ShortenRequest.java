package com.vicky.urlify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ShortenRequest {

    @NotBlank(message = "URL cannot be empty")
    private String longUrl;

    // optional — user can provide their own alias e.g. "my-blog"
    @Pattern(regexp = "^[a-zA-Z0-9-_]*$",
            message = "Alias can only contain letters, numbers, hyphens and underscores")
    private String customAlias;
}