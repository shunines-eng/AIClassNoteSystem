package com.noteaiBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.noteaiBackend.dto.ClassWithStudentCountByTeacherIdDTO;
import com.noteaiBackend.entity.Clazz;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz, Integer> {

    @Query(value = "SELECT " +
            "c.id as id, " +
            "c.class_name as className, " +
            "c.teacher_id as teacherId, " +
            "c.create_time as createTime, " +
            "(SELECT COUNT(*) FROM class_joined WHERE class_id = c.id) as studentCount " +
            "FROM class c " +
            "WHERE c.teacher_id = ?1",
            nativeQuery = true)
    List<ClassWithStudentCountByTeacherIdDTO> findClassesByTeacherIdWithStudentCount(Long teacherId);

    @Query(value = """
            SELECT 
                c.id, 
                c.class_name, 
                u.username,
                c.status,
                (SELECT COUNT(*) FROM class_joined WHERE class_id = c.id) as student_count
            FROM class c
            LEFT JOIN user u ON c.teacher_id = u.id
            """, nativeQuery = true)
    List<Object[]> findAllWithTeacherNameAndStudentCount();
    
    List<Clazz> findByTeacherId(Integer teacherId);
}
