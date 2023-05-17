package com.solvd.api.kucoin;

import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class KucoinTest {

    public List<TickerData> getTicketData() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.kucoin.com/api/v1/market/allTickers")
                .then()
                .extract().jsonPath().getList("data.ticker", TickerData.class);
    }

    @Test
    public void checkCryptoTest() {
        List<TickerData> usdTickers = getTicketData()
                .stream()
                .filter(x -> x.getSymbol().endsWith("USDT"))
                .collect(Collectors.toList());
        Assert.assertTrue(usdTickers.stream().allMatch(x -> x.getSymbol().endsWith("USDT")));

    }

    @Test
    public void checkSortHighToLowTest() {
        List<TickerData> highToLowUsdTickers = getTicketData()
                .stream()
                .filter(x -> x.getSymbol().endsWith("USDT"))
                .sorted(new TickerComparatorHigh())
                .collect(Collectors.toList());

        List<TickerData> top10highToLowUsdTickers = highToLowUsdTickers
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        Assert.assertEquals(top10highToLowUsdTickers.get(0).getSymbol(),
                "SQUAD-USDT");
    }

    @Test
    public void checkSortLowToHighTest() {
        List<TickerData> top10LowToHighUsdTickers = getTicketData()
                .stream()
                .filter(x -> x.getSymbol().endsWith("USDT"))
                .sorted(new TickerComparatorLow())
                .limit(10)
                .collect(Collectors.toList());

        Assert.assertEquals(top10LowToHighUsdTickers.get(0).getSymbol(),
                "SUIA-USDT");
    }

    @Test
    public void checkMapTickerData() {
        Map<String, Float> usdMap = new HashMap<>();
        List<String> lowerCaseUsdTickers = getTicketData()
                .stream()
                .map(x -> x.getSymbol().toLowerCase())
                .collect(Collectors.toList());
        System.out.println(lowerCaseUsdTickers);

        getTicketData().forEach(x -> usdMap.put(x.getSymbol(), Float.parseFloat(x.getChangeRate())));
    }
}
