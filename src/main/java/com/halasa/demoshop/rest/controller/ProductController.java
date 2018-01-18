package com.halasa.demoshop.rest.controller;

import com.halasa.demoshop.api.ProductRestPaths;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.api.dto.ProductRestDto;
import com.halasa.demoshop.api.dto.response.ListResponse;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.rest.ErrorCode;
import com.halasa.demoshop.rest.FetchListParser;
import com.halasa.demoshop.rest.OrderByParser;
import com.halasa.demoshop.rest.mapper.PictureRestMapper;
import com.halasa.demoshop.rest.mapper.ProductRestMapper;
import com.halasa.demoshop.service.ProductService;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.repository.ListResult;
import com.halasa.demoshop.service.repository.PictureRepository;
import com.halasa.demoshop.service.repository.ProductRepository;
import com.halasa.demoshop.service.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ProductController {

    private final ProductRepository productRepository;
    private final PictureRepository pictureRepository;
    private final ProductRestMapper productRestMapper;
    private final PictureRestMapper pictureRestMapper;
    private final ProductService productService;
    private final OrderByParser orderByParser = new OrderByParser();
    private final FetchListParser fetchListParser = new FetchListParser();

    @Autowired
    public ProductController(
            ProductRepository productRepository,
            PictureRepository pictureRepository,
            ProductRestMapper productRestMapper,
            PictureRestMapper pictureRestMapper,
            ProductService productService) {
        this.productRepository = productRepository;
        this.pictureRepository = pictureRepository;
        this.productRestMapper = productRestMapper;
        this.pictureRestMapper = pictureRestMapper;
        this.productService = productService;
    }

    @GetMapping(path = ProductRestPaths.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductRestDto getProduct(@PathVariable Long id, @RequestParam(required = false) String fetch) {
        final Product product = this.productRepository.getByPk(id, this.fetchListParser.parse(fetch));
        return this.productRestMapper.asProductRestDto(product);
    }

    @GetMapping(path = ProductRestPaths.GET_PICTURE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PictureRestDto getProductPicture(@PathVariable(name = "id") Long productId) {
        final Picture picture = this.pictureRepository.loadByProduct(productId);
        return this.pictureRestMapper.asPictureRestDto(picture);
    }

    @PreAuthorize(Roles.IS_ADMIN)
    @PostMapping(path = ProductRestPaths.CREATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductRestDto createProduct(
            @RequestBody @Validated ProductRestDto productCreateRequest,
            @RequestParam(required = false) Optional<String> pictureReferenceCode) {
        final Product product = this.productRestMapper.asProduct(productCreateRequest);
        product.setId(null);
        return this.productRestMapper.asProductRestDto(
                this.productService.save(product, pictureReferenceCode));
    }

    @PreAuthorize(Roles.IS_ADMIN)
    @PutMapping(path = ProductRestPaths.UPDATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductRestDto updateProduct(
            @PathVariable Long id,
            @RequestBody @Validated ProductRestDto productUpdateRequest,
            @RequestParam(required = false) Optional<String> pictureReferenceCode) {
        final Product productToUpdate = this.productRepository.getByPk(id);
        final Product updatedProduct = this.productRestMapper.asProduct(productUpdateRequest, productToUpdate);
        return productRestMapper.asProductRestDto(
                this.productService.save(updatedProduct, pictureReferenceCode));
    }

    @PreAuthorize(Roles.IS_ADMIN)
    @DeleteMapping(value = ProductRestPaths.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        this.productService.remove(id);
    }

    @GetMapping(value = ProductRestPaths.SEARCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<ProductRestDto> searchProducts(
            @RequestParam(required = false) String fulltext,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String fetch,
            @RequestParam(required = false) String orderBy) {

        if (fulltext != null && (filter != null || orderBy != null)) {
            throw new ValidationException(ErrorCode.INVALID_API_USAGE,
                    "You cannot combine fulltext search with RSQL filter or orderBy fields.");
        }

        List<String> fetches = this.fetchListParser.parse(fetch);

        final ListResult<Product> listResult;
        if (fulltext != null) {
            listResult = this.productRepository.fulltextSearch(
                    fulltext,
                    Optional.ofNullable(limit),
                    Optional.ofNullable(offset),
                    fetches);
        } else {
            listResult = this.productRepository.search(
                    Optional.ofNullable(filter),
                    Optional.ofNullable(limit),
                    Optional.ofNullable(offset),
                    fetches,
                    this.orderByParser.parse(orderBy));
        }

        return new ListResponse<ProductRestDto>(
                listResult.getResults().stream()
                        .map(product -> this.productRestMapper.asProductRestDto(product))
                        .collect(Collectors.toList()),
                listResult.getTotalCount(),
                listResult.getLimit(),
                listResult.getOffset()
        );
    }
}
