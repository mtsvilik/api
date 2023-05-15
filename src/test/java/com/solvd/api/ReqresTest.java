package com.solvd.api;

import org.testng.Assert;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;


import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTest {

    private static final String URL = "https://reqres.in/api/users?page=2";

    @Test
    public void checkIdAndAvatarTest() {
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL)
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        users.forEach(user -> {
            Assert.assertTrue(user.getAvatar().contains(user.getId().toString()));
        });

        Assert.assertTrue(users.stream().allMatch(user -> user.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());

        List<String> ids = users.stream().map(user -> user.getId().toString()).collect(Collectors.toList());

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }
}
