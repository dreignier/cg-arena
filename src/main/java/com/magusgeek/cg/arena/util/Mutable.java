package com.magusgeek.cg.arena.util;

public class Mutable<T> {

    T value;

    public Mutable(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
