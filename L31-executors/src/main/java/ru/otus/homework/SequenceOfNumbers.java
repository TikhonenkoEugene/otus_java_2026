package ru.otus.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Два потока печатают числа от 1 до 10, потом от 10 до 1.
 Надо сделать так, чтобы числа чередовались, т.е. получился такой вывод:
 Поток 1:1 2 3 4 5 6 7 8 9 10 9 8 7 6 5 4 3 2 1 2 3 4....
 Поток 2: 1 2 3 4 5 6 7 8 9 10 9 8 7 6 5 4 3 2 1 2 3....
 Всегда должен начинать Поток 1.
 */
public class SequenceOfNumbers {
    private static final Logger logger = LoggerFactory.getLogger(SequenceOfNumbers.class);

    private static final int MIN = 1;
    private static final int MAX = 10;
    private static final int FIRST_THREAD = 1;
    private static final int SECOND_THREAD = 2;

    private final Object monitor = new Object();

    private int current = MIN;
    private int direction = 1;
    private int turn = FIRST_THREAD;

    public static void main(String[] args) {
        SequenceOfNumbers sequence = new SequenceOfNumbers();

        Thread t1 = new Thread(() -> sequence.print(FIRST_THREAD), "T1");
        Thread t2 = new Thread(() -> sequence.print(SECOND_THREAD), "T2");

        t1.start();
        t2.start();
    }

    private void print(int threadId) {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (monitor) {
                while (turn != threadId) {
                    try {
                        monitor.wait();
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                logger.info("Поток {}: {}", threadId, current);

                if (threadId == SECOND_THREAD) {
                    if (current == MAX) {
                        direction = -1;
                    }
                    else if (current == MIN) {
                        direction = 1;
                    }
                    current += direction;
                }

                turn = (threadId == FIRST_THREAD) ? SECOND_THREAD : FIRST_THREAD;
                monitor.notifyAll();
            }
        }
    }
}
