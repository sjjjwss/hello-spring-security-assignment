package kr.ac.hansung.service;

import kr.ac.hansung.entity.User;
import kr.ac.hansung.repository.RoleRepository;
import kr.ac.hansung.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("UserService 테스트")
class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);

    @Test
    @DisplayName("현재 비밀번호가 일치하면 새 비밀번호를 BCrypt로 인코딩해 저장한다")
    void changePassword_validCurrentPassword_encodesAndSavesNewPassword() {
        User user = new User();
        user.setEmail("admin@hansung.ac.kr");
        user.setPassword(passwordEncoder.encode("admin1234"));
        given(userRepository.findByEmail("admin@hansung.ac.kr")).willReturn(Optional.of(user));

        userService.changePassword("admin@hansung.ac.kr", "admin1234", "newpass123");

        assertThat(passwordEncoder.matches("newpass123", user.getPassword())).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("현재 비밀번호가 일치하지 않으면 비밀번호를 변경하지 않는다")
    void changePassword_wrongCurrentPassword_throwsException() {
        User user = new User();
        user.setEmail("admin@hansung.ac.kr");
        user.setPassword(passwordEncoder.encode("admin1234"));
        given(userRepository.findByEmail("admin@hansung.ac.kr")).willReturn(Optional.of(user));

        assertThatThrownBy(() ->
            userService.changePassword("admin@hansung.ac.kr", "wrongpass", "newpass123")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("현재 비밀번호가 일치하지 않습니다");

        assertThat(passwordEncoder.matches("admin1234", user.getPassword())).isTrue();
    }
}
