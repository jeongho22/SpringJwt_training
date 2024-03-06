package com.example.Service;

import com.example.Dto.JoinDTO;
import com.example.Entity.UserEntity;
import com.example.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername(); // Dto로 한번감싸서
        String password = joinDTO.getPassword(); // Dto로 한번감싸서

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {

            return;
        }

        log.info("값 : {}",isExist);

        UserEntity data = new UserEntity();

        data.setUsername(username); // 엔티티를 하나 더   만들어서 이쪽에 한번 감싼 dto 값 넣기
        data.setPassword(bCryptPasswordEncoder.encode(password)); // 엔티티를 하나 더 만들어서 이쪽에 한번 감싼 dto 값 넣기
        data.setRole("ROLE_ADMIN"); // 엔티티를 하나 더 만들어서 이쪽에 한번 감싼 dto 값 넣기

        userRepository.save(data);
    }
}