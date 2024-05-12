package com.example.diplomast.DTO;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

public class Client implements Serializable {
    public int id;
    public String username;
    public String birthdate;
    public String login;
    public String password;
    public int timelineid;
    public String phone;
}
