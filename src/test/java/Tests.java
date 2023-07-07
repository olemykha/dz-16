import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.LocalDate;

public class Tests {

    // https://restful-booker.herokuapp.com/

    private int bookingid;
    private ResponseBookingId bookingid2;

    @BeforeMethod
    public void setUp(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
    }

    @Test(priority = 1)
    public void createBooking() {

        BookingDates bookingDates= new BookingDates()
                .builder()
                .checkin(LocalDate.of(2022, 12,31))
                .checkout(LocalDate.of(2023,01,31))
                .build();
        Booking booking = new Booking()
                .builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .additionalneeds("Breakfast")
                .bookingdates(bookingDates)
                .build();

        Response response = RestAssured
                .given()
                .body(booking)
                .when()
                .post("/booking");
        response.then().statusCode(200);
        response.prettyPrint();
        bookingid = response.as(ResponseBooking.class).getBookingid();
    }

    @Test(priority = 2)
    public void getAllBookingIds(){
        Response response = RestAssured.given().log().all().get("/booking");
        response.then().statusCode(200);
        response.prettyPrint();
        ResponseBookingId[] bookingList = RestAssured.given().when().get("booking/").as(ResponseBookingId[].class);
        bookingid2 = bookingList[0];
    }

    @Test(priority = 3)
    public void updateTotalPrice() {
        JSONObject totalPriceUpdate = new JSONObject();
        totalPriceUpdate.put("totalPrice", 300);
        Response response = RestAssured
                .given()
                .auth()
                .preemptive()
                .basic("admin", "password123")
                .body(totalPriceUpdate.toString())
                .when()
                .patch("/booking/" + bookingid);
        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test(priority = 4)
    public void updateFirstNameAndAdditionalNeeds(){
        Booking booking = RestAssured
                .given()
                .when()
                .get("/booking/" + bookingid2.getBookingid())
                .as(Booking.class);
        booking.setFirstname("James");
        booking.setAdditionalneeds("Dinner");

        Response response = RestAssured
                .given()
                .auth()
                .preemptive()
                .basic("admin", "password123")
                .body(booking)
                .when()
                .put("/booking/" + bookingid2.getBookingid());
        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test(priority = 5)
    public void deleteBooking(){
        Response response = RestAssured
                .given()
                .auth()
                .preemptive()
                .basic("admin", "password123")
                .delete("/booking/" + bookingid);
        response.prettyPrint();
        response.then().statusCode(201);
    }
}
