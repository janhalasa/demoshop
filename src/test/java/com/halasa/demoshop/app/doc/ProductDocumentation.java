package com.halasa.demoshop.app.doc;

import com.halasa.demoshop.api.ProductRestPaths;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.api.dto.ProductRestDto;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.rest.controller.ProductController;
import com.halasa.demoshop.rest.mapper.ProductRestMapper;
import com.halasa.demoshop.service.ProductService;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.repository.ListResult;
import com.halasa.demoshop.service.repository.PictureRepository;
import com.halasa.demoshop.service.repository.ProductRepository;
import com.halasa.demoshop.test.fixture.BasicEntityFixtures;
import com.halasa.demoshop.test.fixture.PictureFixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductDocumentation extends ApiDocumentationBase {

    @Autowired
    private ProductRestMapper productRestMapper;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private PictureRepository pictureRepository;

    @MockBean
    private ProductService productService;

    private Product product;

    private ListResult<Product> productList;

    @Before
    public void beforeEachTest() {
        Picture picture = BasicEntityFixtures.setBasicFields(PictureFixtures.some());

        this.product = BasicEntityFixtures.setBasicFields(new Product(
                "1001", "Canon EOS 5D", "Digital camera", BigDecimal.valueOf(3000), new BigDecimal("3599.90"), picture
        ));

        productList = new ListResult<Product>(
                Arrays.asList(this.product),
                Optional.of(57L),
                Optional.of(3),
                Optional.of(10)
        );

        when(productRepository.getByPk(Matchers.anyLong())).thenReturn(product);
        when(productRepository.getByPk(Matchers.anyLong(), any())).thenReturn(product);
        when(productRepository.search(any(), any(), any(), any(), any())).thenReturn(productList);
        when(pictureRepository.loadByProduct(product.getId())).thenReturn(picture);
        when(productService.save(any(), any())).thenReturn(product);
    }

    @Test
    @WithMockUser
    public void getById() throws Exception {

        final String uri = UriComponentsBuilder.fromPath(ProductRestPaths.GET)
                .queryParam("fetch", "picture")
                .build().toString();

        this.mockMvc.perform(get(uri, this.product.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "product-get",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Product identifier")
                        ),
                        requestParameters(
                                parameterWithName("fetch").description("Comma separated list of associations to fetch")
                        ),
                        responseFields(savedProductFields(true).build())
                ));
    }

    @Test
    @WithMockUser
    public void search() throws Exception {

        final String uri = UriComponentsBuilder.fromPath(ProductRestPaths.SEARCH)
                .queryParam("filter", "")
                .queryParam("offset", "")
                .queryParam("limit", "")
                .queryParam("fetch", "picture")
                .queryParam("orderBy", "")
                .build().toString();

        this.mockMvc.perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "product-search",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        requestParameters(searchQueryParams()),
                        responseFields(listResposeFields()
                                .addAll(savedProductFields(true).withPrefix("results[].").build())
                                .build())
                ));
    }

    @Test
    @WithMockUser
    public void getProductPicture() throws Exception {
        this.mockMvc.perform(get(ProductRestPaths.GET_PICTURE, this.product.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "product-picture-get",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Product identifier")
                        ),
                        responseFields(PictureDocumentation.savedPictureFields().build())
                ));
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void createProduct() throws Exception {
        final ProductRestDto request = this.productRestMapper.asProductRestDto(this.product);

        this.mockMvc.perform(
                post(ProductRestPaths.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document(
                        "product-create",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(),
                        requestParameters(
                                parameterWithName("pictureReferenceCode")
                                        .optional()
                                        .description("Reference code of a picture to associate with the product")
                        ),
                        requestFields(unsavedProductFields().build()),
                        responseFields(savedProductFields(true).build())
                ));
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void updateProduct() throws Exception {
        final ProductRestDto request = this.productRestMapper.asProductRestDto(this.product);

        this.mockMvc.perform(
                put(ProductRestPaths.UPDATE, request.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document(
                        "product-update",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Identifier of a product to be updated")
                        ),
                        requestParameters(
                                parameterWithName("pictureReferenceCode")
                                        .optional()
                                        .description("Reference code of a picture to associate with the product")
                        ),
                        requestFields(savedProductFields(true).build()),
                        responseFields(savedProductFields(true).build())
                ));
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void deleteProduct() throws Exception {
        this.mockMvc.perform(delete(ProductRestPaths.DELETE, this.product.getId()))
                .andExpect(status().isNoContent())
                .andDo(document(
                        "product-delete",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Identifier of a product to delete")
                        )));
    }

    public static FieldDescriptorListBuilder unsavedProductFields() {
        return FieldDescriptorListBuilder.from(ApiDocumentationBase.unsavedBasicEntityFields())
                .addAll(productFields().build())
                .addAll(PictureDocumentation.unsavedPictureFields().withPrefix("picture.").build());
    }

    public static FieldDescriptorListBuilder savedProductFields(boolean withPicture) {
        FieldDescriptorListBuilder builder = FieldDescriptorListBuilder.from(ApiDocumentationBase.basicEntityFields())
                .addAll(productFields().build());
        if (withPicture) {
            builder.addAll(PictureDocumentation.savedPictureFields().withPrefix("picture.").build());
        }
        return builder;
    }

    private static FieldDescriptorListBuilder productFields() {
        return FieldDescriptorListBuilder.of(
                fieldWithPath("code").description("Product code"),
                fieldWithPath("name").description("Product name"),
                fieldWithPath("description").description("Product description"),
                fieldWithPath("priceWithoutVat").description("Product price without VAT"),
                fieldWithPath("priceWithVat").description("Product price including VAT"),
                fieldWithPath("picture").description("Product picture").type(PictureRestDto.class));
    }
}
