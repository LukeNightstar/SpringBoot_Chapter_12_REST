package com.springboot.rest.data.dto;

import lombok.Data;

@Data
public class MemberDto {

    private String name;
    private String email;
    private String organization;

    @Override
    public String toString() {
        return "MemberDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", organization='" + organization + '\'' +
                '}';
    }

}
