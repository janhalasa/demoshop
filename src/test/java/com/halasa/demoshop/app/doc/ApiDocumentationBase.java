package com.halasa.demoshop.app.doc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halasa.demoshop.service.CustomerService;
import com.halasa.demoshop.service.RevokedTokenService;
import com.halasa.demoshop.service.domain.RevokedToken;
import com.halasa.demoshop.service.repository.CustomerRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@TestPropertySource(locations="classpath:application-test.yml")
@ComponentScan({"com.halasa.demoshop.rest.mapper", "com.halasa.demoshop.app.security"})
public class ApiDocumentationBase {

    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected CustomerRepository customerRepository;

    @MockBean
    protected CustomerService customerService;

    @MockBean
    protected RevokedTokenService revokedTokenService;

    @Before
    public void beforeEachTest() {
        Mockito.when(revokedTokenService.isRevoked(Mockito.anyString())).thenReturn(false);
        Mockito.when(revokedTokenService.revoke(Mockito.anyString())).thenReturn(new RevokedToken(""));
    }

    public static FieldDescriptorListBuilder basicEntityFields() {
        return FieldDescriptorListBuilder.of(
                fieldWithPath("id").description("Entity identifier"),
                fieldWithPath("createdAt").description("Timestamp of when the entity was created"),
                fieldWithPath("updatedAt").description("Timestamp of the last entity update"),
                fieldWithPath("entityVersion").description("Entity version number for optimistic locking."));
    }

    public static FieldDescriptorListBuilder unsavedBasicEntityFields() {
        return FieldDescriptorListBuilder.of(
                fieldWithPath("id").ignored(),
                fieldWithPath("createdAt").ignored(),
                fieldWithPath("updatedAt").ignored(),
                fieldWithPath("entityVersion").ignored());
    }

    protected List<ParameterDescriptor> searchQueryParams() {
        return Arrays.asList(
                parameterWithName("filter").optional()
                        .description("RSQL filter. See https://github.com/jirutka/rsql-parser[RSQL Parser] at Github"),
                parameterWithName("offset").optional().description("How many result records will be skipped"),
                parameterWithName("limit").optional().description("Max number of records to be returned"),
                parameterWithName("fetch").optional().description("Comma separated list of associations to fetch"),
                parameterWithName("orderBy").optional().description("Comma separated list of fields paths with optional :asc or :desc "
                        + "suffix (:asc being default). Example: 'id:asc,size,name:desc'.")
        );
    }

    protected FieldDescriptorListBuilder listResposeFields() {
        return FieldDescriptorListBuilder.of(
                fieldWithPath("results").description("List of found objects"),
                fieldWithPath("totalCount").description("Total record count matching the requested criteria"),
                fieldWithPath("limit").description("Max number of results returned"),
                fieldWithPath("offset").description("How many records of the results will be skipped")
        );
    }
}
