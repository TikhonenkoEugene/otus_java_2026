package ru.otus.numbers.client;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import ru.otus.numbers.NumberResponse;

@SuppressWarnings({"squid:S106"})
public class ClientStreamObserver implements StreamObserver<NumberResponse> {
    private final AtomicReference<Long> lastValue;
    private final CountDownLatch completedLatch;

    public ClientStreamObserver(AtomicReference<Long> lastValue, CountDownLatch completedLatch) {
        this.lastValue = lastValue;
        this.completedLatch = completedLatch;
    }

    @Override
    public void onNext(NumberResponse response) {
        var value = response.getValue();
        lastValue.set(value);
        System.out.printf("Получено от сервера: %d%n", value);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Ошибка запроса: " + t.getMessage());
        completedLatch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Запрос завершён");
        completedLatch.countDown();
    }
}
