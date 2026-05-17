package com.noteaiBackend.repository;

import com.noteaiBackend.entity.NoteVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteVersionRepository extends JpaRepository<NoteVersion, Integer> {
    
    /**
     * 根据笔记ID查找所有版本，按版本号降序排列
     */
    List<NoteVersion> findByNoteIdOrderByVersionDesc(Integer noteId);
    
    /**
     * 根据笔记ID和版本号查找特定版本
     */
    Optional<NoteVersion> findByNoteIdAndVersion(Integer noteId, Integer version);
    
    /**
     * 获取笔记的最新版本号
     */
    @Query("SELECT MAX(nv.version) FROM NoteVersion nv WHERE nv.noteId = :noteId")
    Integer findLatestVersionByNoteId(@Param("noteId") Integer noteId);
    
    /**
     * 根据笔记ID获取版本数量
     */
    Long countByNoteId(Integer noteId);
    
    /**
     * 根据笔记ID查找所有版本，按创建时间降序排列
     */
    List<NoteVersion> findByNoteIdOrderByCreatedAtDesc(Integer noteId);
    
    /**
     * 根据笔记ID删除所有版本
     */
    void deleteByNoteId(Integer noteId);
}
