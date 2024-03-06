package com.example.Repository;

import com.example.Entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {


    // username으로 사용자가 존재하는지 확인하는 메소드
    boolean existsByUsername(String username);

    // 유저 네임 조회
    UserEntity findByUsername (String username);

}