package com.login.dto;

import lombok.Data;

@Data
public class DashboardDTO {
    private long totalMembers;
    private long currentMembers;
    private long membersThisMonth;
    private long totalTrainers;

    // Getters and Setters
}
