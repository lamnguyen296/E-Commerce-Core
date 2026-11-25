package vn.test.vtidtn2506_auth_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.test.vtidtn2506_auth_service.dto.request.LoginRequestDto;
import vn.test.vtidtn2506_auth_service.dto.request.UserRegistrationDto;
import vn.test.vtidtn2506_auth_service.dto.response.LoginResponseDto;
import vn.test.vtidtn2506_auth_service.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto dto) {
        log.info("Receive request registering user [{}]", dto.getUsername());
        userService.createUser(dto);
        log.info("User [{}] registered successfully", dto.getUsername());
        return ResponseEntity.ok("Người dùng đã được tạo thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        log.info("Receive request login for user [{}]", dto.getUsername());
        LoginResponseDto response = userService.login(dto);
        log.info("User [{}] login successfully", dto.getUsername());
        return ResponseEntity.ok(response);
    }
}
