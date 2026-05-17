package com.noteaiBackend.controller;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.dto.ClassJoinedByStudentIdDTO;
import com.noteaiBackend.dto.CreateClassJoinByDoubleIdDTO;
import com.noteaiBackend.entity.ClassJoined;
import com.noteaiBackend.service.ClassJoinedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/class-joined")
@RequiredArgsConstructor
public class ClassJoinedController {
    private final ClassJoinedService service;

    @GetMapping
    public List<ClassJoined> getAll() {
        return service.findAll();
    }

//  不会使用的通过cj_id查课程加入
//  还未给学生设计自主添加笔记页面
    @GetMapping("/{id}")
    public ResponseEntity<ClassJoined> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //    (用于/student/courses,只查学生对应课程的教师和课程名称用于展示,外加一个class_id用于卡片查看课程详情按钮)
    @GetMapping("byStudentId/{id}")
    public ResponseEntity<ApiResponse<List<ClassJoinedByStudentIdDTO>>> getByStudentId(@PathVariable Integer id) {
        try {
            List<ClassJoinedByStudentIdDTO> res = service.findByStudentId(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", res));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));}}


//    通过课程id和学生id新建一个"学生加入课程"
// 用于(http://localhost:5173/student/courses)
    @PostMapping
    public ResponseEntity<ApiResponse<ClassJoined>> create(@RequestBody CreateClassJoinByDoubleIdDTO doubleId) {
        try{
//          不携带就不加入即可
            ClassJoined result = service.save(doubleId);
            return ResponseEntity.ok(ApiResponse.success("新建成功", result));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

//通过id编辑课程加入信息
//    @PutMapping("/{id}")
//    public ResponseEntity<ClassJoined> update(@PathVariable Integer id, @RequestBody ClassJoined entity) {
//        return service.findById(id)
//                .map(existing -> {
//                    entity.setId(id);
//                    return ResponseEntity.ok(service.save(entity));
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
