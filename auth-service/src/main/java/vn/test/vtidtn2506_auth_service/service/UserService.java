package vn.test.vtidtn2506_auth_service.service;

import vn.test.vtidtn2506_auth_service.dto.request.LoginRequestDto;
import vn.test.vtidtn2506_auth_service.dto.request.UserRegistrationDto;
import vn.test.vtidtn2506_auth_service.dto.response.LoginResponseDto;

public interface UserService {
    void createUser(UserRegistrationDto dto);

    LoginResponseDto login(LoginRequestDto dto);
}
