package com.noteaiBackend.service;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.entity.NoteComment;
import com.noteaiBackend.repository.NoteCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteCommentService {
    private final NoteCommentRepository noteCommentRepository;

    public List<NoteComment> findAll() {
        return noteCommentRepository.findAll();
    }

    public Optional<NoteComment> findById(Integer id) {
        return noteCommentRepository.findById(id);
    }

    @Transactional
    public NoteComment save(NoteComment noteComment) {
        if (noteComment.getUpdateTime() == null) {
            noteComment.setUpdateTime(LocalDateTime.now());
        }
        return noteCommentRepository.save(noteComment);
    }

    @Transactional
    public void deleteById(Integer id) {
        noteCommentRepository.deleteById(id);
    }

    public List<NoteComment> findByTeacherId(Integer teacherId) {
        return noteCommentRepository.findByTeacherId(teacherId);
    }

    public List<NoteComment> findByNoteId(Integer noteId) {
        return noteCommentRepository.findByNoteId(noteId);
    }

    public Optional<NoteComment> findByTeacherIdAndNoteId(Integer teacherId, Integer noteId) {
        return noteCommentRepository.findByTeacherIdAndNoteId(teacherId, noteId);
    }

    public List<NoteComment> findByTeacherIdOrderByUpdateTimeDesc(Integer teacherId) {
        return noteCommentRepository.findByTeacherIdOrderByUpdateTimeDesc(teacherId);
    }

    public Double getAverageScoreByTeacherId(Integer teacherId) {
        return noteCommentRepository.findAverageScoreByTeacherId(teacherId);
    }

    public Double getAverageScoreByNoteId(Integer noteId) {
        return noteCommentRepository.findAverageScoreByNoteId(noteId);
    }

    public boolean existsByTeacherIdAndNoteId(Integer teacherId, Integer noteId) {
        return noteCommentRepository.existsByTeacherIdAndNoteId(teacherId, noteId);
    }

    public Page<NoteComment> findAll(Pageable pageable) {
        return noteCommentRepository.findAll(pageable);
    }

    @Transactional
    public NoteComment update(Integer id, NoteComment noteComment) {
        return noteCommentRepository.findById(id)
                .map(existing -> {
                    if (noteComment.getTeacherId() != null) {
                        existing.setTeacherId(noteComment.getTeacherId());
                    }
                    if (noteComment.getNoteId() != null) {
                        existing.setNoteId(noteComment.getNoteId());
                    }
                    if (noteComment.getComment() != null) {
                        existing.setComment(noteComment.getComment());
                    }
                    if (noteComment.getScore() != null) {
                        existing.setScore(noteComment.getScore());
                    }
                    existing.setUpdateTime(LocalDateTime.now());
                    return noteCommentRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("NoteComment not found with id: " + id));
    }

    @Transactional
    public void updateByNoteId(Integer id, NoteComment noteComment) {
//        数据拆分
        Integer teacherId = noteComment.getTeacherId();
        Integer score = noteComment.getScore();
        String comment = noteComment.getComment();
        LocalDateTime updateTime = noteComment.getUpdateTime();
        noteCommentRepository.updateByNoteId(teacherId, id,comment,score,updateTime);
    }
}
