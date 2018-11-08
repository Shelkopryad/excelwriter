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
        url = getApiUrl(url);
        RestAssured.baseURI = url;
        RequestSpecification specification = new RequestSpecBuilder()
                .addHeader("Referer", url)
                .addHeader("Authorization", "qwe")
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
