package net.ioncannon.pipeline.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ResourceFetchActivity {
    @ActivityMethod
    ResourceFetchActivityResult startFetch(String resourceUrl);
}
