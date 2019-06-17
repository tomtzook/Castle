package test;

import com.castle.io.Closeables;
import com.castle.io.streams.Streams;
import com.castle.io.streams.data.ReadOnlyStreamable;
import com.castle.nio.temp.TempPath;
import com.castle.zip.OpenZip;
import com.castle.zip.Zip;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        Collection<Closeable> closeables = new ArrayList<>();

        try {
            Zip zip = new Zip(Paths.get("/home/tomtzook/Downloads/file.zip"));
            closeables.add(zip.open());

            try (OpenZip openZip = zip.open()) {
                Collection<TempPath> tempPaths = openZip.findAllFiles(Pattern.compile(".*hel.*"))
                        .stream()
                        .map((path) -> {
                            try {
                                return openZip.extract(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                tempPaths.forEach(System.out::println);
                tempPaths.forEach(Closeables.silentCloser());

                Path path = openZip.findFile(Pattern.compile("^/fs/hello$"));
                ReadOnlyStreamable data = openZip.getPathData(path);
                try (InputStream inputStream = data.openRead()) {
                    Streams.copy(inputStream, System.out);
                }
            }
        } finally {
            System.out.println(((OpenZip)closeables.iterator().next()).isOpen());
            Closeables.silentClose(closeables);
        }
    }
}
