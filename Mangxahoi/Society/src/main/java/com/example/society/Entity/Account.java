package com.example.society.Entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {
    @Id
    private ObjectId accountID;
    @Indexed(unique = true)
    private ObjectId userID;
    private String username;
    private String password;
    private int failedAttempts;
    private Date lastLoginAt;
    private Date createdAt;
    private Boolean isPrivate = false;
}
