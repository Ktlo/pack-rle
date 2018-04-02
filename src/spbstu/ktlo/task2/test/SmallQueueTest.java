package spbstu.ktlo.task2.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import spbstu.ktlo.task2.SmallQueue;

class SmallQueueTest {

    @Test
    void reorganize() {
        char[] buffer = new char[] {'0', '0', '1', '2'};
        SmallQueue queue = new SmallQueue(buffer);
        queue.reorganize();
        assertArrayEquals(new char[] {'0', '0', '1', '2'}, buffer);
        queue.next('3');
        queue.next('4');
        queue.reorganize();
        assertArrayEquals(new char[] {'1', '2', '3', '4'}, buffer);
    }
}