package com.pavlova.notes.utilities;

public enum Flags {
    DEFAULT,
    NO_RENAME,
    ROOT,
    NO_ADD,
    USER;

    public Integer flag() {
        return 1 << this.ordinal();
    }
}
