package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.PasswordChangeDto;
import kr.ac.hansung.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/password")
    public String passwordForm(@ModelAttribute("passwordChangeDto") PasswordChangeDto dto) {
        return "user/password";
    }

    @PostMapping("/user/password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("passwordChangeDto") PasswordChangeDto dto,
                                 BindingResult bindingResult,
                                 RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "user/password";
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "새 비밀번호가 일치하지 않습니다");
            return "user/password";
        }

        try {
            userService.changePassword(
                userDetails.getUsername(),
                dto.getCurrentPassword(),
                dto.getNewPassword()
            );
            ra.addFlashAttribute("successMessage", "비밀번호가 변경되었습니다");
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("currentPassword", "wrong", e.getMessage());
            return "user/password";
        }

        return "redirect:/home";
    }
}
