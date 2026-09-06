package ru.otus.numbers.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import ru.otus.numbers.NumbersRequest;
import ru.otus.numbers.NumbersServiceGrpc;

@SuppressWarnings({"squid:S106"})
public class NumbersClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8191;

    private static final long FIRST_VALUE = 0;
    private static final long LAST_VALUE = 30;

    private static final int LOOP_LIMIT = 50;
    private static final long TICK_MS = 1000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Стартуем клиент");

        ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        var asyncStub = NumbersServiceGrpc.newStub(channel);
        var lastServerValue = new AtomicReference<Long>();
        var completedLatch = new CountDownLatch(1);

        var request = NumbersRequest.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_VALUE)
                .build();

        var response = new ClientStreamObserver(lastServerValue, completedLatch);

        asyncStub.generateNumbers(request, response);

        long currentValue = 0;

        for (int i = 0; i <= LOOP_LIMIT; i++) {
            Long serverValue = lastServerValue.getAndSet(null);
            currentValue = currentValue + (serverValue == null ? 0 : serverValue) + 1;
            System.out.printf("Текущее значение: %d%n", currentValue);
            Thread.sleep(TICK_MS);
        }

        channel.shutdown();
    }
}
