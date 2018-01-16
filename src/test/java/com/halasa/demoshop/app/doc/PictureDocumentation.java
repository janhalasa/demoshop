package com.halasa.demoshop.app.doc;

import com.halasa.demoshop.api.PictureRestPaths;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.rest.controller.PictureController;
import com.halasa.demoshop.rest.mapper.PictureRestMapper;
import com.halasa.demoshop.service.PictureService;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.test.fixture.PictureFixtures;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PictureController.class)
public class PictureDocumentation extends ApiDocumentationBase {

    @Autowired
    private PictureRestMapper pictureRestMapper;

    @MockBean
    private PictureService pictureService;

    @Test
    @WithMockUser
    public void createProduct() throws Exception {
        final Picture picture = PictureFixtures.some();
        picture.setReferenceCode(UUID.randomUUID().toString());

        when(pictureService.save(any())).thenReturn(picture);

        PictureRestDto pictureRequest = this.pictureRestMapper.asPictureRestDto(picture);

        this.mockMvc.perform(
                post(PictureRestPaths.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pictureRequest)))
                .andExpect(status().isCreated())
                .andDo(document(
                        "picture-create",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        requestParameters(
                                parameterWithName("pictureReferenceCode")
                                        .optional()
                                        .description("Reference code of a picture to associate with the product")
                        ),
                        requestFields(
                                unsavedPictureFields().build()
                        ),
                        responseFields(
                                fieldWithPath("referenceCode").description("A code for referencing a picture for products and customers.")
                        )
                ));
    }

    public static FieldDescriptorListBuilder unsavedPictureFields() {
        return FieldDescriptorListBuilder.from(unsavedBasicEntityFields())
                .addAll(pictureFields().build());
    }

    public static FieldDescriptorListBuilder savedPictureFields() {
        return FieldDescriptorListBuilder
                .from(ApiDocumentationBase.basicEntityFields())
                .addAll(pictureFields().build());
    }

    private static FieldDescriptorListBuilder pictureFields() {
        return FieldDescriptorListBuilder.of(
                fieldWithPath("name").description("Picture name"),
                fieldWithPath("width").description("Picture width"),
                fieldWithPath("height").description("Picture height"),
                fieldWithPath("contentType").description("Content type of the picture"),
                fieldWithPath("content").description("Picture itself"));
    }
}
