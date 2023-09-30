package utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class Writer {
    public void writePos(double pos, BufferedWriter writer) {
        try {
            writer.write(pos + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}