package com.dto;

import java.io.Serializable;

public class DTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public int dLength;
    public int dRow;
    public int dCol;
    public int dType;
    public byte[] dBuffer;
    public KeyPointDTO[] kp;
    
}
