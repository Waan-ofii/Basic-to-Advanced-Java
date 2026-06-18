package services;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.application.Platform;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloadService {
    
    // Download file with progress bar and multithreading
    public static Task<Void> downloadFileTask(String sourcePath, String destinationPath, 
                                                ProgressBar progressBar, Runnable onSuccess) {
        
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourcePath));
                     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destinationPath))) {
                    
                    File file = new File(sourcePath);
                    long fileSize = file.length();
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = 0;
                    
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        
                        // Update progress
                        double progress = (double) totalBytesRead / fileSize;
                        updateProgress(progress, 1.0);
                        
                        // Update ProgressBar on UI thread
                        if (progressBar != null) {
                            javafx.application.Platform.runLater(() -> 
                                progressBar.setProgress(progress)
                            );
                        }
                        
                        // Small delay to make progress visible (optional)
                        Thread.sleep(1);
                    }
                    
                    bos.flush();
                    
                    // Call success callback on UI thread
                    if (onSuccess != null) {
                        javafx.application.Platform.runLater(onSuccess);
                    }
                    
                    return null;
                }
            }
        };
    }
    
    // Download from URL (if materials are hosted online)
    public static Task<Void> downloadFromUrlTask(String fileUrl, String destinationPath,
                                                  ProgressBar progressBar, Runnable onSuccess) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int fileSize = connection.getContentLength();
                
                try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destinationPath))) {
                    
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    int totalBytesRead = 0;
                    
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        
                        if (fileSize > 0) {
                            double progress = (double) totalBytesRead / fileSize;
                            updateProgress(progress, 1.0);
                            if (progressBar != null) {
                                javafx.application.Platform.runLater(() -> 
                                    progressBar.setProgress(progress)
                                );
                            }
                        }
                    }
                    
                    if (onSuccess != null) {
                        javafx.application.Platform.runLater(onSuccess);
                    }
                    
                    return null;
                }
            }
        };
    }
}