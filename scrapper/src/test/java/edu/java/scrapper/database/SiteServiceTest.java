package edu.java.scrapper.database;

import edu.java.service.SiteService;
import edu.java.util.CommonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

public class SiteServiceTest extends BaseDatabaseTest {
    @Autowired
    private SiteService siteService;

    @Test
    public void testNormalize() {
        assertThat(siteService.normalizeLink(CommonUtils.toURL(
            "https://github.com/google/googletest/blob/main/googlemock/include/gmock/gmock-cardinalities.h")))
            .isEqualTo("https://github.com/google/googletest");
    }

    @Test
    public void validateLinkTest() {
        assertThat(siteService.validateLink(CommonUtils.toURL(
            "https://github.com/google/googletest/blob/main/googlemock/include/gmock/gmock-cardinalities.h")))
            .isEmpty();

        assertThat(siteService.validateLink(CommonUtils.toURL("https://github.com"))).isPresent();

        assertThat(siteService.validateLink(CommonUtils.toURL(
            "https://github.com/fessur/Computer-Architecture/blob/main/lab2/testbench.sv")))
            .isPresent();

        assertThat(siteService.validateLink(CommonUtils.toURL("https://stackoverflow.com/questions/12276")))
            .isPresent();

        assertThat(siteService.validateLink(CommonUtils.toURL(
            "https://stackoverflow.com/questions/26881739/unable-to-get-spring-boot-to-automatically-create-database-schema")))
            .isEmpty();
    }
}
