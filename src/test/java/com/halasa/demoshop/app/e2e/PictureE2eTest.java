package com.halasa.demoshop.app.e2e;

import com.halasa.demoshop.api.PictureRestPaths;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.api.dto.response.ReferenceCodeResponse;
import com.halasa.demoshop.rest.mapper.PictureRestMapper;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.test.fixture.PictureFixtures;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PictureE2eTest extends EndToEndTestBase {

    @Autowired
    private PictureRestMapper pictureRestMapper;

    @Test
    @WithMockUser
    public void testCreate() throws Exception {
        final PictureRestDto pictureCreateRequest = this.pictureRestMapper.asPictureRestDto(PictureFixtures.some());

        final MvcResult mvcResult = this.mockMvc.perform(
                post(PictureRestPaths.CREATE)
                        .content(objectMapper.writeValueAsString(pictureCreateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        ReferenceCodeResponse referenceCodeResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ReferenceCodeResponse.class);

        Assert.assertNotNull(referenceCodeResponse.getReferenceCode());
    }

    public static void assertPictureOwnFieldsEqual(Picture expected, PictureRestDto actual) {
        final Base64.Encoder base64encoder = Base64.getEncoder();

        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getContentType(), actual.getContentType());
        Assert.assertEquals(expected.getWidth(), actual.getWidth());
        Assert.assertEquals(expected.getHeight(), actual.getHeight());
        Assert.assertEquals(
                base64encoder.encodeToString(expected.getContent()),
                base64encoder.encodeToString(actual.getContent()));
    }
}
