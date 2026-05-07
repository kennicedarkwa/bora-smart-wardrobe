package com.example.sea2_bora.implementation.Photo_Processing.service;
import com.example.sea2_bora.implementation.Photo_Processing.config.StorageProperties;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@Service
public class BackgroundRemovalService {

    private final StorageProperties properties;

    public BackgroundRemovalService(StorageProperties properties) {
        this.properties = properties;
    }

    public void removeBackground(Path inputPath, Path outputPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                List.of(
                        properties.getPythonBin(),
                        properties.getPythonScript(),
                        inputPath.toAbsolutePath().toString(),
                        outputPath.toAbsolutePath().toString()
                )
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder log = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                log.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Background removal fehlgeschlagen:\n" + log);
        }
    }
}