package com.noteaiBackend.repository;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.entity.NoteComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteCommentRepository extends JpaRepository<NoteComment, Integer> {
    List<NoteComment> findByTeacherId(Integer teacherId);

    List<NoteComment> findByNoteId(Integer noteId);

    Optional<NoteComment> findByTeacherIdAndNoteId(Integer teacherId, Integer noteId);

    List<NoteComment> findByScoreGreaterThanEqual(Integer score);

    @Query("SELECT nc FROM NoteComment nc WHERE nc.teacherId = :teacherId AND nc.noteId = :noteId")
    Optional<NoteComment> findByTeacherAndNote(@Param("teacherId") Integer teacherId, @Param("noteId") Integer noteId);

    @Query("SELECT nc FROM NoteComment nc WHERE nc.teacherId = :teacherId ORDER BY nc.updateTime DESC")
    List<NoteComment> findByTeacherIdOrderByUpdateTimeDesc(@Param("teacherId") Integer teacherId);

    @Query("SELECT AVG(nc.score) FROM NoteComment nc WHERE nc.teacherId = :teacherId")
    Double findAverageScoreByTeacherId(@Param("teacherId") Integer teacherId);

    @Query("SELECT AVG(nc.score) FROM NoteComment nc WHERE nc.noteId = :noteId")
    Double findAverageScoreByNoteId(@Param("noteId") Integer noteId);

    boolean existsByTeacherIdAndNoteId(Integer teacherId, Integer noteId);

    @Modifying
    @Transactional(readOnly = false)  // 明确设置为非只读
    @Query(value = """
    INSERT INTO note_comment (teacher_id, note_id, comment, score, update_time)
    VALUES (:teacherId, :noteId, :comment, :score, :updateTime)
    ON DUPLICATE KEY UPDATE
        teacher_id = VALUES(teacher_id),
        comment = VALUES(comment),
        score = VALUES(score),
        update_time = VALUES(update_time)
    """, nativeQuery = true)
    void updateByNoteId(@Param("teacherId")Integer teacherId,@Param("noteId")Integer noteId,@Param("comment")String comment,@Param("score")Integer score,@Param("updateTime") LocalDateTime updateTime);
}