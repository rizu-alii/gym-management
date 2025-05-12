//package com.login.dao;
//
//
//import com.login.entities.TrainerEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public interface TrainerRepo extends JpaRepository<TrainerEntity, Long> {
//
////    // Find all active trainers
////    List<TrainerEntity> findByIsDeletedFalse();
////
////    // Find trainers based on specialization
////    List<TrainerEntity> findBySpecializationAndIsDeletedFalse(String specialization);
////
////    // Find trainers who joined after a specific date
////    List<TrainerEntity> findByJoiningDateAfterAndIsDeletedFalse(LocalDate date);
////
////    // Count trainers (excluding deleted ones)
////    long countByIsDeletedFalse();
//}
