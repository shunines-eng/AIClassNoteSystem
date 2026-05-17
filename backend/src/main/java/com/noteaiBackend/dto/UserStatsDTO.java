package com.noteaiBackend.dto;


import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UserStatsDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long blockedUsers;
    private Map<String, Long> usersByRole;
    private Map<String, Long> usersByStatus;
    private Map<String, Long> usersByGender;

    @Data
    public static class RoleCount {
        private Integer role;
        private String roleName;
        private Long count;
    }
}