package com.example.app01.DTO.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LastMessageResponse {
    private BubbleResponse bubbleResponse;
    private MessageResponse messageResponse;
}
