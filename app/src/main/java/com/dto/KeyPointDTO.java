package com.dto;

import org.opencv.core.KeyPoint;

import java.io.Serializable;

public class KeyPointDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    public double x;
    public double y;
    public float size;
    public float angle;
    public float response;
    public int octave;
    public int class_id;

    public void setFromKeyPoint(KeyPoint keyPoint) {

        this.x = keyPoint.pt.x;
        this.y = keyPoint.pt.y;
        this.size = keyPoint.size;
        this.angle = keyPoint.angle;
        this.response = keyPoint.response;
        this.octave = keyPoint.octave;
        this.class_id = keyPoint.class_id;

    }
    public KeyPoint getKeyPoint() {

        return new KeyPoint(
                (float) this.x,
                (float) this.y,
                this.size,
                this.angle,
                this.response,
                this.octave,
                this.class_id
        );
    }
}
