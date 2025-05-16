package net.ioncannon.pipeline.activities;

import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@ActivityImpl(workers = "rag-pipeline-worker")
public class ResourceFetchActivityImpl implements ResourceFetchActivity {
    @Override
    public ResourceFetchActivityResult startFetch(String resourceUrl) {
        try {
            // Please note that doing the following assumes everything is running on the same machine.
            // If you wanted to do this where everything isn't going to run on the same machine you 
            // would need to have a shared filesystem between all workers or use an external object 
            // store. 
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
            InputStream inputStream = URI.create(resourceUrl).toURL().openStream();
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new ResourceFetchActivityResult(
                    tempFile.getAbsolutePath(),
                    false,
                    null
            );
        } catch (Exception e) {
            return new ResourceFetchActivityResult(null, true, e.getMessage());
        }
    }
}
