package com.preOrderService.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String image;
    private String introduction;

    @Builder
    public Member(Long id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void changeRole(String role) {
        this.role = role;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeImg(String img) {
        this.image = img;
    }

    public void changeIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void changePassword(String password) {
        this.password = password;
    }

}
