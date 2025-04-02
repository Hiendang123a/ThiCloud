package com.example.society.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowInfo {
    private String username;
    private String name;
    private String avatar;
}
