package com.example.society.DTO.Request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOTPRepassRequest {
    private String otp;
    private String username;
    @NotBlank(message = "PASSWORD_FIELD")
    @Pattern.List({
            @Pattern(regexp = "^.{8,}$", message = "WEAK_PASSWORD_TOO_SHORT"),
            @Pattern(regexp = ".*[a-z].*", message = "WEAK_PASSWORD_NO_LOWERCASE"),
            @Pattern(regexp = ".*[A-Z].*", message = "WEAK_PASSWORD_NO_UPPERCASE")
    })
    private String password;
}
