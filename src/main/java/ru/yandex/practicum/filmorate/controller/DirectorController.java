package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping()
    @ResponseStatus(CREATED)
    public DirectorDto createDirector(@RequestBody DirectorDto directorDto) {
        return directorService.addDirector(directorDto);
    }
    @GetMapping()
    @ResponseStatus(OK)
    public List<DirectorDto> getAllDirectors() {
        return directorService.findAllDirectors();
    }

    @GetMapping("/{directorId}")
    @ResponseStatus(OK)
    public DirectorDto getDirectorById(@PathVariable("directorId") Long directorId) {
        return directorService.findDirectorById(directorId);
    }

    @PutMapping()
    @ResponseStatus(OK)
    public DirectorDto updateDirector(@RequestBody DirectorDto directorDto) {
        return directorService.updateDirector(directorDto);
    }

    @DeleteMapping("/{directorId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteDirector(@PathVariable("directorId") Long directorId) {
        directorService.deleteDirector(directorId);
    }
}
