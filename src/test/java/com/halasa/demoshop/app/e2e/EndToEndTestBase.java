package com.halasa.demoshop.app.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halasa.demoshop.api.dto.BasicRestDto;
import com.halasa.demoshop.service.domain.BasicEntity;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Order;
import com.halasa.demoshop.service.domain.OrderItem;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.repository.GenericWriteOnlyRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
public abstract class EndToEndTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected GenericWriteOnlyRepository genericWriteOnlyRepository;

    @Before
    public void cleanDbBeforeEachTest() {
        this.genericWriteOnlyRepository.removeAll(OrderItem.class);
        this.genericWriteOnlyRepository.removeAll(Order.class);
        this.genericWriteOnlyRepository.removeAll(Product.class);
        this.genericWriteOnlyRepository.removeAll(Customer.class);
        this.genericWriteOnlyRepository.removeAll(Picture.class);
    }

    protected void assertBasicEntityFieldsEqual(BasicEntity expected, BasicRestDto actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getCreatedAt().withZoneSameInstant(ZoneId.of("UTC")), actual.getCreatedAt());
        Assert.assertEquals(expected.getUpdatedAt().withZoneSameInstant(ZoneId.of("UTC")), actual.getUpdatedAt());
        Assert.assertEquals(expected.getEntityVersion(), actual.getEntityVersion());
    }

    protected void assertBasicEntityFieldsCreated(BasicRestDto requestDto, BasicRestDto responseDto) {
        Assert.assertNotNull(responseDto.getId());
        Assert.assertNotNull(responseDto.getCreatedAt());
        Assert.assertNotNull(responseDto.getUpdatedAt());
        Assert.assertNotNull(responseDto.getEntityVersion());
    }
}
