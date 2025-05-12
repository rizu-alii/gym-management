//package com.login.services;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class UniqueValidator implements ConstraintValidator<Unique, String> {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private Class<?> entity;
//    private String field;
//
//    @Override
//    public void initialize(Unique constraintAnnotation) {
//        this.entity = constraintAnnotation.entity();
//        this.field = constraintAnnotation.field();
//    }
//
//    @Override
//    @Transactional
//    public boolean isValid(String value, ConstraintValidatorContext context) {
//        if (value == null || value.trim().isEmpty()) {
//            return true; // Let @NotNull handle required validation
//        }
//
//        String jpql = "SELECT COUNT(e) FROM " + entity.getSimpleName() + " e WHERE e." + field + " = :value";
//        Long count = entityManager.createQuery(jpql, Long.class)
//                .setParameter("value", value)
//                .getSingleResult();
//
//        return count == 0;
//    }
//}
