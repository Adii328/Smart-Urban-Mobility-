package com.smartmobility.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminSummary {

    private long totalUsers;
    private long totalBookings;
    private long confirmedBookings;
    private double totalRevenue;
    private double totalRewardsDistributed;
}

