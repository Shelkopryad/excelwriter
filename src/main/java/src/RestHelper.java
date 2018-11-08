package src;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shelk on 08.11.2018.
 */
public class RestHelper {

    private static RestHelper instance;

    private RestHelper() {

    }

    public static RestHelper getInstance() {
        if (instance == null) {
            instance = new RestHelper();
        }
        return instance;
    }

    public String getValue(Response response, String path) {
        return response.body().path(path).toString();
    }

    public Response getResponse(String url) {
        String apiUrl = getApiUrl(url);
        RestAssured.baseURI = apiUrl;
        RequestSpecification specification = new RequestSpecBuilder()
                .addHeader("Referer", url)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiI4MTI4ZjQ2ZC00OWRkLTRmZmUtYWZlNy01ZWU5ODZhMGE1YjUiLCJzdWIiOiJhbm9ueW1vdXMiLCJleHAiOjE1NDI1NTg2MDh9.FpdzGi3s6atKKc7a51G_bXyH32F_bQ9_SvU1jCBVk2JlwOgFBzhIJXQJz5MB7uWcDFHx7au1Z77udgBHOXypZw")
                .build();

        return RestAssured.given().spec(specification).get();
    }

    private String getApiUrl(String baseUrl) {
        String start = "https://pub.fsa.gov.ru/api/v1/rds/common/declarations/";

        Pattern pattern = Pattern.compile("(\\d)+");
        Matcher matcher = pattern.matcher(baseUrl);
        matcher.find();

        return start + matcher.group();
    }

}
