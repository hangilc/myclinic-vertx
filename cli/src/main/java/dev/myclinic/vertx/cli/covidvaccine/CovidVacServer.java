package dev.myclinic.vertx.cli.covidvaccine;

import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VoiceGrant;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class CovidVacServer {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        String certPath = System.getenv("MYCLINIC_SERVER_CERT");
        String privateKey = System.getenv("MYCLINIC_SERVER_PRIVATE_KEY");
        HttpServer server = vertx.createHttpServer(new HttpServerOptions()
                .setPemKeyCertOptions(new PemKeyCertOptions()
                        .setCertPath(certPath)
                        .setKeyPath(privateKey)
                )
                .setSsl(true)
        );
        Router router = Router.router(vertx);
        router.route("/").handler(ctx -> {
            ctx.response()
                    .setStatusCode(302)
                    .putHeader("Location", "/static/index.html")
                    .end();
        });
        router.route("/twilio-token").handler(CovidVacServer::twilioWebphoneToken);
        router.route("/static/*").handler(StaticHandler.create()
                .setWebRoot("cli/src/main/resources/covidvaccine/web")
                .setCachingEnabled(false)
                .setDefaultContentEncoding("UTF-8"));
        server.requestHandler(router);
        server.listen(3443, "0.0.0.0");
    }

    private static void twilioWebphoneToken(RoutingContext routingContext) {
        String twilioAccountSid = System.getenv("TWILIO_SID");
        String twilioApiKey = System.getenv("TWILIO_WEBPHONE_API_SID");
        String twilioApiSecret = System.getenv("TWILIO_WEBPHONE_API_SECRET");

        // Required for Voice
        String outgoingApplicationSid = System.getenv("TWILIO_WEBPHONE_APP_SID");
        String identity = "webphone";

        // Create Voice grant
        VoiceGrant grant = new VoiceGrant();
        grant.setOutgoingApplicationSid(outgoingApplicationSid);

        // Optional: add to allow incoming calls
        grant.setIncomingAllow(true);

        // Create access token
        AccessToken token = new AccessToken.Builder(
                twilioAccountSid,
                twilioApiKey,
                twilioApiSecret
        ).identity(identity).grant(grant).build();

        routingContext.response().headers().set("content-type", "text/plain");
        routingContext.response().end(token.toJwt());
    }

}
