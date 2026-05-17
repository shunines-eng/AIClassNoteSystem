package com.noteaiBackend.repository;

import com.noteaiBackend.dto.NoteFindByTaskIdWithCommentDTO;
import com.noteaiBackend.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    @Query(value = """
            SELECT u.username, n.user_id as userId, n.id as noteId, n.is_score as isScore, nc.score, nc.comment
            FROM note n
                  LEFT JOIN user u ON n.user_id = u.id
                  LEFT JOIN note_comment nc ON n.id = nc.note_id
            WHERE n.task_id = :taskId;""",nativeQuery = true)
    List<NoteFindByTaskIdWithCommentDTO> findByTaskIdWithComment(@Param("taskId") Integer taskId);

    @SuppressWarnings("SqlCaseVsIf")
    @Query(value = """
            SELECT
                n.id,
                n.title,
                n.content,
                n.summary,
                n.user_id as userId,
                u.username as authorName,
                u.avatar as authorAvatar,
                n.task_id as taskId,
                t.class_id as courseId,
                c.class_name as courseName,
                n.vision,
                n.create_time as createTime,
                n.update_time as updateTime,
                n.status,
                n.tag,
                n.is_score as isScore,
                COALESCE(ni.like_count, 0) as likes,
                COALESCE(ni.view_count, 0) as views,
                COALESCE(nc.comment_count, 0) as comments
            FROM note n
            LEFT JOIN user u ON n.user_id = u.id
            LEFT JOIN task t ON n.task_id = t.id
            LEFT JOIN class c ON t.class_id = c.id
            LEFT JOIN (
                SELECT note_id,
                       SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) as like_count,
                       COUNT(*) as view_count
                FROM note_interaction
                GROUP BY note_id
            ) ni ON n.id = ni.note_id
            LEFT JOIN (
                SELECT note_id, COUNT(*) as comment_count
                FROM note_comment
                GROUP BY note_id
            ) nc ON n.id = nc.note_id
            WHERE n.vision = 2  -- 2表示开放笔记
            ORDER BY n.create_time DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Object[]> findPublicNotes(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = """
            SELECT COUNT(*)
            FROM note n
            WHERE n.vision = 2
            """, nativeQuery = true)
    long countPublicNotes();

    @Query(value = """
            SELECT 
                t.title,
                COUNT(n.id) as submission_count,
                (SELECT COUNT(*) FROM class_joined cj WHERE cj.class_id = t.class_id) as total_students,
                MAX(nc.score) as max_score,
                MIN(nc.score) as min_score,
                AVG(nc.score) as avg_score
            FROM task t
            LEFT JOIN note n ON t.id = n.task_id
            LEFT JOIN note_comment nc ON n.id = nc.note_id
            WHERE t.class_id = :classId
            GROUP BY t.id, t.title
            """, nativeQuery = true)
    List<Object[]> findTaskStatsByClassId(@Param("classId") Long classId);

    @Query(value = """
            SELECT n.title, nc.score, u.username
            FROM note n
            JOIN note_comment nc ON n.id = nc.note_id
            JOIN user u ON n.user_id = u.id
            JOIN task t ON n.task_id = t.id
            WHERE t.class_id = :classId
            ORDER BY nc.score DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findTop5NotesByClassId(@Param("classId") Long classId);

    @Query(value = "SELECT score FROM note_comment nc JOIN note n ON nc.note_id = n.id JOIN task t ON n.task_id = t.id WHERE t.class_id = :classId", nativeQuery = true)
    List<Double> findScoresByClassId(@Param("classId") Long classId);

    @Query(value = "SELECT n.title FROM note n JOIN task t ON n.task_id = t.id WHERE t.class_id = :classId", nativeQuery = true)
    List<String> findNoteTitlesByClassId(@Param("classId") Long classId);

    @Query(value = "SELECT class_name FROM class WHERE id = :classId", nativeQuery = true)
    String findClassNameByClassId(@Param("classId") Long classId);

    @Query(value = "SELECT t.name FROM tag t JOIN note_tag nt ON t.id = nt.tag_id WHERE nt.note_id = :noteId", nativeQuery = true)
    List<String> findTagNamesByNoteId(@Param("noteId") Integer noteId);

    @Query(value = "SELECT t.* FROM tag t JOIN note_tag nt ON t.id = nt.tag_id WHERE nt.note_id = :noteId", nativeQuery = true)
    List<Object[]> findTagsByNoteId(@Param("noteId") Integer noteId);

    @Query(value = """
        SELECT
            COUNT(DISTINCT n.user_id) as active_students,
            COUNT(n.id) as total_submissions,
            AVG(nc.score) as average_score
        FROM note n
        LEFT JOIN note_comment nc ON n.id = nc.note_id
        JOIN task t ON n.task_id = t.id
        WHERE t.class_id = :classId
""", nativeQuery = true)
    List<Object[]> findCourseStats(@Param("classId") Integer classId);

//    热门公开笔记
//    @SuppressWarnings("SqlCaseVsIf")
//    @Query(value = """
//            SELECT
//                n.id,
//                n.title,
//                n.content,
//                n.summary,
//                n.user_id as userId,
//                u.username as authorName,
//                u.avatar as authorAvatar,
//                n.task_id as taskId,
//                t.class_id as courseId,
//                c.class_name as courseName,
//                n.vision,
//                n.create_time as createTime,
//                n.update_time as updateTime,
//                n.status,
//                n.tag,
//                n.is_score as isScore,
//                COALESCE(ni.like_count, 0) as likes,
//                COALESCE(ni.view_count, 0) as views,
//                COALESCE(nc.comment_count, 0) as comments
//            FROM note n
//            LEFT JOIN user u ON n.user_id = u.id
//            LEFT JOIN task t ON n.task_id = t.id
//            LEFT JOIN class c ON t.class_id = c.id
//            LEFT JOIN (
//                SELECT note_id,
//                       SUM(CASE WHEN type = 2 THEN 1 ELSE 0 END) as like_count,
//                       SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) as view_count
//                FROM note_interaction
//                GROUP BY note_id
//            ) ni ON n.id = ni.note_id
//            LEFT JOIN (
//                SELECT note_id, COUNT(*) as comment_count
//                FROM note_comment
//                GROUP BY note_id
//            ) nc ON n.id = nc.note_id
//            WHERE n.vision = 2 AND n.status = 0
//            ORDER BY likes DESC, n.create_time DESC
//            LIMIT :limit
//            """, nativeQuery = true)
//    List<Object[]> findHotNotes(@Param("limit") int limit);
    
    @SuppressWarnings("SqlCaseVsIf")
    @Query(value = """
            SELECT
                n.id,
                n.title,
                n.content,
                n.summary,
                n.user_id as userId,
                u.username as authorName,
                u.avatar as authorAvatar,
                n.task_id as taskId,
                t.class_id as courseId,
                c.class_name as courseName,
                n.vision,
                n.create_time as createTime,
                n.update_time as updateTime,
                n.status,
                n.tag,
                n.is_score as isScore,
                COALESCE(ni.like_count, 0) as likes,
                COALESCE(ni.view_count, 0) as views,
                COALESCE(nc.comment_count, 0) as comments
            FROM note n
            LEFT JOIN user u ON n.user_id = u.id
            LEFT JOIN task t ON n.task_id = t.id
            LEFT JOIN class c ON t.class_id = c.id
            LEFT JOIN (
                SELECT note_id,
                       SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) as like_count,
                       COUNT(*) as view_count
                FROM note_interaction
                GROUP BY note_id
            ) ni ON n.id = ni.note_id
            LEFT JOIN (
                SELECT note_id, COUNT(*) as comment_count
                FROM note_comment
                GROUP BY note_id
            ) nc ON n.id = nc.note_id
            WHERE n.user_id = :userId
            ORDER BY n.create_time DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Object[]> findNotesByUserId(@Param("userId") Integer userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = """
            SELECT COUNT(*)
            FROM note n
            WHERE n.user_id = :userId
            """, nativeQuery = true)
    long countNotesByUserId(@Param("userId") Integer userId);

    @Query(value = """
            SELECT
                n.id,
                n.title,
                n.content,
                n.summary,
                n.user_id as userId,
                u.username as authorName,
                u.avatar as authorAvatar,
                n.task_id as taskId,
                t.class_id as courseId,
                c.class_name as courseName,
                n.vision,
                n.create_time as createTime,
                n.update_time as updateTime,
                n.status,
                n.tag,
                n.is_score as isScore,
                COALESCE(ni.like_count, 0) as likes,
                COALESCE(ni.view_count, 0) as views,
                COALESCE(nc.comment_count, 0) as comments
            FROM note n
            LEFT JOIN user u ON n.user_id = u.id
            LEFT JOIN task t ON n.task_id = t.id
            LEFT JOIN class c ON t.class_id = c.id
            LEFT JOIN (
                SELECT note_id,
                       SUM(CASE WHEN type = 2 THEN 1 ELSE 0 END) as like_count,
                       SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) as view_count
                FROM note_interaction
                GROUP BY note_id
            ) ni ON n.id = ni.note_id
            LEFT JOIN (
                SELECT note_id, COUNT(*) as comment_count
                FROM note_comment
                GROUP BY note_id
            ) nc ON n.id = nc.note_id
            WHERE n.vision = 2 AND n.status = 0
            ORDER BY likes DESC, n.create_time DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findHotNotes(@Param("limit") int limit);

    @Query(value = """
            SELECT 
                n.id, 
                n.title, 
                u.username as author,
                c.class_name as courseName,
                n.vision,
                (SELECT COUNT(*) FROM report r WHERE r.type = 2 AND r.type_id = n.id) as reportCount,
                n.content
            FROM note n
            LEFT JOIN user u ON n.user_id = u.id
            LEFT JOIN task t ON n.task_id = t.id
            LEFT JOIN class c ON t.class_id = c.id
            WHERE (:query IS NULL OR n.title LIKE CONCAT('%', :query, '%') OR u.username LIKE CONCAT('%', :query, '%'))
            """, 
            countQuery = """
            SELECT COUNT(*) FROM note n
            LEFT JOIN user u ON n.user_id = u.id
            WHERE (:query IS NULL OR n.title LIKE CONCAT('%', :query, '%') OR u.username LIKE CONCAT('%', :query, '%'))
            """,
            nativeQuery = true)
    Page<Object[]> findAdminNotes(@Param("query") String query, Pageable pageable);

    @SuppressWarnings("SqlCaseVsIf")
    @Query(value = """
            SELECT
                n.id,
                n.title,
                n.content,
                n.summary,
                n.user_id as userId,
                u.username as authorName,
                u.avatar as authorAvatar,
                n.task_id as taskId,
                t.class_id as courseId,
                c.class_name as courseName,
                n.vision,
                n.create_time as createTime,
                n.update_time as updateTime,
                n.status,
                n.tag,
                n.is_score as isScore,
                COALESCE(ni.like_count, 0) as likes,
                COALESCE(ni.view_count, 0) as views,
                COALESCE(nc.comment_count, 0) as comments
            FROM note n
            LEFT JOIN user u ON n.user_id = u.id
            LEFT JOIN task t ON n.task_id = t.id
            LEFT JOIN class c ON t.class_id = c.id
            LEFT JOIN (
                SELECT note_id,
                       SUM(CASE WHEN type = 2 THEN 1 ELSE 0 END) as like_count,
                       SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) as view_count
                FROM note_interaction
                GROUP BY note_id
            ) ni ON n.id = ni.note_id
            LEFT JOIN (
                SELECT note_id, COUNT(*) as comment_count
                FROM note_comment
                GROUP BY note_id
            ) nc ON n.id = nc.note_id
            WHERE n.vision = 1
            AND c.id = :classId
            ORDER BY COALESCE(ni.like_count, 0) DESC, n.create_time DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findExcellentNotesByClassId(@Param("classId") Integer classId);
}
