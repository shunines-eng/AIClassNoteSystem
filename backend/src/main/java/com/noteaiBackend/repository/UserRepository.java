package com.noteaiBackend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.noteaiBackend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserAccount(String userAccount);

    Optional<User> findByPhone(String phone);

    boolean existsByUserAccount(String userAccount);

    boolean existsByPhone(String phone);

    // 按角色查询
    List<User> findByRole(Integer role);

    Page<User> findByRole(Integer role, Pageable pageable);

    // 按状态查询
    List<User> findByStatus(Integer status);

    Page<User> findByStatus(Integer status, Pageable pageable);

    // 按性别查询
    List<User> findByGender(Integer gender);

    Page<User> findByGender(Integer gender, Pageable pageable);

    // 按年龄范围查询
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    List<User> findByAgeGreaterThanEqual(Integer age);

    List<User> findByAgeLessThanEqual(Integer age);

    // 按创建时间范围查询
    List<User> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    // 搜索功能
    @Query("SELECT u FROM User u WHERE " +
            "u.username LIKE %:keyword% OR " +
            "u.userAccount LIKE %:keyword% OR " +
            "u.realName LIKE %:keyword% OR " +
            "u.phone LIKE %:keyword% OR " +
            "u.signature LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE " +
            "u.username LIKE %:keyword% OR " +
            "u.userAccount LIKE %:keyword% OR " +
            "u.realName LIKE %:keyword% OR " +
            "u.phone LIKE %:keyword% OR " +
            "u.signature LIKE %:keyword%")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 组合条件查询
    @Query("SELECT u FROM User u WHERE " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:gender IS NULL OR u.gender = :gender)")
    List<User> findByConditions(@Param("role") Integer role,
                                @Param("status") Integer status,
                                @Param("gender") Integer gender);

    // 统计功能
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    Long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 0")
    Long countBlockedUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") Integer role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.status = 1")
    Long countActiveByRole(@Param("role") Integer role);

    // 获取用户数量统计
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();

    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countUsersByStatus();

    @Query("SELECT u.gender, COUNT(u) FROM User u GROUP BY u.gender")
    List<Object[]> countUsersByGender();

    // 验证登录
    @Query("SELECT u FROM User u WHERE u.userAccount = :userAccount AND u.password = :password")
    Optional<User> findByUserAccountAndPassword(@Param("userAccount") String userAccount,
                                                @Param("password") String password);
}
