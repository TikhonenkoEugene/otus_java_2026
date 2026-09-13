package ru.otus.services.processors;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private static final Comparator<SensorData> BY_MEASUREMENT_TIME =
            Comparator.comparing(SensorData::getMeasurementTime).thenComparingInt(System::identityHashCode);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile ConcurrentSkipListSet<SensorData> dataBuffer = newBuffer();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    @Override
    public void process(SensorData data) {
        ConcurrentSkipListSet<SensorData> currentBuffer;
        lock.readLock().lock();
        try {
            currentBuffer = dataBuffer;
            currentBuffer.add(data);
        }
        finally {
            lock.readLock().unlock();
        }

        if (currentBuffer.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        ConcurrentSkipListSet<SensorData> bufferToFlush;
        lock.writeLock().lock();
        try {
            if (dataBuffer.isEmpty()) {
                return;
            }
            bufferToFlush = dataBuffer;
            dataBuffer = newBuffer();
        }
        finally {
            lock.writeLock().unlock();
        }

        try {
            writer.writeBufferedData(List.copyOf(bufferToFlush));
        }
        catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }

    private static ConcurrentSkipListSet<SensorData> newBuffer() {
        return new ConcurrentSkipListSet<>(BY_MEASUREMENT_TIME);
    }
}
