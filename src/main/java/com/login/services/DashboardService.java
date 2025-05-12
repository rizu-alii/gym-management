package com.login.services;

import com.login.dao.MemberRepo;
//import com.login.dao.TrainerRepo;
import com.login.dto.DashboardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    @Autowired
    private MemberRepo memberRepo;

//    @Autowired
//    private TrainerRepo trainerRepo;

    public DashboardDTO getDashboardStats(Long userId) {
        DashboardDTO dashboardDTO = new DashboardDTO();

        // Total Members for this User
        dashboardDTO.setTotalMembers(memberRepo.countByUserId(userId));

        // Current Members (Paid Members) for this User
        dashboardDTO.setCurrentMembers(memberRepo.countByUserIdAndFeeStatusPaid(userId));

        // Members Registered This Month for this User
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1);
        dashboardDTO.setMembersThisMonth(memberRepo.countByUserIdAndRegistrationDateAfter(userId, firstDayOfMonth));


        return dashboardDTO;
    }
}
