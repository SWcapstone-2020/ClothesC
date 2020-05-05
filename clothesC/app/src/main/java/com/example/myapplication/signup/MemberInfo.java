package com.example.myapplication.signup;

public class MemberInfo {
    private String name;
    private String birth;
    private String profilePic;

    public MemberInfo(String name, String birth, String profilePic) {
        this.name = name;
        this.birth = birth;
        this.profilePic=profilePic;
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


}
