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
import java.util.Set;

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

    /** 파일 유효성 체크 - 확장자 */
    public static String allowedFileExtentsionYn(String extentsion, Set<String> allowedFileExtensions) {
        return (allowedFileExtensions.contains(extentsion)) ? "Y" : "N";
    }

    /** 파일 유효성 체크 - 파일크기*/
    public static String allowedFileSizeYn(int fileSize, int maxFileSize) {
        return (fileSize <= maxFileSize) ? "Y" : "N";
    }

    /** 서버에서 파일 읽어오기 */
    public static byte[] readFile(String directory, String fileName) {
        Path filePath = getNormalizedPath(directory, fileName);
        try {
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("파일이 존재하지 않습니다: " + filePath);
            }

            byte[] bytes = Files.readAllBytes(filePath);
            log.info("[FILE] Reading File: {}", filePath);
            return bytes;
        } catch (IOException e) {
            log.error("파일 읽기 중 오류 발생: {}", filePath, e);
            throw new IllegalArgumentException("Error occurred during reading file", e);
        }
    }

    /** 파일에서 정보 읽어오기 */
    public static FileInfo extractFileInfo(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        long size = file.getSize();

        return new FileInfo(originalName, extension, size);
    }

    /** 파일 저장하기 */
    public static void saveFile(MultipartFile file, String uploadDir) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Empty file cannot be saved");
            }

            makeDirectory(uploadDir);

            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.trim().isEmpty()) {
                originalName = "file_" + System.currentTimeMillis();
            }
            String safeFileName = Paths.get(originalName).getFileName().toString();

            Path savePath = getNormalizedPath(uploadDir, safeFileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("[FILE-] Saving File: {}/{}", uploadDir, safeFileName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error Occurred During Saving File", e);
        }
    }

    /** 파일 목록 저장하기 */
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

    /** 파일 삭제하기 */
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