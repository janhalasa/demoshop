package com.halasa.demoshop.rest.controller;

import com.halasa.demoshop.api.PictureRestPaths;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.api.dto.response.ReferenceCodeResponse;
import com.halasa.demoshop.rest.mapper.PictureRestMapper;
import com.halasa.demoshop.service.PictureService;
import com.halasa.demoshop.service.domain.Picture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PictureController {

    private final PictureRestMapper pictureRestMapper;

    private final PictureService pictureService;

    @Autowired
    public PictureController(
            PictureRestMapper pictureRestMapper,
            PictureService pictureService) {
        this.pictureRestMapper = pictureRestMapper;
        this.pictureService = pictureService;
    }

    @PostMapping(path = PictureRestPaths.CREATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ReferenceCodeResponse createPicture(@RequestBody @Validated PictureRestDto pictureRestDto) {
        final Picture pictureToCreate = this.pictureRestMapper.asPicture(pictureRestDto);
        final Picture created = this.pictureService.save(pictureToCreate);
        return new ReferenceCodeResponse(created.getReferenceCode());
    }

}
