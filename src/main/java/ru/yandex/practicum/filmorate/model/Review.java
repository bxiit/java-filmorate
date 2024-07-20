package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Review {

    public Review() {
        this.likedBy = new HashSet<>();
        this.dislikedBy = new HashSet<>();
    }

    private Long reviewId;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private String content;
    @JsonProperty(value = "isPositive")
    @NotNull
    private Boolean isPositive;
    private long useful;
    private Set<Long> likedBy;
    private Set<Long> dislikedBy;

}
