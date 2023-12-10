package com.example.cleancode.song.entity;

import com.example.cleancode.song.dto.SongDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Song {
    @Id
    private Long id;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String title;
    private String imgUrl;
    @Column(name = "artist", nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String artist;
    private boolean isTop;
    @ElementCollection
    private List<Integer> spectr;
    @Column(name = "vocalUrl", nullable = true, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String vocalUrl;
    @Column(name = "instrUrl", nullable = true, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String instUrl;
    @Column(name = "originUrl", nullable = true, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String originUrl;
    @Column(name = "genre", nullable = true, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    @ElementCollection
    public List<String> genre;
    @ElementCollection
    public List<Long> encoded_genre;
    private ProgressStatus status;
    public SongDto toSongDto(){
        return SongDto.builder()
                .id(id)
                .title(title)
                .imgUrl(imgUrl)
                .artist(artist)
                .genre(genre)
                .originUrl(originUrl)
                .instUrl(instUrl)
                .status(status)
                .build();
    }
    public Song changeStatus(ProgressStatus progressStatus){
//        this.status = progressStatus;
        return Song.builder()
                .id(id)
                .title(title)
                .imgUrl(imgUrl)
                .artist(artist)
                .genre(genre)
                .encoded_genre(encoded_genre)
                .isTop(isTop)
                .originUrl(originUrl)
                .spectr(spectr)
                .vocalUrl(vocalUrl)
                .instUrl(instUrl)
                .status(progressStatus)
                .build();
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(title, song.title) &&
                Objects.equals(artist, song.artist);
    }
    @Override
    public int hashCode() {
        return Objects.hash(title, artist);
    }
}
