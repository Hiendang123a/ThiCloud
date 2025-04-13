package com.example.society.DTO.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Size(max = 100, message = "NAME_TOO_LONG")
    private String name;
    private String gender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a")
    private Date dob;
    @Nullable
    private String bio;
    @Pattern(regexp = "0\\d{9}", message = "INVALID_PHONE")
    private String phone;
    @Nullable
    private String avatar;
}
