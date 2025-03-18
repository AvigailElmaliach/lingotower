package com.lingotower.dto.user;
//משמש להחזרת מידע על ההתקדמות של המשתמש

public class UserProgressDTO {
    private Long userId;
    private double progressPercentage;

    public UserProgressDTO(Long userId, double progressPercentage) {
        this.userId = userId;
        this.progressPercentage = progressPercentage;
    }

    public Long getUserId() {
        return userId;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }
}

