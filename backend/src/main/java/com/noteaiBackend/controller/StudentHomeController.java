package com.noteaiBackend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.service.StudentHomeService;

import lombok.RequiredArgsConstructor;

/**
 * 学生首页控制器
 * 处理学生首页的数据展示接口
 * 
 * 输入/输出说明：
 * - GET /api/student/home/data?userId=1
 *   输入：userId (请求参数，学生用户ID)
 *   输出：{ code, data: { hotNotes: [...], pendingAssignments: [...] } }
 *   功能：获取学生首页数据，包括热门笔记和待完成作业列表
 *         - hotNotes: 平台热门笔记列表（最多5条）
 *         - pendingAssignments: 该学生尚未提交的作业列表
 */
@RestController
@RequestMapping("/api/student/home")
@RequiredArgsConstructor
public class StudentHomeController {

    private final StudentHomeService studentHomeService;

    @GetMapping("/data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHomeData(@RequestParam Integer userId) {
        Map<String, Object> data = studentHomeService.getHomeData(userId);
        return ResponseEntity.ok(ApiResponse.success("获取首页数据成功", data));
    }
}
