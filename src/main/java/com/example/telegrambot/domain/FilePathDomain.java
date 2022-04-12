package com.example.telegrambot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class FilePathDomain {

    private Boolean ok;
    private Result result;

    @JsonIgnoreProperties(
            ignoreUnknown = true
    )
    @JsonRootName("result")
    @Data
    public static class Result {
        private String file_id;
        private String file_unique_id;
        private String file_size;
        private String file_path;
    }
}

