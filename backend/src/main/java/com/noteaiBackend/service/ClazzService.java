package com.noteaiBackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.noteaiBackend.dto.ClassWithStudentCountByTeacherIdDTO;
import com.noteaiBackend.entity.Clazz;
import com.noteaiBackend.repository.ClazzRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClazzService {
    private final ClazzRepository repository;

    public List<Clazz> findAll() {
        return repository.findAll();
    }

    public Optional<Clazz> findById(Integer id) {
        return repository.findById(id);
    }

    public Clazz save(Clazz entity) {
        return repository.save(entity);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public List<ClassWithStudentCountByTeacherIdDTO> findByIdWithStudentCountByTeacherId(Integer id) {
        return repository.findClassesByTeacherIdWithStudentCount(Long.valueOf(id));
    }

    public List<Object[]> findAllWithTeacherNameAndStudentCount() {
        return repository.findAllWithTeacherNameAndStudentCount();
    }

    /**
     * 更新班级状态（屏蔽/取消屏蔽）
     * @param id 班级ID
     * @param status 状态值
     * @return 更新后的班级
     */
    public Clazz updateStatus(Integer id, Integer status) {
        Clazz existingClass = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("班级不存在"));
        
        existingClass.setStatus(status);
        return repository.save(existingClass);
    }

    /**
     * 根据教师ID获取班级列表
     * @param teacherId 教师ID
     * @return 班级列表
     */
    public List<Clazz> findByTeacherId(Integer teacherId) {
        return repository.findByTeacherId(teacherId);
    }
}
