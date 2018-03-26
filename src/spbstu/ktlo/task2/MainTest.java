package spbstu.ktlo.task2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void pack() throws IOException {
        PipedOutputStream myOut = new PipedOutputStream();
        PipedInputStream input = new PipedInputStream(myOut);
        PipedInputStream myIn = new PipedInputStream();
        PipedOutputStream output = new PipedOutputStream(myIn);
        new Thread(() -> {
            Main task = new Main(input, output, Main.DO_PACK);
            try {
                task.pack();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Writer writer = new OutputStreamWriter(myOut);
        writer.write("1111111111 wddwi dhiwhveguhhhhh  8e888888dwhdduu;virkjfdigz, .hmi 0000000000");
        writer.close();
        Reader reader = new InputStreamReader(myIn);
        char[] buffer = new char[3 + 16 + 3 + 4 + 3 + 25 + 3];
        int result = reader.read(buffer);
        reader.close();

        assertEquals(buffer.length, result);
        assertEquals("\0\n1 wddwi dhiwhvegu\0\005h  8e\0\0068dwhdduu;virkjfdigz, .hmi \0\n0", new String(buffer));
    }

    @Test
    void unpack() throws IOException {
        PipedOutputStream myOut = new PipedOutputStream();
        PipedInputStream input = new PipedInputStream(myOut);
        PipedInputStream myIn = new PipedInputStream();
        PipedOutputStream output = new PipedOutputStream(myIn);
        new Thread(() -> {
            Main task = new Main(input, output, Main.DO_UNPACK);
            task.unpack();
        }).start();
        Writer writer = new OutputStreamWriter(myOut);
        writer.write("\0\n1 wddwi dhiwhvegu\0\005h  8e\0\0068dwhdduu;virkjfdigz, .hmi \0\n0");
        writer.close();
        Reader reader = new InputStreamReader(myIn);
        char[] buffer = new char[76];
        int result = reader.read(buffer);
        reader.close();

        assertEquals(buffer.length, result);
        assertEquals("1111111111 wddwi dhiwhveguhhhhh  8e888888dwhdduu;virkjfdigz, .hmi 0000000000", new String(buffer));
    }

    @Test
    void getOutputStream() {
        assertEquals(System.out, new Main(new String[]{ "-iou" }).getOutputStream());
    }

    @Test
    void getInputStream() {
        assertEquals(System.in, new Main(new String[]{ "-ioz" }).getInputStream());
    }
}