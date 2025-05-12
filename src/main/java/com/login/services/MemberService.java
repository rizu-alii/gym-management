package com.login.services;

import com.login.dao.MemberRepo;
import com.login.dao.UserRepo;
import com.login.entities.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MemberService {
    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    private UserRepo userRepo;

    public Page<MemberEntity> searchMembers(Specification<MemberEntity> spec, Pageable pageable) {
        return memberRepo.findAll(spec, pageable);
    }

    // Fetch active members with pagination and filter by user_id
    public Page<MemberEntity> getActiveMembersByUserId(Long userId, Pageable pageable) {
        return memberRepo.findByUsers_UserIdAndIsDeletedFalse(userId, pageable);
    }

    // Get list of all active members
    public List<MemberEntity> getAllActiveMembers() {
        return memberRepo.findByIsDeletedFalse();
    }

    // New method to search members by user_id and additional criteria
    public Page<MemberEntity> searchMembersByUserId(Long userId, Specification<MemberEntity> spec, Pageable pageable) {
        // Combine the user_id filter with additional criteria
        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("users").get("user_id"), userId));
        return memberRepo.findAll(spec, pageable);
    }
}
