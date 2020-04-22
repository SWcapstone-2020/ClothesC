package com.example.myapplication;

public class MemberInfo {
    private String name;
    private String birth;

    public MemberInfo(String name, String birth) {
        this.name = name;
        this.birth = birth;
    }
    public String getName() {
        return name;
    }

    public String getBirth() {
        return birth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }


}
