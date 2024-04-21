package com.example.diplomast.DTO;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    public int id;
    public String title;
    public String content;
    public int specialistid;
    public String status;
}
