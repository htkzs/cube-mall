package com.cubemall.search.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@Data
@AllArgsConstructor
public class User {
    private int id;
    private String name;
    private String address;
    private Date birthday;
}
