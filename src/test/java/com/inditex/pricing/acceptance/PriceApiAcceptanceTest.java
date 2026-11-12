package com.inditex.pricing.acceptance;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static com.inditex.pricing.PriceTestData.BRAND_ID_STR;
import static com.inditex.pricing.PriceTestData.PRODUCT_ID_STR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PriceApiAcceptanceTest {

    private static final String PATH_URL = "/api/v1/prices";
    private static final String PARAM_APPLICATION_DATE = "applicationDate";
    private static final String PARAM_PRODUCT_ID = "productId";
    private static final String PARAM_BRAND_ID = "brandId";

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "[{index}] date={0} -> priceList={1}, price={2}")
    @MethodSource("requiredScenarios")
    void shouldResolveTheFivePricesRequiredByTheExercise(String applicationDate, int expectedPriceList,
                                                         double expectedPrice,
                                                         String expectedStartDate,
                                                         String expectedEndDate) throws Exception {

        mockMvc.perform(get(PATH_URL)
                        .param(PARAM_APPLICATION_DATE, applicationDate)
                        .param(PARAM_PRODUCT_ID, PRODUCT_ID_STR)
                        .param(PARAM_BRAND_ID, BRAND_ID_STR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId")
                        .value(Long.parseLong(PRODUCT_ID_STR)))
                .andExpect(jsonPath("$.brandId")
                        .value(Long.parseLong(BRAND_ID_STR)))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.price").value(expectedPrice))
                .andExpect(jsonPath("$.startDate").value(expectedStartDate))
                .andExpect(jsonPath("$.endDate").value(expectedEndDate))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    private static Stream<Arguments> requiredScenarios() {
        return Stream.of(
                Arguments.of("2020-06-14T10:00:00", 1, 35.50,
                        "2020-06-14T00:00:00", "2020-12-31T23:59:59"),
                Arguments.of("2020-06-14T16:00:00", 2, 25.45,
                        "2020-06-14T15:00:00", "2020-06-14T18:30:00"),
                Arguments.of("2020-06-14T21:00:00", 1, 35.50,
                        "2020-06-14T00:00:00", "2020-12-31T23:59:59"),
                Arguments.of("2020-06-15T10:00:00", 3, 30.50,
                        "2020-06-15T00:00:00", "2020-06-15T11:00:00"),
                Arguments.of("2020-06-16T21:00:00", 4, 38.95,
                        "2020-06-15T16:00:00", "2020-12-31T23:59:59")
        );
    }

    @ParameterizedTest(name = "[{index}] date={0}, productId={1}, brandId={2} should return -> 404")
    @MethodSource("notFoundScenarios")
    void shouldReturnNotFoundWhenNoPriceIsApplicable(String applicationDate, String productId,
                                                     String brandId) throws Exception {

        mockMvc.perform(get(PATH_URL)
                        .param(PARAM_APPLICATION_DATE, applicationDate)
                        .param(PARAM_PRODUCT_ID, productId)
                        .param(PARAM_BRAND_ID, brandId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Applicable price not found"));
    }

    private static Stream<Arguments> notFoundScenarios() {
        return Stream.of(
                Arguments.of("2026-09-01T10:00:00", PRODUCT_ID_STR, BRAND_ID_STR),
                Arguments.of("2020-06-14T10:00:00", "99999", BRAND_ID_STR),
                Arguments.of("2020-06-14T10:00:00", PRODUCT_ID_STR, "999"),
                Arguments.of("2020-06-13T23:59:59", PRODUCT_ID_STR, BRAND_ID_STR),
                Arguments.of("2021-01-01T00:00:00", PRODUCT_ID_STR, BRAND_ID_STR)
        );
    }

    @ParameterizedTest(name = "[{index}] invalid applicationDate={0}")
    @MethodSource("invalidApplicationDates")
    void shouldReturnBadRequestWhenApplicationDateIsInvalid(String applicationDate) throws Exception {

        mockMvc.perform(get(PATH_URL)
                        .param(PARAM_APPLICATION_DATE, applicationDate)
                        .param(PARAM_PRODUCT_ID, PRODUCT_ID_STR)
                        .param(PARAM_BRAND_ID, BRAND_ID_STR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private static Stream<Arguments> invalidApplicationDates() {
        return Stream.of(
                Arguments.of("invalid-date"),
                Arguments.of("2020-13-14T10:00:00"),
                Arguments.of("2020-06-14"),
                Arguments.of("14-06-2020T10:00:00"),
                Arguments.of(""),

                // Zona o desplazamiento horario no admitidos
                Arguments.of("2020-06-14T16:00:00Z"),
                Arguments.of("2020-06-14T16:00:00+02:00"),

                // Fecha y hora inexistentes
                Arguments.of("2020-02-30T16:00:00"),
                Arguments.of("2020-06-14T24:00:00"),

                // Formato distinto del contrato
                Arguments.of("2020-06-14T16:00"),
                Arguments.of("2020-06-14T16:00:00.123"),
                Arguments.of("2020-06-14 16:00:00")

        );
    }

    @ParameterizedTest(name = "[{index}] productId={0}, brandId={1} should return -> 400")
    @MethodSource("invalidIdentifiers")
    void shouldReturnBadRequestWhenAnIdentifierIsInvalid(String productId, String brandId) throws Exception {

        mockMvc.perform(get(PATH_URL)
                        .param(PARAM_APPLICATION_DATE, "2020-06-14T10:00:00")
                        .param(PARAM_PRODUCT_ID, productId)
                        .param(PARAM_BRAND_ID, brandId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private static Stream<Arguments> invalidIdentifiers() {
        // OpenAPI defines a minimum value of 1 for both identifiers (productId and brandId).
        // Must be positive numbers; zero, negative values and text are not allowed.
        return Stream.of(
                Arguments.of("0", "1"),
                Arguments.of("-1", "1"),
                Arguments.of("35455", "0"),
                Arguments.of("35455", "-1"),
                Arguments.of("invalid", "1"),
                Arguments.of("35455", "invalid")
        );
    }

    @ParameterizedTest(name = "Missing required parameter: {0}")
    @MethodSource("requestsWithMissingParameter")
    void shouldReturnBadRequestWhenARequiredParameterIsMissing(String missingParameter,
                                                               String nameParam1, String param1,
                                                               String nameParam2, String param2) throws Exception {
        MockHttpServletRequestBuilder request = get(PATH_URL);
        request.param(nameParam1, param1);
        request.param(nameParam2, param2);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private static Stream<Arguments> requestsWithMissingParameter() {
        return Stream.of(
                Arguments.of(PARAM_APPLICATION_DATE,
                        PARAM_PRODUCT_ID, PRODUCT_ID_STR,
                        PARAM_BRAND_ID, BRAND_ID_STR
                ),
                Arguments.of(PARAM_PRODUCT_ID,
                        PARAM_APPLICATION_DATE, "2020-06-14T10:00:00",
                        PARAM_BRAND_ID, BRAND_ID_STR
                ),
                Arguments.of(PARAM_BRAND_ID,
                        PARAM_APPLICATION_DATE, "2020-06-14T10:00:00",
                        PARAM_PRODUCT_ID, PRODUCT_ID_STR
                )
        );
    }

    @ParameterizedTest(name = "[{index}] boundary={0} -> priceList={1}")
    @MethodSource("boundaryScenarios")
    void shouldResolvePriceAtDateBoundaries(String applicationDate, int expectedPriceList,
                                            double expectedPrice) throws Exception {

        mockMvc.perform(get(PATH_URL)
                        .param(PARAM_APPLICATION_DATE, applicationDate)
                        .param(PARAM_PRODUCT_ID, PRODUCT_ID_STR)
                        .param(PARAM_BRAND_ID, BRAND_ID_STR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.price").value(expectedPrice));
    }

    private static Stream<Arguments> boundaryScenarios() {
        return Stream.of(
                // Inicio de la tarifa 1
                Arguments.of("2020-06-14T00:00:00", 1, 35.50),

                // Inicio de la tarifa 2
                Arguments.of("2020-06-14T14:59:59", 1, 35.50),
                Arguments.of("2020-06-14T15:00:00", 2, 25.45),

                // Fin inclusivo de la tarifa 2
                Arguments.of("2020-06-14T18:30:00", 2, 25.45),
                Arguments.of("2020-06-14T18:30:01", 1, 35.50),

                // Inicio de la tarifa 3
                Arguments.of("2020-06-14T23:59:59", 1, 35.50),
                Arguments.of("2020-06-15T00:00:00", 3, 30.50),

                // Fin inclusivo de la tarifa 3
                Arguments.of("2020-06-15T11:00:00", 3, 30.50),
                Arguments.of("2020-06-15T11:00:01", 1, 35.50),

                // Inicio de la tarifa 4
                Arguments.of("2020-06-15T15:59:59", 1, 35.50),
                Arguments.of("2020-06-15T16:00:00", 4, 38.95),

                // Fin inclusivo de las tarifas 1 y 4: gana la 4
                Arguments.of("2020-12-31T23:59:59", 4, 38.95)
        );
    }
}
