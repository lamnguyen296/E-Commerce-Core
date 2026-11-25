package vn.test.vtidtn2506_auth_service.service.impl;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.test.vtidtn2506_auth_service.dto.request.LoginRequestDto;
import vn.test.vtidtn2506_auth_service.dto.request.UserRegistrationDto;
import vn.test.vtidtn2506_auth_service.dto.response.LoginResponseDto;
import vn.test.vtidtn2506_auth_service.service.UserService;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Override
    public void createUser(UserRegistrationDto dto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(dto.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() != 201) {
            String errorMessage = response.readEntity(String.class);
            log.error("Create user error {}: {}", response.getStatus(), errorMessage);
            throw new RuntimeException("Không thể tạo user trong Keycloak: " + errorMessage);
        }
        log.info("Created user [{}] in Keycloak realm [{}] successfully", dto.getUsername(), realm);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        try {
            Keycloak userKeycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(dto.getUsername())
                    .password(dto.getPassword())
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();

            AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();
            log.info("User [{}] authenticated successfully with Keycloak, token issued", dto.getUsername());

            return LoginResponseDto.builder()
                    .accessToken(tokenResponse.getToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .expiresIn(tokenResponse.getExpiresIn())
                    .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
                    .tokenType(tokenResponse.getTokenType())
                    .build();
        } catch (NotAuthorizedException e) {
            log.error("Login failed for user {}: Invalid credentials", dto.getUsername());
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác!");
        } catch (Exception e) {
            log.error("Login error for user {}: {}", dto.getUsername(), e.getMessage());
            throw new RuntimeException("Đăng nhập thất bại: " + e.getMessage());
        }
    }
}
