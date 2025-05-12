package com.login.dao;

import com.login.entities.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemberRepo extends JpaRepository<MemberEntity, Long> {
    boolean existsByPhone(String phone);
    List<MemberEntity> findByIsDeletedFalse();
    Page<MemberEntity> findAll(Specification<MemberEntity> spec, Pageable pageable);

    @Query("SELECT m FROM MemberEntity m WHERE m.isDeleted = false")
    Page<MemberEntity> findAllActiveMembers(Pageable pageable);

    long countByFeeStatus(String feeStatus);
    long countByRegistrationDateAfter(LocalDateTime date);

    // Custom method to find active members by user_id with pagination
    @Query("SELECT m FROM MemberEntity m WHERE m.users.user_id = :userId AND m.isDeleted = false")
    Page<MemberEntity> findByUsers_UserIdAndIsDeletedFalse(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT m FROM MemberEntity m WHERE m.users.user_id = :userId AND m.isDeleted = false")
    List<MemberEntity> findByUsers_UserIdAndIsDeletedFalseForUpcomingFee(@Param("userId") Long userId);


    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.users.user_id = :userId AND m.isDeleted = false")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.users.user_id = :userId AND m.feeStatus = 'Paid' AND m.isDeleted = false")
    long countByUserIdAndFeeStatusPaid(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.users.user_id = :userId AND m.registrationDate >= :firstDayOfMonth AND m.isDeleted = false")
    long countByUserIdAndRegistrationDateAfter(@Param("userId") Long userId, @Param("firstDayOfMonth") LocalDateTime firstDayOfMonth);


}



//@Repository
//public interface MemberRepo extends JpaRepository<MemberEntity, Long>, JpaSpecificationExecutor<MemberEntity> {
//
//
//}
