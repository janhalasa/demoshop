package com.halasa.demoshop.app.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.halasa.demoshop.api.ProductRestPaths;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.api.dto.ProductRestDto;
import com.halasa.demoshop.api.dto.response.ErrorResponse;
import com.halasa.demoshop.api.dto.response.ListResponse;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.rest.ErrorCode;
import com.halasa.demoshop.rest.mapper.PictureRestMapper;
import com.halasa.demoshop.rest.mapper.ProductRestMapper;
import com.halasa.demoshop.service.PictureService;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.repository.PictureRepository;
import com.halasa.demoshop.service.repository.ProductRepository;
import com.halasa.demoshop.test.fixture.PictureFixtures;
import com.halasa.demoshop.test.fixture.ProductFixtures;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductE2eTest extends EndToEndTestBase {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private ProductRestMapper productRestMapper;

    @Autowired
    private PictureRestMapper pictureRestMapper;

    @Autowired
    private PictureService pictureService;

    @Test
    @WithMockUser
    public void testGetProduct() throws Exception {
        final Product product = this.createProduct(ProductFixtures.some());

        final MvcResult mvcResult = this.mockMvc.perform(
                get(ProductRestPaths.GET, product.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProductRestDto productResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductRestDto.class);

        Assert.assertNull(product.getPicture());
        this.assertProductOwnFieldsEqual(product, productResponse);
        this.assertBasicEntityFieldsEqual(product, productResponse);
    }

    @Test
    @WithMockUser
    public void testGetNonExistingProduct() throws Exception {
        final MvcResult mvcResult = this.mockMvc.perform(
                get(ProductRestPaths.GET, 123L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
        Assert.assertEquals(ErrorCode.REQUIRED_RESULT_NOT_FOUND, errorResponse.getCode());
    }

    @Test
    @WithMockUser
    public void testGetWithInvalidFetch() throws Exception {
        final Product product = this.createProduct(ProductFixtures.some());
        final URI uri = UriComponentsBuilder.fromPath(ProductRestPaths.GET)
                .queryParam("fetch", "invalid.fetch")
                .buildAndExpand(product.getId())
                .encode().toUri();

        final MvcResult mvcResult = this.mockMvc.perform(
                get(uri)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
        Assert.assertEquals(ErrorCode.UNSUPPORTED_ASSOCIATION_FETCH, errorResponse.getCode());
    }

    @Test
    @WithMockUser
    public void testGetPicture() throws Exception {
        final Picture picture = PictureFixtures.some();
        final Product product = this.genericWriteOnlyRepository.save(ProductFixtures.some(picture));

        final MvcResult mvcResult = this.mockMvc.perform(
                get(ProductRestPaths.GET_PICTURE, product.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PictureRestDto pictureRestDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PictureRestDto.class);

        PictureE2eTest.assertPictureOwnFieldsEqual(picture, pictureRestDto);
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testCreate() throws Exception {
        final ProductRestDto productCreateRequest = this.productRestMapper.asProductRestDto(
                ProductFixtures.some(PictureFixtures.some()));

        final MvcResult mvcResult = this.mockMvc.perform(
                post(ProductRestPaths.CREATE)
                    .content(objectMapper.writeValueAsString(productCreateRequest))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        ProductRestDto productResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductRestDto.class);

        this.assertProductOwnFieldsEqual(productRestMapper.asProduct(productCreateRequest), productResponse);
        this.assertBasicEntityFieldsCreated(productCreateRequest, productResponse);
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testCreateWithSameCode() throws Exception {
        final Product existingProduct = this.genericWriteOnlyRepository.save(ProductFixtures.some());
        final ProductRestDto productCreateRequest = new ProductRestDto(
                existingProduct.getCode(),
                "some name",
                "some desc",
                BigDecimal.ONE,
                BigDecimal.ONE);

        final MvcResult mvcResult = this.mockMvc.perform(
                post(ProductRestPaths.CREATE)
                        .content(objectMapper.writeValueAsString(productCreateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
        Assert.assertEquals(ErrorCode.DATA_INTEGRITY_VIOLATION, errorResponse.getCode());
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testCreateWithInvalidPictureReferenceCode() throws Exception {
        final ProductRestDto productCreateRequest = this.productRestMapper.asProductRestDto(
                ProductFixtures.some(PictureFixtures.some()));

        final URI uri = UriComponentsBuilder.fromPath(ProductRestPaths.CREATE)
                .queryParam("pictureReferenceCode", "invalid-code")
//                .queryParam("fetches", "picture")
                .build().encode().toUri();

        final MvcResult mvcResult = this.mockMvc.perform(
                post(uri)
                        .content(objectMapper.writeValueAsString(productCreateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
        Assert.assertEquals(ErrorCode.INVALID_REFERENCE_CODE, errorResponse.getCode());
    }

    private void assertProductOwnFieldsEqual(Product expected, ProductRestDto actual) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getCode(), actual.getCode());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getPriceWithoutVat() + " != " + actual.getPriceWithoutVat(),
                BigDecimal.ZERO, expected.getPriceWithoutVat().subtract(actual.getPriceWithoutVat()).stripTrailingZeros());
        Assert.assertEquals(expected.getPriceWithVat() + " != " + actual.getPriceWithVat(),
                BigDecimal.ZERO, expected.getPriceWithVat().subtract(actual.getPriceWithVat()).stripTrailingZeros());
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testCreateWithPictureReference() throws Exception {
        final Picture picture = pictureService.save(PictureFixtures.some());

        final ProductRestDto productCreateRequest = this.productRestMapper.asProductRestDto(ProductFixtures.some());

        final URI uri = UriComponentsBuilder.fromPath(ProductRestPaths.CREATE)
                .queryParam("pictureReferenceCode", picture.getReferenceCode())
//                .queryParam("fetches", "picture")
                .build().encode().toUri();

        final MvcResult mvcResult = this.mockMvc.perform(
                post(uri)
                        .content(objectMapper.writeValueAsString(productCreateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        ProductRestDto productResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductRestDto.class);

        Picture pictureOfProduct = this.productRepository.getByPk(productResponse.getId(), Arrays.asList("picture")).getPicture();

        this.assertProductOwnFieldsEqual(productRestMapper.asProduct(productCreateRequest), productResponse);
        this.assertBasicEntityFieldsCreated(productCreateRequest, productResponse);

        PictureE2eTest.assertPictureOwnFieldsEqual(picture, pictureRestMapper.asPictureRestDto(pictureOfProduct));
    }

    private Product createProduct(Product product) {
        genericWriteOnlyRepository.persist(product);
        return product;
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testUpdate() throws Exception {
        final Product originalProduct = createProduct(ProductFixtures.some());

        final ProductRestDto productUpdateRequest = this.productRestMapper.asProductRestDto(ProductFixtures.some());
        productUpdateRequest.setId(originalProduct.getId());
        productUpdateRequest.setEntityVersion(originalProduct.getEntityVersion());

        final MvcResult mvcResult = this.mockMvc.perform(
                put(ProductRestPaths.UPDATE, originalProduct.getId())
                        .content(objectMapper.writeValueAsString(productUpdateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProductRestDto productResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductRestDto.class);

        this.assertProductOwnFieldsEqual(this.productRestMapper.asProduct(productUpdateRequest), productResponse);

        Assert.assertEquals(productUpdateRequest.getId(), productResponse.getId());
        Assert.assertTrue(originalProduct.getUpdatedAt().isBefore(productResponse.getUpdatedAt()));
        Assert.assertEquals(new Long(productUpdateRequest.getEntityVersion() + 1L), productResponse.getEntityVersion());
    }

    @Test
    @WithMockUser(authorities = Roles.ADMIN)
    public void testDelete() throws Exception {
        Product product = createProduct(ProductFixtures.some());

        this.mockMvc.perform(
                delete(ProductRestPaths.DELETE, product.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testSearch() throws Exception {
        final Product product = this.createProduct(ProductFixtures.some());

        this.createProduct(ProductFixtures.some());
        this.createProduct(ProductFixtures.some());
        this.createProduct(ProductFixtures.some());
        this.createProduct(ProductFixtures.some());

        final URI uri = UriComponentsBuilder.fromPath(ProductRestPaths.SEARCH)
                .queryParam("limit", 3)
                .queryParam("offset", 1)
                .queryParam("orderBy", "name:desc,id:asc")
                .queryParam("filter", "id=gt=" + product.getId())
                .build().encode().toUri();

        final MvcResult mvcResult = this.mockMvc
                .perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ListResponse<ProductRestDto> productResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ListResponse<ProductRestDto>>() {});

        Assert.assertEquals(3, productResponse.getResults().size());
    }

    @Test
    @WithMockUser
    public void testSearchByNamePart() throws Exception {
        final Product product = this.createProduct(ProductFixtures.some());

        this.createProduct(ProductFixtures.some());
        this.createProduct(ProductFixtures.some());
        this.createProduct(ProductFixtures.some());
        this.createProduct(ProductFixtures.some());

        final URI uri = UriComponentsBuilder.fromPath(ProductRestPaths.SEARCH)
                .queryParam("filter", "name==*" + product.getName().substring(1, product.getName().length() - 1) + "*")
                .build().encode().toUri();

        final MvcResult mvcResult = this.mockMvc
                .perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ListResponse<ProductRestDto> productResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ListResponse<ProductRestDto>>() {});

        Assert.assertEquals(1, productResponse.getResults().size());
        Assert.assertEquals(product.getName(), productResponse.getResults().get(0).getName());
        Assert.assertEquals(product.getId(), productResponse.getResults().get(0).getId());
    }

    @Test
    @WithMockUser
    public void testFulltextSearchSingleWord() throws Exception {
        ListResponse<ProductRestDto> response = fulltextSearch("Canon");
        Assert.assertEquals(2, response.getResults().size());
    }

    @Test
    @WithMockUser
    public void testFulltextSearchMultipleWords() throws Exception {
        ListResponse<ProductRestDto> response = fulltextSearch("Canon camera");
        Assert.assertEquals(2, response.getResults().size());
    }

    @Test
    @WithMockUser
    public void testFulltextSearchNoResult() throws Exception {
        ListResponse<ProductRestDto> response = fulltextSearch("Something else");
        Assert.assertEquals(0, response.getResults().size());
    }

//    @Autowired
//    private EntityManager entityManager;

    private ListResponse<ProductRestDto> fulltextSearch(String searchedTerm) throws Exception {

//        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//        fullTextEntityManager.createIndexer().startAndWait();

        this.createProduct(ProductFixtures.builder()
                .code("c-5D")
                .name("Canon 5D Mark IV")
                .description("The Canon EOS 5D series is arguably one of the most recognizable camera lines of the digital age and "
                        + "the Mark IV is designed to appeal to the same wide range of enthusiasts and professionals.")
                .build());
        this.createProduct(ProductFixtures.builder()
                .code("c-M5")
                .name("Canon EOS M5")
                .description("The EOS M5 is the most enthusiast-friendly EOS M yet. It's a 24MP mirrorless camera built around "
                        + "a Dual Pixel APS-C sensor, giving it depth-aware focus across most of the frame.")
                .build());
        this.createProduct(ProductFixtures.builder()
                .code("n-850")
                .name("Nikon D850")
                .description("The Nikon D850 is Nikon's latest high resolution full-frame DSLR, boasting a 46MP backside-illuminated CMOS "
                        + "sensor. This combination of properties should significantly widen the camera's appeal to high-end enthusiasts "
                        + "as well as a broad range of professional photographers.")
                .build());

        final URI uri = UriComponentsBuilder.fromPath(ProductRestPaths.SEARCH)
                .queryParam("fulltext", searchedTerm)
                .queryParam("limit", 3)
                .queryParam("offset", 0)
                .queryParam("fetch", "picture")
                .build().encode().toUri();

        final MvcResult mvcResult = this.mockMvc
                .perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ListResponse<ProductRestDto> productResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ListResponse<ProductRestDto>>() {});

        productResponse.getResults().forEach(productRestDto -> System.out.println(productRestDto.getCode()));

        return productResponse;
    }
}
