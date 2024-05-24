package net.bigmangohead.crystalworks.util.serialization.trackedobject.handler;

import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;

public abstract class TrackedObjectHandler {

    public abstract void queueUpdate(TrackedObject<?> trackedObject);

}
