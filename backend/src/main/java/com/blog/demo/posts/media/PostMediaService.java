package com.blog.demo.posts.media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.demo.globalExceptions.HandleFileException;
import com.blog.demo.posts.Post;
import com.blog.demo.posts.PostRepository;

import jakarta.transaction.Transactional;

@Service
public class PostMediaService {

    private final PostMediaRepository postMediaRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public PostMediaService(PostMediaRepository postMediaRepository, PostRepository postRepository) {
        this.postMediaRepository = postMediaRepository;

    }

    private static final byte[] PNG_MAGIC = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] JPG_MAGIC_START = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    // Map extensions to their magic numbers    
    private boolean isMagicNumberValid(MultipartFile file, String expectedExt) throws IOException {
        if (file.getSize() < 8) {
            // Too small to check signature for most file types
            return false;
        }

        // Read the first 16 bytes (the longest signature we check)
        byte[] fileHeader = new byte[16];
        try (InputStream inputStream = file.getInputStream()) {
            int bytesRead = inputStream.read(fileHeader, 0, 16);
            if (bytesRead < 4) {
                return false; // Not enough bytes read
            }
        }

        // Check PNG
        if (expectedExt.equals("png") && Arrays.equals(Arrays.copyOfRange(fileHeader, 0, PNG_MAGIC.length), PNG_MAGIC)) {
            return true;
        }

        if ((expectedExt.equals("jpg") || expectedExt.equals("jpeg")) && Arrays.equals(Arrays.copyOfRange(fileHeader, 0, 3), JPG_MAGIC_START)) {
            return true;
        }

        if (expectedExt.equals("webp") && fileHeader[0] == 'R' && fileHeader[1] == 'I'
                && fileHeader[2] == 'F' && fileHeader[3] == 'F' && fileHeader[8] == 'W' && fileHeader[9] == 'E' && fileHeader[10] == 'B' && fileHeader[11] == 'P') {
            return true;
        }

        if (expectedExt.equals("avif") && fileHeader[4] == 'f' && fileHeader[5] == 't' && fileHeader[6] == 'y' && fileHeader[7] == 'p'
                && (fileHeader[8] == 'a' && fileHeader[9] == 'v' && fileHeader[10] == 'i' && fileHeader[11] == 'f')) {
            return true;
        }

        if (expectedExt.equals("mp4") && fileHeader[4] == 0x66 && fileHeader[5] == 0x74 && fileHeader[6] == 0x79 && fileHeader[7] == 0x70) {
            return true;
        }

        return false;
    }

    public List<PostMedia> saveMediaForNEwPost(Post post, List<MultipartFile> files) {

        List<PostMedia> savedMedia = new ArrayList<>();

        // saving media
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new HandleFileException("one of the files is empty");
            }

            String originalFileName = file.getOriginalFilename();

            // check if the file has an extension .
            if (originalFileName == null || !originalFileName.contains(".")) {
                throw new HandleFileException("invalid file :)");
            }

            String fileExt = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedImgs = List.of("png", "jpg", "jpeg", "webp", "avif");
            List<String> allowedVideos = List.of("mp4");

            boolean isAllowedImg = allowedImgs.contains(fileExt);
            boolean isAllowedVideo = allowedVideos.contains(fileExt);

            if (!isAllowedImg && !isAllowedVideo) {
                //unsuported extension 
                throw new HandleFileException("unsuported extension :)");
            }

            //  File Signature (Magic Number)
            try {
                if (!isMagicNumberValid(file, fileExt)) {
                    // spoofed files
                    throw new HandleFileException("File content does not match the expected format.");
                }
            } catch (IOException e) {
                throw new HandleFileException("Failed to read file signature.");
            }

            // type
            String contentType = file.getContentType();
            if (contentType == null) {
                throw new HandleFileException("no contentType found :)!!!");
            }
            if (isAllowedImg && !contentType.startsWith("image/")) {
                throw new HandleFileException("unsuported extension");
            }

            if (isAllowedVideo && !contentType.startsWith("video/")) {
                throw new HandleFileException("unsuported extension");
            }

            try {
                String uniqueFileName = uploadFileAndReturnUrl(file);
                String url = "http://localhost:8080/uploads/" + uniqueFileName;

                MediaType type = isAllowedVideo ? MediaType.VIDEO : MediaType.IMAGE;

                PostMedia media = new PostMedia();
                media.setPost(post);
                media.setUrl(url);
                media.setType(type);
                media.setFileName(uniqueFileName);
                media.setFileSize(file.getSize());
                media.setMimeType(contentType);

                PostMedia saved = postMediaRepository.save(media);
                savedMedia.add(saved);
                // post.setMedia(savedMedia);
                post.getMedia().add(media);
            } catch (IOException e) {
                throw new HandleFileException("failed to upload file : " + file.getOriginalFilename());
            }
        }

        return savedMedia;

    }

    private String uploadFileAndReturnUrl(MultipartFile file) throws IOException {
        // create a unique name
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path updalaodPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(updalaodPath)) {
            Files.createDirectories(updalaodPath);
        }
        // copy img
        Path filePath = updalaodPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    @Transactional
    public void deleteFileByUrl(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
            postMediaRepository.deleteByFileName(fileName);

        } catch (IOException e) {
            System.out.println("could not delete file " + e.getMessage());
        }
    }
}
