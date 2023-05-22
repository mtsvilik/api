package com.solvd.api.reqres;

import com.solvd.api.specification.Specification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTest {

    private static final String URL = "https://reqres.in/";

    @Test
    public void checkIdAndAvatarTest() {
        Specification.installSpecification(Specification.requestSpecification(URL),
                Specification.responseSpecificationOk200());
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
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

    @Test
    public void successfulRegistrationTest() {
        Specification.installSpecification(Specification.requestSpecification(URL),
                Specification.responseSpecificationOk200());

        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";

        Registration user = new Registration("eve.holt@reqres.in", "pistol");
        SuccessfulRegistration successfulRegistration = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessfulRegistration.class);

        Assert.assertNotNull(successfulRegistration.getId());
        Assert.assertNotNull(successfulRegistration.getToken());

        Assert.assertEquals(id, successfulRegistration.getId());
        Assert.assertEquals(token, successfulRegistration.getToken());
    }

    @Test
    public void unSuccessfulRegistrationTest() {
        Specification.installSpecification(Specification.requestSpecification(URL),
                Specification.responseSpecificationError400());

        String error = "Missing password";

        Registration user = new Registration("sydney@fife", "");
        UnSuccessfulRegistration unSuccessfulRegistration = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessfulRegistration.class);

        Assert.assertEquals(error, unSuccessfulRegistration.getError());
    }

    @Test
    public void sortedYearsTest() {
        Specification.installSpecification(Specification.requestSpecification(URL),
                Specification.responseSpecificationOk200());

        List<Data> data = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", Data.class);

        List<Integer> years = data.stream().map(Data::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());

        Assert.assertEquals(sortedYears, years);
        System.out.println(years);
        System.out.println(sortedYears);
    }

    @Test
    public void deleteUserTest() {
        Specification.installSpecification(Specification.requestSpecification(URL),
                Specification.responseSpecificationUnique(204));

        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void timeTest() {
        Specification.installSpecification(Specification.requestSpecification(URL),
                Specification.responseSpecificationOk200());

        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        String regex1 = "(.{8})$";
        String regex2 = "(.{5})$";

        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex1, "");
        System.out.println(currentTime);
        String serverTime = response.getUpdatedAt().replaceAll(regex2, "");
        System.out.println(serverTime);
        Assert.assertEquals(currentTime, serverTime);
    }
}
