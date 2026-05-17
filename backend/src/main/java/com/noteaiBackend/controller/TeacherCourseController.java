package com.noteaiBackend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.service.TeacherCourseService;

import lombok.RequiredArgsConstructor;

/**
 * 教师课程管理控制器
 * 处理教师端课程详情、学生提交状态等接口
 * 
 * 输入/输出说明：
 * - GET /api/teacher/courses/{classId}/details
 *   输入：classId (路径变量，课程ID)
 *   输出：{ code, data: { stats: { totalStudents, activeStudents, ... }, students: [...] } }
 *   功能：教师查看某课程的详细统计信息（含学生列表和完成情况）
 * 
 * - GET /api/teacher/courses/tasks/{taskId}/submissions-status
 *   输入：taskId (路径变量，作业ID)
 *   输出：{ code, data: [ { userId, userName, noteId, submitted, isScore, score, comment }, ... ] }
 *   功能：教师查看某作业的全班学生提交状态（谁交了、谁没交、得分等）
 */
@RestController
@RequestMapping("/api/teacher/courses")
@RequiredArgsConstructor
public class TeacherCourseController {

    private final TeacherCourseService teacherCourseService;

    @GetMapping("/{classId}/details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseDetails(@PathVariable Integer classId) {
        Map<String, Object> data = teacherCourseService.getCourseDetails(classId);
        return ResponseEntity.ok(ApiResponse.success("获取详情成功", data));
    }

    @GetMapping("/tasks/{taskId}/submissions-status")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSubmissionStatus(@PathVariable Integer taskId) {
        List<Map<String, Object>> result = teacherCourseService.getSubmissionStatus(taskId);
        return ResponseEntity.ok(ApiResponse.success("获取提交状态成功", result));
    }
}
