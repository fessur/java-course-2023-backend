package edu.java.scrapper.database;

import edu.java.service.DomainService;
import edu.java.util.CommonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class DomainServiceTest extends IntegrationTest {
    @Autowired
    private DomainService domainService;
    @Test
    public void testNormalize() {
        assertThat(domainService.normalizeLink(CommonUtils.toURL(
            "https://github.com/google/googletest/blob/main/googlemock/include/gmock/gmock-cardinalities.h")))
            .isEqualTo("https://github.com/google/googletest");
    }

    @Test
    public void validateLinkTest() {
        assertThat(domainService.validateLink(CommonUtils.toURL(
            "https://github.com/google/googletest/blob/main/googlemock/include/gmock/gmock-cardinalities.h")))
            .isEmpty();

        assertThat(domainService.validateLink(CommonUtils.toURL("https://github.com"))).isPresent();

        assertThat(domainService.validateLink(CommonUtils.toURL(
            "https://github.com/fessur/Computer-Architecture/blob/main/lab2/testbench.sv")))
            .isPresent();

        assertThat(domainService.validateLink(CommonUtils.toURL("https://stackoverflow.com/questions/12276")))
            .isPresent();

        assertThat(domainService.validateLink(CommonUtils.toURL(
            "https://stackoverflow.com/questions/26881739/unable-to-get-spring-boot-to-automatically-create-database-schema")))
            .isEmpty();
    }
}
