package dev.vortres.lobby.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

public final class ResourceUtils {
    public static void extractResource(String source) throws URISyntaxException, IOException {
        final URI uri = ResourceUtils.class.getResource("/" + source).toURI();
        FileSystem fileSystem = null;

        // Only create a new filesystem if it's a jar file
        // (People can run this from their IDE too)
        if (uri.toString().startsWith("jar:"))
            fileSystem = FileSystems.newFileSystem(uri, Map.of("create", "true"));

        try {
            final Path jarPath = Paths.get(uri);
            final Path target = Path.of(source);
            if (Files.exists(target)) {
                try (Stream<Path> pathStream = Files.walk(target)) {
                    pathStream.sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                }
            }
            Files.walkFileTree(jarPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                    Path currentTarget = target.resolve(jarPath.relativize(dir).toString());
                    Files.createDirectories(currentTarget);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    final Path to = target.resolve(jarPath.relativize(file).toString());
                    Files.copy(file, to, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } finally {
            if (fileSystem != null)
                fileSystem.close();
        }
    }
}