package com.javarush.jira.common.util;

import com.javarush.jira.bugtracking.attachment.FileUtil;
import com.javarush.jira.common.error.IllegalRequestDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;

public class FileUtilTest {

    @Test
    public void shouldUploadFileAndDelete() {
        String originDirectoryPath = "src/test/resources/file_util_data/";
        String originFileName = "origin-file.txt";
        String targetFileName = "target-file.txt";
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", originFileName,
                        "text/plain", originFileName.getBytes());

        FileUtil.upload(mockMultipartFile, originDirectoryPath, targetFileName);
        File uploadedFile = new File(originDirectoryPath + targetFileName);
        assert uploadedFile.exists();
        assert uploadedFile.isFile();
        Assertions.assertTrue(uploadedFile.delete());
    }

    @Test
    public void shouldFailsOnEmptyFile() {
        String originDirectoryPath = "src/test/resources/file_util_data/";
        String originFileName = "origin-file.txt";
        String targetFileName = "target-file.txt";
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", originFileName,
                        "text/plain", new byte[0]);
        Assertions.assertThrows(IllegalRequestDataException.class, () -> {
            FileUtil.upload(mockMultipartFile, originDirectoryPath, targetFileName);
        });
    }
}
