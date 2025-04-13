package com.example.society.Service.Interface;

import com.example.society.DTO.Request.*;
import com.example.society.DTO.Response.AuthResponse;
import com.example.society.DTO.Response.OTPResponse;
import com.example.society.DTO.Response.UpdateAccountResponse;
import org.bson.types.ObjectId;

public interface AccountService {

    OTPResponse createAccount(RegisterUserRequest request);
    void verifyOTP(VerifyOTPRequest verifyOTPRequest);
    OTPResponse forgotPassword(String username);
    void verifyOTPRepass(VerifyOTPRepassRequest verifyOTPRepassRequest);
    AuthResponse login(LoginRequest loginRequest);
    boolean getUserPrivacyStatus(ObjectId userID);
    UpdateAccountResponse updateAccount(ObjectId userID,UpdateAccountRequest updateAccountRequest);
    void updatePassword(ObjectId userID, RepassRequest repassRequest);
}