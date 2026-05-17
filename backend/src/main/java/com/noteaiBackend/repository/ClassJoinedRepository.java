package com.noteaiBackend.repository;

import com.noteaiBackend.dto.ClassJoinedByStudentIdDTO;
import com.noteaiBackend.entity.ClassJoined;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassJoinedRepository extends JpaRepository<ClassJoined, Integer> {
    @Query(value = """
        select u.username as teacher_name,c.class_name,c.id  as class_id,c.describe as description
        from class_joined cj
        left join class c on c.id = cj.class_id
        left join user u on u.id = c.teacher_id
        where student_id =:id
""", nativeQuery = true)
    List<ClassJoinedByStudentIdDTO> findByStudentId(@Param("id")Integer id);

    @Query(value = """
        SELECT 
            u.id, u.username, u.real_name, u.phone, u.avatar, cj.join_time,
            (SELECT COUNT(*) FROM task t 
             WHERE t.class_id = cj.class_id 
             AND NOT EXISTS (SELECT 1 FROM note n WHERE n.task_id = t.id AND n.user_id = u.id)) as unfinished_count
        FROM class_joined cj
        JOIN user u ON cj.student_id = u.id
        WHERE cj.class_id = :classId
""", nativeQuery = true)
    List<Object[]> findStudentsByClassIdWithUnfinishedCount(@Param("classId") Integer classId);

    @Query(value = "SELECT COUNT(*) FROM class_joined WHERE class_id = :classId", nativeQuery = true)
    long countByClassId(@Param("classId") Integer classId);

    @Query(value = """
        SELECT u.id, u.username, u.real_name, u.phone, u.avatar, cj.join_time
        FROM class_joined cj
        JOIN user u ON cj.student_id = u.id
        WHERE cj.class_id = :classId
""", nativeQuery = true)
    List<Object[]> findStudentsByClassId(@Param("classId") Integer classId);
}
