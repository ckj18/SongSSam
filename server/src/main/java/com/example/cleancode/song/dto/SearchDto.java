package com.example.cleancode.song.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchDto {
    private String title;
    private String artist;
    private String albumTitle;
    private String songId;
    private String albumId;
}
