package com.noteaiBackend.controller;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.dto.ClassWithStudentCountByTeacherIdDTO;
import com.noteaiBackend.entity.Clazz;
import com.noteaiBackend.service.ClazzService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClazzController {
    private final ClazzService service;

    @GetMapping("/admin/list")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAdminClassList() {
        List<Object[]> raw = service.findAllWithTeacherNameAndStudentCount();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("className", row[1]);
            item.put("teacherName", row[2]);
            item.put("status", row[3]);
            item.put("studentCount", row[4]);
            result.add(item);
        }
        return ResponseEntity.ok(ApiResponse.success("获取全平台班级成功", result));
    }

//    查询所有课程（CRUD）
    @GetMapping
    public List<Clazz> getAll() {
        return service.findAll();
    }

    //    查id的课程（CRUD）
    @GetMapping("/{id}")
    public ResponseEntity<Clazz> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//   新建课程（CRUD）
//    教师新建课程
    @PostMapping
    public ResponseEntity<ApiResponse<Clazz>> create(@RequestBody Clazz entity) {
        Clazz result =  service.save(entity);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

//    编辑id为1课程
    @PutMapping("/{id}")
    public ResponseEntity<Clazz> update(@PathVariable Integer id, @RequestBody Clazz entity) {
//        findById，如果有，输出optional类，否则提示找不到，
        return service.findById(id)
                .map(existing -> {
                    entity.setId(id);
                    return ResponseEntity.ok(service.save(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

//    删除id为1的课程
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
//  （api/teacher/courses的表单）查教师的课程
    @GetMapping("/withStudentCount/{id}")
    public ResponseEntity<ApiResponse<List<ClassWithStudentCountByTeacherIdDTO>>> getWithStudentCount(@PathVariable Integer id) {
        List<ClassWithStudentCountByTeacherIdDTO> result = service.findByIdWithStudentCountByTeacherId(id);

        // 如果查询结果列表为空，返回 404 Not Found
        if (result == null || result.isEmpty()) {
            // 返回空的ApiResponse，而不是noContent()
            return ResponseEntity.ok(ApiResponse.success("没有找到相关班级", new ArrayList<>()));
        }

        // 返回完整的ApiResponse对象
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    // 更新班级状态（屏蔽/取消屏蔽）
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Clazz>> updateClassStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        try {
            Clazz updatedClass = service.updateStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("班级状态更新成功", updatedClass));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 根据教师ID获取班级列表
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<Clazz>>> getClassesByTeacherId(@PathVariable Integer teacherId) {
        try {
            List<Clazz> classes = service.findByTeacherId(teacherId);
            return ResponseEntity.ok(ApiResponse.success("获取教师班级成功", classes));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
