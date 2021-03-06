package org.hadatac.console.controllers;

import play.*;
import com.typesafe.config.Config;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http.*;
import play.mvc.*;

import javax.inject.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CustomErrorHandler extends DefaultHttpErrorHandler {

    @Inject
    public CustomErrorHandler(Config configuration, Environment environment,
                        OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    protected CompletionStage<Result> onDevServerError(RequestHeader var1, UsefulException var2) {
        return CompletableFuture.completedFuture(Results.status(500, org.hadatac.console.views.html.error500.render()));
    }

    protected CompletionStage<Result> onProdServerError(RequestHeader var1, UsefulException var2) {
        return CompletableFuture.completedFuture(Results.status(500, org.hadatac.console.views.html.error500.render()));
    }
}
