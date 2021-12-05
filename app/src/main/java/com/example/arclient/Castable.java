package com.example.arclient;

public interface Castable {

    void cast(Object... obj);
    void launch(Object... obj);

    abstract class Streamable {

        abstract void _init();
        abstract void _launch(Object... obj);
        abstract void _stop(Object... obj);

    }


}
