package org.acme;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ReactiveRoutesTest {

    private static final ForkJoinPool POOL = new ForkJoinPool(10);

    @Inject
    Concurrent concurrent;

    @BeforeEach
    public void reset() {
        concurrent.reset();
    }

    @Test
    public void blockingWithAnnotationTest() {
        requests("/blocking-with-annotation");
        // Concurrent request limited by quarkus.vertx.event-loops-pool-size
        Assertions.assertEquals(1, concurrent.getMax());
    }

    @Test
    public void blockingWithRouterTest() {
        requests("/blocking-with-router");
        // Concurrent request limited by POOL
        Assertions.assertEquals(10, concurrent.getMax());
    }

    private void requests(String url) {
        POOL.submit(() -> IntStream.range(0, 40).parallel().forEach(i -> request(url))).join();
    }

    private void request(String url) {
        given()
                .when().get(url)
                .then().statusCode(200).body(is("hello"));
    }
}