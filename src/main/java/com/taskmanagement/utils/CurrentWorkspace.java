package com.taskmanagement.utils;

import com.taskmanagement.model.Workspace;

public final class CurrentWorkspace {

    private static volatile Workspace current;

    private CurrentWorkspace() {
    }

    public static void set(Workspace workspace) {
        current = workspace;
    }

    public static Workspace get() {
        return current;
    }

    public static void clear() {
        current = null;
    }
}
