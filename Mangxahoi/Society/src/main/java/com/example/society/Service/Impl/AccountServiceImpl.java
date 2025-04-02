package com.example.society.Service.Impl;
import com.example.society.DTO.Request.*;
import com.example.society.Mapper.AccountMapper;
import com.example.society.Mapper.UserMapper;
import com.example.society.DTO.Response.AuthResponse;
import com.example.society.DTO.Response.OTPResponse;
import com.example.society.Entity.Account;
import com.example.society.Entity.User;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import com.example.society.Repository.IAccountRepository;
import com.example.society.Mail.OTPGenerater;
import com.example.society.Mail.UtilsEmail;
import com.example.society.Repository.IUserRepository;
import com.example.society.Security.TokenProvider;
import com.example.society.Service.Interface.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AccountServiceImpl implements AccountService {
    private static final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
    public  static final ConcurrentHashMap<String, List<Object>> otpStorage = new ConcurrentHashMap<>();
    public  static final ConcurrentHashMap<String, String> otpRepass = new ConcurrentHashMap<>();

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private UtilsEmail utilsEmail;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenProvider tokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public OTPResponse createAccount(RegisterUserRequest request) {
        if (accountRepository.findByUsername(request.getAccount().getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        // Chuyển đổi DTO sang entity
        User user = userMapper.toUser(request.getUser());
        Account account = accountMapper.toAccount(request.getAccount());
        System.out.println("user DOB: " + user.getDob());
        if(!validateUser(user.getDob()))
            throw new AppException(ErrorCode.UNDERAGE_USER);
        // Tạo OTP & gửi email
        String OTP = OTPGenerater.generateUniqueOTP();
        emailExecutor.submit(() -> utilsEmail.sendEmail(account.getUsername(), "Xác nhận OTP", UtilsEmail.CreateContent(OTP)));
        System.out.println("OTP: " + OTP);
        // Lưu OTP vào bộ nhớ tạm
        otpStorage.put(OTP, List.of(user, account));
        return new OTPResponse(account.getUsername(), OTP, "OTP đã gửi, vui lòng xác nhận!");
    }

    @Transactional
    @Override
    public void verifyOTP(VerifyOTPRequest verifyOTPRequest) {
        String otp = verifyOTPRequest.getOtp().replace("\"", "").trim();
        String username = verifyOTPRequest.getUsername();

        System.out.println("OTP đã nhập: " + otp);
        List<Object> userAccountList = otpStorage.get(otp);

        if (userAccountList == null || userAccountList.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (userAccountList.size() < 2) {
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }

        Account account = (Account) userAccountList.get(1);
        if (!account.getUsername().equals(username)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        otpStorage.remove(otp);
        User user = (User) userAccountList.get(0);
        System.out.println("AvartarID " + verifyOTPRequest.getIdAvatar());
        user.setAvatar(verifyOTPRequest.getIdAvatar());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        userRepository.save(user);
        account.setUserID(user.getUserID());
        account.setCreatedAt(new Date());
        accountRepository.save(account);
    }

    @Override
    public OTPResponse forgotPassword(String username) {
        System.out.println(username);
        if (!accountRepository.existsByUsername(username)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        String OTP = OTPGenerater.generateUniqueOTPRepass();
        emailExecutor.submit(() -> utilsEmail.sendEmail(username, "Xác nhận OTP", UtilsEmail.CreateContent(OTP)));
        System.out.println("OTP: " + OTP);
        otpRepass.put(OTP, username);
        return new OTPResponse(username, OTP, "OTP đã gửi, vui lòng xác nhận!");
    }

    @Transactional
    @Override
    public void verifyOTPRepass(VerifyOTPRepassRequest verifyOTPRepassRequest) {
        String otp = verifyOTPRepassRequest.getOtp().replace("\"", "").trim();

        System.out.println("OTP đã nhập: " + otp);

        if (!otpRepass.containsKey(otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        String username = otpRepass.remove(otp);

        if (username == null || !username.equals(verifyOTPRepassRequest.getUsername())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.setPassword(passwordEncoder.encode(verifyOTPRepassRequest.getPassword()));
        accountRepository.save(account);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        Account account = accountRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        return new AuthResponse(tokenProvider.generateAccessToken(account.getUserID().toString()), tokenProvider.generateRefreshToken(account.getUserID().toString()), account.getUserID().toString());
    }


    public boolean validateUser(Date dob) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dob);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age>=18;
    }
}
