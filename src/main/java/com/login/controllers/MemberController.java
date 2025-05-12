package com.login.controllers;

import com.login.dao.MemberRepo;
import com.login.dao.UserRepo;
import com.login.dto.DashboardDTO;
import com.login.entities.FeeHistory;
import com.login.entities.MemberEntity;
import com.login.entities.UsersEntity;
import com.login.exceptions.MemberNotFoundException;
import com.login.security.JWTService;
import com.login.services.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberController {


    //    @Autowired
//    private TrainerRepo trainerRepo;
    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    JWTService jwtService;
    @Autowired
    private FeeService feeService;

    @Autowired
    private FeeStatusScheduler feeStatusScheduler;

    @Autowired
    private MemberService memberService;

    @Autowired
    private FeeService2 feeService2;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepo userRepo;


    @GetMapping("/dashboard/home")
    public ResponseEntity<DashboardDTO> getDashboardHome(HttpServletRequest request, @RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", ""); // Remove "Bearer " prefix
        String username = jwtService.extractUserName(jwt);
        // Fetch user by username
        UsersEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DashboardDTO dashboardStats = dashboardService.getDashboardStats(user.getUser_id());
        return ResponseEntity.ok(dashboardStats);
    }

    @GetMapping("/dashboard/upcoming-fee")
    public ResponseEntity<Map<String, Long>> getFeeStatusCounts(HttpServletRequest request, @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", ""); // Remove "Bearer " prefix
        String username = jwtService.extractUserName(jwt); // Implement this in your jwtService



        UsersEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Long> feeCounts = feeService2.getUpcomingFeeCounts(user.getUser_id());
        return ResponseEntity.ok(feeCounts);
    }

    @PutMapping("/update-member/{id}")
    public ResponseEntity<?> updateMemberDetails(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            log.info("before find member");
            MemberEntity existingMember = memberRepo.findById(id)

                    .filter(member -> !member.isDeleted())
                    .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));
            log.info("after find member");
            if (updates.containsKey("membership_type")) existingMember.setEmail((String) updates.get("membership_type"));
            if (updates.containsKey("email")) existingMember.setEmail((String) updates.get("email"));
            if (updates.containsKey("phone")) existingMember.setPhone((String) updates.get("phone"));
            if (updates.containsKey("blood_group")) existingMember.setBlood_group((String) updates.get("blood_group"));
            if (updates.containsKey("address")) existingMember.setAddress((String) updates.get("address"));
            log.info("before save");
            memberRepo.save(existingMember);
            log.info("after save");
            return ResponseEntity.ok(Map.of("message", "Member updated successfully"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update member: " + ex.getMessage());
        }
    }

    @PostMapping("/{id}/update-status")
    public ResponseEntity<Map<String, Object>> updateFeeStatus(@PathVariable Long id, @RequestParam double amountPaid) {
        Map<String, Object> response = feeService.updateFeeStatus(id, amountPaid);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{memberId}")
    public ResponseEntity<List<FeeHistory>> viewFeeHistory(@PathVariable Long memberId) {
        if (memberRepo.findById(memberId).filter(member -> !member.isDeleted()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }
        return ResponseEntity.ok(feeService.viewFeeHistory(memberId));
    }

    @PostMapping("/reset-fee-status")
    public ResponseEntity<Map<String, Object>> resetFeeStatus() {
        try {
            feeStatusScheduler.resetFeeStatus();
            return ResponseEntity.ok(Map.of("status", "success", "message", "Fee status reset executed successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to reset fee status: " + e.getMessage()));
        }
    }
//
//        @GetMapping("/search")
//        public ResponseEntity<Map<String, Object>> searchMembers(@RequestParam(required = false) Long id,
//                                                                 @RequestParam(required = false) String name,
//                                                                 @RequestParam(required = false) String phone,
//                                                                 @RequestParam(required = false) String feeStatus,
//                                                                 @RequestParam(required = false) String membershipType,
//                                                                 @RequestParam(required = false) String gender,
//                                                                 @RequestParam(defaultValue = "0") int page,
//                                                                 @RequestParam(defaultValue = "10") int size) {
//            Specification<MemberEntity> spec = (root, query, criteriaBuilder) -> {
//                List<Predicate> predicates = new ArrayList<>();
//                predicates.add(criteriaBuilder.isFalse(root.get("isDeleted"))); // Filter non-deleted members
//
//                if (id != null) predicates.add(criteriaBuilder.equal(root.get("id"), id));
//                if (name != null && !name.isEmpty())
//                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
//                if (phone != null && !phone.isEmpty()) predicates.add(criteriaBuilder.equal(root.get("phone"), phone));
//                if (feeStatus != null && !feeStatus.isEmpty()) predicates.add(criteriaBuilder.equal(root.get("feeStatus"), feeStatus));
//                if (membershipType != null && !membershipType.isEmpty())
//                    predicates.add(criteriaBuilder.equal(root.get("membership_type"), membershipType));
//                if (gender != null && !gender.isEmpty()) predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
//
//                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//            };
//
//            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
//            Page<MemberEntity> membersPage = memberService.searchMembers(spec, pageable);
//
//            return ResponseEntity.ok(Map.of(
//                    "members", membersPage.getContent(),
//                    "currentPage", membersPage.getNumber(),
//                    "totalPages", membersPage.getTotalPages(),
//                    "totalMembers", membersPage.getTotalElements(),
//                    "pageSize", size
//            ));
//        }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMembers(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String feeStatus,
            @RequestParam(required = false) String membershipType,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Extract the JWT token and username
        String jwt = token.replace("Bearer ", "");
        String username = jwtService.extractUserName(jwt);

        // Fetch user by username
        UsersEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a specification to filter members
        Specification<MemberEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted"))); // Filter non-deleted members

            if (id != null) predicates.add(criteriaBuilder.equal(root.get("id"), id));
            if (name != null && !name.isEmpty())
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (phone != null && !phone.isEmpty()) predicates.add(criteriaBuilder.equal(root.get("phone"), phone));
            if (feeStatus != null && !feeStatus.isEmpty()) predicates.add(criteriaBuilder.equal(root.get("feeStatus"), feeStatus));
            if (membershipType != null && !membershipType.isEmpty())
                predicates.add(criteriaBuilder.equal(root.get("membership_type"), membershipType));
            if (gender != null && !gender.isEmpty()) predicates.add(criteriaBuilder.equal(root.get("gender"), gender));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Create a Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        // Search members using the specification and pagination
        Page<MemberEntity> membersPage = memberService.searchMembersByUserId(user.getUser_id(), spec, pageable);

        // Return the response with member data and pagination details
        return ResponseEntity.ok(Map.of(
                "members", membersPage.getContent(),
                "currentPage", membersPage.getNumber(),
                "totalPages", membersPage.getTotalPages(),
                "totalMembers", membersPage.getTotalElements(),
                "pageSize", size
        ));
    }



    @Transactional
    @PostMapping("/add-members")
    public ResponseEntity<Map<String, Object>> addMember( @RequestBody MemberEntity member,
                                                          @RequestHeader("Authorization") String token) {
        try {
            log.info("Entering addMember method");

            String jwt = token.replace("Bearer ", "");
            String username = jwtService.extractUserName(jwt);
            log.info("Extracted username: {}", username);

            // Fetch user by username
            UsersEntity user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            log.info("User found: {}", user.getUsername());

            member.setUsers(user);
            memberRepo.save(member);
            log.info("Member successfully saved");

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", HttpStatus.CREATED,
                    "message", "Member successfully created"
            ));
        } catch (Exception ex) {
            log.error("Error while adding member: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add member", "message", ex.getMessage()));
        }
    }


    @GetMapping("/all-members")
    public ResponseEntity<Map<String, Object>> displayAllMembers( HttpServletRequest request,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");
        String username = jwtService.extractUserName(jwt);
        log.info("Extracted username: {}", username);
        // Fetch user by username
        UsersEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        // Fetch active members by user_id with pagination
        Page<MemberEntity> membersPage = memberService.getActiveMembersByUserId(user.getUser_id(), pageable);

        // Return paginated response
        return ResponseEntity.ok(Map.of(
                "members", membersPage.getContent(),
                "currentPage", membersPage.getNumber(),
                "totalPages", membersPage.getTotalPages(),
                "totalMembers", membersPage.getTotalElements(),
                "pageSize", size
        ));
    }

    @Transactional
    @DeleteMapping( "/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteMember(@PathVariable Long id) {
        MemberEntity member = memberRepo.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));
        member.setDeleted(true); // Soft delete
        memberRepo.save(member);
        return ResponseEntity.ok(Map.of("message", "Member marked as deleted successfully"));
    }

//    @PostMapping("/add-trainer")
//    public ResponseEntity<TrainerEntity> saveTrainer(@RequestBody TrainerEntity trainer) {
//        TrainerEntity savedTrainer = trainerRepo.save(trainer);
//        return ResponseEntity.ok(savedTrainer);
//    }
}


