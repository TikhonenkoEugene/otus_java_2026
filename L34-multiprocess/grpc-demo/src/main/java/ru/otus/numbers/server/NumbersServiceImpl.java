package ru.otus.numbers.server;

import io.grpc.stub.StreamObserver;
import ru.otus.numbers.NumberResponse;
import ru.otus.numbers.NumbersRequest;
import ru.otus.numbers.NumbersServiceGrpc;

@SuppressWarnings({"squid:S106"})
public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {

    private static final long GENERATION_INTERVAL_MS = 2000;

    @Override
    public void generateNumbers(NumbersRequest request, StreamObserver<NumberResponse> responseObserver) {
        long firstValue = request.getFirstValue();
        long lastValue = request.getLastValue();

        try {
            for (var value = firstValue + 1; value <= lastValue; value++) {
                Thread.sleep(GENERATION_INTERVAL_MS);
                responseObserver.onNext(
                        NumberResponse.newBuilder().setValue(value).build());
            }
            responseObserver.onCompleted();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            responseObserver.onError(e);
        }
    }
}
