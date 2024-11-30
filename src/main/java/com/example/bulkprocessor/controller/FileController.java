package com.example.bulkprocessor.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @PostMapping(value = "/combine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?>combineFiles(@RequestParam("files")List<MultipartFile>files){
        String outputFilePath="combined_file.txt";
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(outputFilePath))){
            for (MultipartFile file:files){
                BufferedReader reader =new BufferedReader(new InputStreamReader(file.getInputStream()));
                String line;
                while ((line=reader.readLine())!=null){
                    writer.write(line);
                    writer.newLine();
                }
                reader.close();
            }
            Path outputPath= Paths.get(outputFilePath);
            Files.write(outputPath,Files.readAllBytes(Paths.get(outputFilePath)));

            File combinedFile=new File(outputFilePath);
            InputStreamResource resource=new InputStreamResource(new FileInputStream(combinedFile));

                return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header("Content-Disposition","Attachment; filename" +combinedFile.getName())
                    .contentLength(combinedFile.length())
                    .body(resource);
            }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the file :"+e.getMessage());
        }
    }
}
