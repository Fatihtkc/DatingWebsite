package com.datingapp.backend.records;

import java.util.List;

public record OldImagesCleanupEvent (List<String> fileUrls){
}