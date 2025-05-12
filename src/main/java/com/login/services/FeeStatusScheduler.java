package com.login.services;

import com.login.dao.MemberRepo;
import com.login.entities.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FeeStatusScheduler {

    @Autowired
    private MemberRepo memberRepo;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void resetFeeStatus() {
        List<MemberEntity> members = memberRepo.findAll();

        for (MemberEntity member : members) {
            LocalDateTime dueDate = calculateDueDate(member.getRegistrationDate(), member.getMembership_type());

            if (LocalDateTime.now().isAfter(dueDate) && "Paid".equalsIgnoreCase(member.getFeeStatus())) {
                member.setFeeStatus("Unpaid");
                memberRepo.save(member);
            }
        }
    }

    private LocalDateTime calculateDueDate(LocalDateTime registrationDate, String membershipType) {
        switch (membershipType.toLowerCase()) {
            case "daily":
                return registrationDate.plusDays(1);
            case "weekly":
                return registrationDate.plusWeeks(1);
            case "monthly":
                return registrationDate.plusMonths(1);
            case "yearly":
                return registrationDate.plusYears(1);
            default:
                throw new RuntimeException("Invalid membership type");
        }
    }
}

//
//package com.login.services;
//
//import com.login.dao.MemberRepo;
//import com.login.entities.MemberEntity;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//@Slf4j
//@Component
//public class FeeStatusScheduler {
//
//    @Autowired
//    private MemberRepo memberRepo;
//
//    // Scheduled to run every minute for testing purposes
//    @Scheduled(cron = "0 * * * * ?") // Runs every minute
//    public void resetFeeStatus() {
//        List<MemberEntity> members = memberRepo.findAll();
//log.info("runing");
//        for (MemberEntity member : members) {
//            LocalDateTime dueDate = calculateDueDate(member.getRegistrationDate(), member.getMembership_type());
//            log.info("runing 2");
//            if (LocalDateTime.now().isAfter(dueDate) && "Paid".equalsIgnoreCase(member.getFeeStatus())) {
//                member.setFeeStatus("Unpaid");
//                memberRepo.save(member);
//            }
//        }
//    }
//
//    // Custom durations for testing
//    private LocalDateTime calculateDueDate(LocalDateTime registrationDate, String membershipType) {
//        switch (membershipType.toLowerCase()) {
//            case "daily":
//                return registrationDate.plusSeconds(30); // 30 seconds for testing
//            case "weekly":
//                return registrationDate.plusSeconds(55); // 55 seconds for testing
//            case "monthly":
//                return registrationDate.plusMonths(1); // Retained original
//            case "yearly":
//                return registrationDate.plusYears(1); // Retained original
//            default:
//                throw new RuntimeException("Invalid membership type");
//        }
//    }
//}

