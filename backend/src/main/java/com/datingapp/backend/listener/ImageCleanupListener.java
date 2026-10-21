package com.datingapp.backend.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.datingapp.backend.records.OldImagesCleanupEvent;
import com.datingapp.backend.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component
@RequiredArgsConstructor
public class ImageCleanupListener{

    private final FileStorageService storage;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void cleanupOldImages(OldImagesCleanupEvent event){
        for (String fileUrl : event.fileUrls()){
            try{
                storage.deleteFile(fileUrl);
            } catch (Exception e){
                log.error("Failed to delete old image: {}", e);

            }
        }
    }
}