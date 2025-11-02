package io;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ResultCSVWriter {
    private final Path path;

    public ResultCSVWriter(String filePath) {
        this.path = Paths.get(filePath);
    }
    public void ensureHeader() throws IOException {
        if (Files.exists(path)) return;
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            w.write("File,GraphID,Vertices,Edges,Cyclic,Algorithm,Time(ms),OpCount");
            w.newLine();
        }
    }
    public void appendRow(String file, int graphId, int vertices, int edges, boolean cyclic,
                          String algorithm, double timeMs, long opCount) throws IOException {
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
        String line = String.format(Locale.US, "%s,%d,%d,%d,%b,%s,%.3f,%d",
                file, graphId, vertices, edges, cyclic, algorithm, timeMs, opCount);
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(line);
            w.newLine();
        }
    }
}