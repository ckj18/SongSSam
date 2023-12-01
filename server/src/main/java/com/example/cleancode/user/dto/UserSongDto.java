package com.example.cleancode.user.dto;

import com.example.cleancode.song.entity.ProgressStatus;
import com.example.cleancode.song.entity.Song;
import com.example.cleancode.song.repository.SongRepository;
import com.example.cleancode.user.entity.User;
import com.example.cleancode.user.entity.UserSong;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSongDto {
    private Long id;
    private Song song;
    private String vocalUrl;
    private String originUrl;
    private List<Integer> spectr;
    private LocalDateTime createdAt;
    private User user;
    private ProgressStatus status;

    public UserSong toUserSong(){
        return UserSong.builder()
                .id(id)
                .song(song)
                .vocalUrl(vocalUrl)
                .originUrl(originUrl)
                .spectr(spectr)
                .status(status)
                .user(user)
                .build();
    }
    public UserSongOutput outputFormat(){
        return UserSongOutput.builder()
                .userId(user.getId())
                .songId(song.getId())
                .createdAt(createdAt)
                .vocalUrl(vocalUrl)
                .originUrl(originUrl)
                .spectr(spectr)
                .status(status)
                .build();
    }
}
