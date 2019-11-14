package com.europcar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BookControllerWireMockIntTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper mapper;


    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Before
    public void setUp() {
        //String bookPort = valueOf(((ServerConnector) bookServer.getConnectors()[0]).getLocalPort());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterClass
    public static void stopServer() {
    }

    @BeforeClass
    public static void startServer() {

    }

    @Test
    public void discountAllBook_shouldDiscountPrice_10Percent() throws Exception {
        //given
        List<Book> books = asList(buildBook(2L, 100), buildBook(1L, 10));
        List<Book> expectedBooks = asList(buildBook(2L, 90), buildBook(1L, 9));
        String jsonResponseApi = mapper.writeValueAsString(expectedBooks);
        /*String jsonResponseApi = "[ " +
                "   { " +
                "      \"id\":2," +
                "      \"name\":\"name\"," +
                "      \"author\":\"author\"," +
                "      \"price\":90" +
                "   }," +
                "   { " +
                "      \"id\":1," +
                "      \"name\":\"name\"," +
                "      \"author\":\"author\"," +
                "      \"price\":9" +
                "   }" +
                "]";*/

        String jsonResponseExternalApi = getJsonFromObject(books);
        stubFor(WireMock.get(urlPathMatching("/books"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponseExternalApi)));

        //when
        ResultActions result = mockMvc.perform(get("/books/discount/10"));
        //wireMockRule.setGlobalFixedDelay(100000000); // On configure le serveur pour répondre après 1100 ms

        //then
        String expectedJson = result.andReturn().getResponse().getContentAsString();
        assertEquals(jsonResponseApi, expectedJson, JSONCompareMode.LENIENT);
    }

    @Test
    public void discountAllBook_shouldReturn503_WhenExternalApiIsUnavailable() throws Exception {
        //given
        stubFor(WireMock.get(urlPathMatching("/books"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody("!!! Service Unavailable !!!")));


        //when
        ResultActions result = mockMvc.perform(get("/books/discount/10"));

        //then
        int status = result.andReturn().getResponse().getStatus();
        assertThat(status).isEqualTo(503);
    }

    @Test
    public void discountAllBook_shouldReturn503_WhenTimeout() throws Exception {
        //given
        List<Book> books = asList(buildBook(2L, 100), buildBook(1L, 10));
        /*String jsonResponseApi = "[ " +
                "   { " +
                "      \"id\":2," +
                "      \"name\":\"name\"," +
                "      \"author\":\"author\"," +
                "      \"price\":90" +
                "   }," +
                "   { " +
                "      \"id\":1," +
                "      \"name\":\"name\"," +
                "      \"author\":\"author\"," +
                "      \"price\":9" +
                "   }" +
                "]";*/

        String jsonResponseExternalApi = getJsonFromObject(books);
        stubFor(WireMock.get(urlPathMatching("/books"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponseExternalApi)
                        .withFixedDelay(2000))); //fixed delay to simulate timeout

        //when
        ResultActions result = mockMvc.perform(get("/books/discount/10"));

        //then
        verify(1, getRequestedFor(urlEqualTo("/books")));
        int status = result.andReturn().getResponse().getStatus();
        assertThat(status).isEqualTo(503);
    }

    private String getJsonFromObject(List<Book> books) throws JsonProcessingException {
        return mapper.writeValueAsString(books);
    }

    private Book buildBook(Long id, Integer price) {
        Book book = new Book();
        book.setId(id);
        book.setPrice(BigDecimal.valueOf(price));
        book.setAuthor("author");
        book.setName("name");
        return book;
    }

}
