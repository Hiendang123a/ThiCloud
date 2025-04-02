package com.example.society.DTO.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LastMessageResponse {
    private BubbleResponse bubbleResponse;
    private MessageResponse messageResponse;
}
