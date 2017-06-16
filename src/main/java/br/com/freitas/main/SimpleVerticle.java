package br.com.freitas.main;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class SimpleVerticle extends AbstractVerticle {

	private static final Integer PORTA = 3000;
	private static final String TEMPO = System.getProperty("tempo", null);

	@Override
	public void start() {

		HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/status").handler(this::handlePostStatus);

		server.requestHandler(router::accept).listen(PORTA, this::handleListen);

	}

	private void handlePostStatus(RoutingContext routingContext) {

		LocalTime init = LocalTime.now().minusHours(3);

		System.out.println("----------------------------------");
		System.out.println("Init: " + init);

		routingContext.request().headers().forEach(getConsumer("header"));
		routingContext.request().params().forEach(getConsumer("param"));

		System.out.println("body: " + routingContext.getBodyAsString());

		HttpServerResponse response = routingContext.request().response();
		response.putHeader("content-type", "application/json");

		try {
			if (Objects.nonNull(TEMPO)) {
				Thread.sleep(Long.valueOf(TEMPO));
			}
		} catch (InterruptedException e) {
		}

		Response resp = new Response(init);
		System.out.println("response: " + resp);

		System.out.println("----------------------------------\n\n\n");
		response.end(Json.encodePrettily(resp));
	}

	private static Consumer<Entry<String, String>> getConsumer(final String param) {
		return h -> System.out.println(param + " " + h.getKey() + " = " + h.getValue());
	}

	private void handleListen(AsyncResult<HttpServer> result) {
		System.out
				.println("Servidor de pé na porta " + PORTA + ", com delay de " + Objects.toString(TEMPO, "0") + "ms.");
	}

}

class Response {

	private final String status = "OK";
	private final LocalTime date;
	private final long duration;

	public Response(LocalTime date) {
		this.date = date;
		this.duration = ChronoUnit.MILLIS.between(date, LocalTime.now().minusHours(3));
	}

	public String getStatus() {
		return status;
	}

	public LocalTime getDate() {
		return date;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		return "Response [status=" + status + ", date=" + date + ", duration=" + duration + "]";
	}

}
