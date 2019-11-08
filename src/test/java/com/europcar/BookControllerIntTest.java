package com.europcar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BookControllerIntTest {

    private static Server bookServer = new Server(8080);
    private static Handler bookHandler;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        //String bookPort = valueOf(((ServerConnector) bookServer.getConnectors()[0]).getLocalPort());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterClass
    public static void stopServer() throws Exception {
        bookServer.stop();
    }

    @BeforeClass
    public static void startServer() throws Exception {
        bookServer.setHandler(new AbstractHandler() {
            @Override
            public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
                bookHandler.handle(s, request, httpServletRequest, httpServletResponse);
            }
        });
        bookServer.start();
    }

    @Test
    public void discountAllBook_shouldDiscountPrice_10Percent() throws Exception {
        //given
        List<Book> books = Arrays.asList(buildBook(2L, 100), buildBook(1L, 10));
        setBookHandler(books);
        String jsonResponseApi = "[ " +
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
                "]";
        //when
        ResultActions result = mockMvc.perform(get("/books/discount/10"));

        //then
        String expectedJson = result.andReturn().getResponse().getContentAsString();
        assertEquals(jsonResponseApi, expectedJson, JSONCompareMode.LENIENT);
    }

    private Book buildBook(Long id, Integer price) {
        Book book = new Book();
        book.setId(id);
        book.setPrice(BigDecimal.valueOf(price));
        book.setAuthor("author");
        book.setName("name");
        return book;
    }

    private void setBookHandler(Object responseApi) {
        bookHandler = new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json; charset=utf-8");

                ObjectMapper mapper = new ObjectMapper();
                //mapper.registerModule(new JavaTimeModule()); if date exist in json
                //mapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
                String json = mapper.writeValueAsString(responseApi);
                PrintWriter writer = response.getWriter();
                writer.println(json);
                writer.flush();
                writer.close();
                baseRequest.setHandled(true);
            }
        };
    }
}
