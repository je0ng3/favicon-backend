package com.capstone.favicon.user.dto;

import lombok.Getter;

@Getter
public class MonthlyCountDto {
    private int month;
    private int count;

    public MonthlyCountDto(int month, int count) {
        this.month = month;
        this.count = count;
    }
}
