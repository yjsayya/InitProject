package init.project.global.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
public class FileUtil {

    @Getter
    @AllArgsConstructor
    public static class FileInfo {
        private final String fileOriginalName;
        private final String extension;
        private final Long size;
    }

    @Getter
    @AllArgsConstructor
    public static class SaveFileInfo {
        private MultipartFile file;
        private String saveFileName;
    }

    public static FileInfo extractFileInfo(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        long size = file.getSize();

        return new FileInfo(originalName, extension, size);
    }

    public static void saveFiles(List<SaveFileInfo> fileInfoList, String uploadDir) {
        try {
            makeDirectory(uploadDir);
            for (SaveFileInfo fileInfo : fileInfoList) {
                MultipartFile file = fileInfo.getFile();
                String fileName = fileInfo.getSaveFileName();
                Path savePath = getNormalizedPath(uploadDir, fileName);

                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
                }
                log.info("[FILE] Saving File: {}/{}", uploadDir, fileName);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error Occurred During Saving Files", e);
        }
    }

    public static void deleteFile(String fullFilePath) {
        try {
            Path path = Paths.get(fullFilePath).normalize();
            Files.deleteIfExists(path);
            log.info("[FILE] Deleting File: {}", fullFilePath);
        } catch (Exception e) {
            log.warn("Error Occured During Deleting File");
            throw new IllegalArgumentException("Error Occured During Deleting File", e);
        }
    }

    private static void makeDirectory(String uploadDir) throws IOException {
        Path path = Paths.get(uploadDir).normalize();
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new IOException("디렉토리 생성 실패: " + uploadDir, e);
            }
        }
    }

    private static Path getNormalizedPath(String uploadDir, String fileName) {
        return Paths.get(uploadDir, fileName).normalize();
    }

}