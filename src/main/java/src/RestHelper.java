package src;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import javax.swing.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shelkopryad on 08.11.2018.
 */
public class RestHelper {

    private static RestHelper instance;
    private static Response response;

    private RestHelper() {

    }

    public static RestHelper getInstance() {
        if (instance == null) {
            instance = new RestHelper();
        }
        return instance;
    }

    public String getValue(String path) {
        return response.body().path(path).toString();
    }

    public Response getResponse(String url, String token) {
        String apiUrl = getApiUrl(url);
        RequestSpecification specification = RestAssured.given().baseUri(apiUrl);
        specification.headers(new HashMap<String, String>() {{
            put("Referer", url);
            put("Authorization", token);
        }});
        response = specification.get();
        try {
            response.then().statusCode(200);
        } catch (AssertionError e) {
            JOptionPane.showMessageDialog(null, "Нужен новый токен!");
        }
        return response;
    }

    private String getApiUrl(String baseUrl) {
        String start = "https://pub.fsa.gov.ru/api/v1/rds/common/declarations/";

        Pattern pattern = Pattern.compile("(\\d)+");
        Matcher matcher = pattern.matcher(baseUrl);
        matcher.find();

        return start + matcher.group();
    }

}
