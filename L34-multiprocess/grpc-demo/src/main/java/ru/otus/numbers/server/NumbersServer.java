package ru.otus.numbers.server;

import io.grpc.ServerBuilder;
import java.io.IOException;

@SuppressWarnings({"squid:S106"})
public class NumbersServer {

    public static final int SERVER_PORT = 8191;

    public static void main(String[] args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(SERVER_PORT)
                .addService(new NumbersServiceImpl())
                .build();

        server.start();
        System.out.println("Старт сервера");
        server.awaitTermination();
    }
}
