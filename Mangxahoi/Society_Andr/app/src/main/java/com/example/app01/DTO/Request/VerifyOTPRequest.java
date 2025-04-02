package com.example.app01.DTO.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOTPRequest {
    private String otp;
    private String username;
    private String idAvatar;
    public VerifyOTPRequest(String otp, String username) {
        this.otp = otp;
        this.username = username;
    }
}
