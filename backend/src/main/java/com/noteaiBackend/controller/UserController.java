package com.noteaiBackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.dto.CreateUserRequest;
import com.noteaiBackend.dto.LoginRequest;
import com.noteaiBackend.dto.LoginResponse;
import com.noteaiBackend.dto.UpdateUserRequest;
import com.noteaiBackend.dto.UserDTO;
import com.noteaiBackend.dto.UserSimpleDTO;
import com.noteaiBackend.dto.UserStatsDTO;
import com.noteaiBackend.entity.User;
import com.noteaiBackend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 基础CRUD
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> dtos = users.stream()
                .map(UserDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Integer id) {
        try {
            UserDTO user = userService.findById(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserDTO savedUser = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("创建成功", savedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserDTO registeredUser = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("注册成功", registeredUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Integer id,
                                                           @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserDTO updatedUser = userService.update(id, request);
            return ResponseEntity.ok(ApiResponse.success("更新成功", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer id) {
        if (userService.findEntityById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "用户不存在"));
        }
    }

    // 查询功能
    @GetMapping("/account/{userAccount}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByAccount(@PathVariable String userAccount) {
        Optional<User> userOpt = userService.findByUserAccount(userAccount);
        if (userOpt.isPresent()) {
            UserDTO dto = UserDTO.fromEntity(userOpt.get());
            return ResponseEntity.ok(ApiResponse.success(dto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "用户不存在"));
        }
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByPhone(@PathVariable String phone) {
        Optional<User> user = userService.findByPhone(phone);
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(UserDTO.fromEntity(user.get())));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "用户不存在"));
        }
    }

    @GetMapping("/exists/account/{userAccount}")
    public ResponseEntity<ApiResponse<Boolean>> existsByUserAccount(@PathVariable String userAccount) {
        boolean exists = userService.existsByUserAccount(userAccount);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists/phone/{phone}")
    public ResponseEntity<ApiResponse<Boolean>> existsByPhone(@PathVariable String phone) {
        boolean exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    // 按角色查询
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable Integer role) {
        List<UserDTO> users = userService.findByRole(role);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/role/{role}/page")
    public ResponseEntity<ApiResponse<Page<UserSimpleDTO>>> getUsersByRolePage(
            @PathVariable Integer role, Pageable pageable) {
        Page<UserSimpleDTO> users = userService.findByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // 按状态查询
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByStatus(@PathVariable Integer status) {
        List<User> users = userService.findByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // 按性别查询
    @GetMapping("/gender/{gender}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByGender(@PathVariable Integer gender) {
        List<User> users = userService.findByGender(gender);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // 搜索
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(@RequestParam String keyword) {
        List<User> users = userService.searchByKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // 组合条件查询
    @GetMapping("/search/conditions")
    public ResponseEntity<ApiResponse<List<User>>> searchByConditions(
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer gender) {
        List<User> users = userService.findByConditions(role, status, gender);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // 统计功能
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatsDTO>> getUserStatistics() {
        UserStatsDTO stats = userService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/stats/active-count")
    public ResponseEntity<ApiResponse<Long>> getActiveUserCount() {
        Long count = userService.countActiveUsers();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/stats/blocked-count")
    public ResponseEntity<ApiResponse<Long>> getBlockedUserCount() {
        Long count = userService.countBlockedUsers();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/stats/role/{role}/count")
    public ResponseEntity<ApiResponse<Long>> getUsersCountByRole(@PathVariable Integer role) {
        Long count = userService.countByRole(role);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 更新特定字段
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        try {
            UserDTO updatedUser = userService.updateStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("状态更新成功", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserRole(
            @PathVariable Integer id,
            @RequestParam Integer role) {
        try {
            UserDTO updatedUser = userService.updateRole(id, role);
            return ResponseEntity.ok(ApiResponse.success("角色更新成功", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/avatar")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserAvatar(
            @PathVariable Integer id,
            @RequestParam String avatarUrl) {
        try {
            UserDTO updatedUser = userService.updateAvatar(id, avatarUrl);
            return ResponseEntity.ok(ApiResponse.success("头像更新成功", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/list")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<User> userPage;
        if (query != null && !query.trim().isEmpty()) {
            userPage = userService.searchUsers(query, pageable);
        } else {
            userPage = userService.findAllUsers(pageable);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", userPage.getContent().stream().map(UserDTO::fromEntity).toList());
        data.put("total", userPage.getTotalElements());
        
        return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", data));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserPassword(
            @PathVariable Integer id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            UserDTO updatedUser = userService.updatePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.success("密码更新成功", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 认证相关
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<Boolean>> authenticate(
            @RequestParam String userAccount,
            @RequestParam String password) {
        boolean authenticated = userService.authenticate(userAccount, password);
        return ResponseEntity.ok(ApiResponse.success(authenticated));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 批量操作
    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatusBatch(
            @RequestParam Integer status,
            @RequestBody List<Integer> userIds) {
        try {
            userService.updateUserStatusBatch(userIds, status);
            return ResponseEntity.ok(ApiResponse.success("批量状态更新成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRoleBatch(
            @RequestParam Integer role,
            @RequestBody List<Integer> userIds) {
        try {
            userService.updateUserRoleBatch(userIds, role);
            return ResponseEntity.ok(ApiResponse.success("批量角色更新成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
