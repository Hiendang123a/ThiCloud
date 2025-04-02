package com.example.society.DTO.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotBlank(message = "NAME_FIELD")
    @Size(max = 100, message = "NAME_TOO_LONG")
    private String name;

    @NotBlank(message = "GENDER_FIELD")
    private String gender;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a")
    @NotNull(message = "DOB_FIELD")
    private Date dob;

    @Nullable
    private String bio;

    @Pattern(regexp = "0\\d{9}", message = "INVALID_PHONE")
    @NotBlank(message = "PHONE_FIELD")
    private String phone;

    @Nullable
    private String avatar;
}