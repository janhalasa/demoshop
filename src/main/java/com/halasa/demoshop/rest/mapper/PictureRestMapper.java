package com.halasa.demoshop.rest.mapper;

import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.service.domain.Picture;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withIgnoreFields = { "referenceCode", "products", "customers" })
public interface PictureRestMapper {

    @Maps()
    PictureRestDto asPictureRestDto(Picture in);

    @Maps(withIgnoreFields = { "createdAt", "updatedAt" })
    Picture asPicture(PictureRestDto in);

    @Maps(withIgnoreFields = { "createdAt", "updatedAt" })
    Picture asPicture(PictureRestDto in, Picture out);
}
