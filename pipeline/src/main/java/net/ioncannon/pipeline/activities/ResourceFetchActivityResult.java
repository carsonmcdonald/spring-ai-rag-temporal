package net.ioncannon.pipeline.activities;

public record ResourceFetchActivityResult(
        String fetchedFileLocation,
        boolean failed,
        String errorMessage
) {
}
