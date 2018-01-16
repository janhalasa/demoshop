package com.halasa.demoshop.service;

import com.halasa.demoshop.rest.ErrorCode;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.repository.GenericWriteOnlyRepository;
import com.halasa.demoshop.service.repository.PictureRepository;
import com.halasa.demoshop.service.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PictureService {

    private final GenericWriteOnlyRepository genericWriteOnlyRepository;
    private final PictureRepository pictureRepository;
    private final ReferenceCodeGenerator referenceCodeGenerator;

    @Autowired
    public PictureService(
            GenericWriteOnlyRepository genericWriteOnlyRepository,
            PictureRepository pictureRepository,
            ReferenceCodeGenerator referenceCodeGenerator) {
        this.genericWriteOnlyRepository = genericWriteOnlyRepository;
        this.pictureRepository = pictureRepository;
        this.referenceCodeGenerator = referenceCodeGenerator;
    }

    @Transactional
    public Picture save(Picture picture) {
        picture.setReferenceCode(this.referenceCodeGenerator.generate());
        return this.genericWriteOnlyRepository.save(picture);
    }

    @Transactional
    public Picture loadByReferenceCode(String pictureReferenceCode) {
        Optional<Picture> optionalPicture = this.pictureRepository.getByReferenceCode(pictureReferenceCode);
        if (! optionalPicture.isPresent()) {
            throw new ValidationException(
                    ErrorCode.INVALID_REFERENCE_CODE,
                    "Picture with reference code '" + pictureReferenceCode + "' doesn't exist.");
        }
        final Picture picture = optionalPicture.get();
        // Clear the reference code - it can be used just once
        picture.setReferenceCode(null);
        return this.genericWriteOnlyRepository.save(picture);
    }
}
