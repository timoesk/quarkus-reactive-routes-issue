package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

@ApplicationScoped
public class ReactiveRoutes {

    @Inject
    Concurrent concurrent;

    @Route(path = "/blocking-with-annotation", methods = HttpMethod.GET, type = Route.HandlerType.BLOCKING)
    public void hello(RoutingContext rc) {
        try {
            concurrent.inc();
            Thread.sleep(100);
            rc.response().end("hello");
        } catch (InterruptedException ignored) {
            // Ignore
        } finally {
            concurrent.dec();
        }
    }

    void init(@Observes Router router) {
        router.route().handler(BodyHandler.create());
        router.route(HttpMethod.GET, "/blocking-with-router").blockingHandler(this::hello, false);
    }
}
