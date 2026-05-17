package com.noteaiBackend.service;

import com.noteaiBackend.entity.NoteInteraction;
import com.noteaiBackend.repository.NoteInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteInteractionService {
    private final NoteInteractionRepository noteInteractionRepository;

    /**
     * 学生查看笔记
     * 如果没有记录，创建一条type=0的记录
     * 如果有记录，不修改（因为已经看过了）
     */
    @Transactional
    public NoteInteraction viewNote(Integer noteId, Integer userId) {
        return noteInteractionRepository.findByNoteIdAndUserId(noteId, userId)
                .orElseGet(() -> {
                    // 没有记录，创建新记录，type=0（看过但没点赞）
                    NoteInteraction interaction = new NoteInteraction();
                    interaction.setNoteId(noteId);
                    interaction.setUserId(userId);
                    interaction.setType(0);
                    interaction.setCreateTime(LocalDateTime.now());
                    return noteInteractionRepository.save(interaction);
                });
    }

    /**
     * 学生点赞笔记
     * 如果没有记录，创建一条type=1的记录（点赞即看过）
     * 如果有记录且type=0，改为type=1
     * 如果有记录且type=1，不变（已经点过赞了）
     */
    @Transactional
    public NoteInteraction likeNote(Integer noteId, Integer userId) {
        Optional<NoteInteraction> existing = noteInteractionRepository
                .findByNoteIdAndUserId(noteId, userId);

        if (existing.isPresent()) {
            NoteInteraction interaction = existing.get();
            if (interaction.getType() == 0) {
                // 从看过改为点赞
                interaction.setType(1);
                interaction.setCreateTime(LocalDateTime.now());
                return noteInteractionRepository.save(interaction);
            }
            // 已经是点赞状态，不修改
            return interaction;
        } else {
            // 没有记录，创建新记录，type=1（点赞即看过）
            NoteInteraction interaction = new NoteInteraction();
            interaction.setNoteId(noteId);
            interaction.setUserId(userId);
            interaction.setType(1);
            interaction.setCreateTime(LocalDateTime.now());
            return noteInteractionRepository.save(interaction);
        }
    }

    /**
     * 学生取消点赞
     * 如果有记录且type=1，改为type=0
     * 如果有记录且type=0，不变
     * 没有记录，抛出异常
     */
    @Transactional
    public NoteInteraction cancelLike(Integer noteId, Integer userId) {
        return noteInteractionRepository.findByNoteIdAndUserId(noteId, userId)
                .map(interaction -> {
                    if (interaction.getType() == 1) {
                        // 从点赞改为仅看过
                        interaction.setType(0);
                        interaction.setCreateTime(LocalDateTime.now());
                        return noteInteractionRepository.save(interaction);
                    }
                    return interaction;
                })
                .orElseThrow(() -> new RuntimeException("记录不存在"));
    }

    /**
     * 获取笔记的查看学生数
     * 就是统计该笔记有多少条记录
     */
    public Long getViewCount(Integer noteId) {
        return noteInteractionRepository.countByNoteId(noteId);
    }

    /**
     * 获取笔记的点赞学生数
     * 统计该笔记type=1的记录数
     */
    public Long getLikeCount(Integer noteId) {
        return noteInteractionRepository.countByNoteIdAndType(noteId, 1);
    }

    /**
     * 检查学生是否看过笔记
     * 检查是否存在记录
     */
    public boolean hasViewed(Integer noteId, Integer userId) {
        return noteInteractionRepository.existsByNoteIdAndUserId(noteId, userId);
    }

    /**
     * 检查学生是否点赞笔记
     * 检查是否存在记录且type=1
     */
    public boolean hasLiked(Integer noteId, Integer userId) {
        return noteInteractionRepository.existsByNoteIdAndUserIdAndType(noteId, userId, 1);
    }

    /**
     * 获取学生的互动状态
     */
    public Map<String, Object> getInteractionStatus(Integer noteId, Integer userId) {
        Map<String, Object> status = new java.util.HashMap<>();

        Optional<NoteInteraction> interaction = noteInteractionRepository
                .findByNoteIdAndUserId(noteId, userId);

        if (interaction.isPresent()) {
            status.put("hasViewed", true);
            status.put("hasLiked", interaction.get().getType() == 1);
            status.put("type", interaction.get().getType());
            status.put("createTime", interaction.get().getCreateTime());
        } else {
            status.put("hasViewed", false);
            status.put("hasLiked", false);
            status.put("type", -1);  // 表示无记录
        }

        return status;
    }

    /**
     * 切换点赞状态
     * 如果没看过，先创建记录再点赞
     * 如果看过没点赞，改为点赞
     * 如果已点赞，取消点赞
     */
    @Transactional
    public NoteInteraction toggleLike(Integer noteId, Integer userId) {
        Optional<NoteInteraction> existing = noteInteractionRepository
                .findByNoteIdAndUserId(noteId, userId);

        if (existing.isPresent()) {
            NoteInteraction interaction = existing.get();
            if (interaction.getType() == 0) {
                // 从看过改为点赞
                interaction.setType(1);
            } else {
                // 从点赞改为看过
                interaction.setType(0);
            }
            interaction.setCreateTime(LocalDateTime.now());
            return noteInteractionRepository.save(interaction);
        } else {
            // 没有记录，创建点赞记录
            NoteInteraction interaction = new NoteInteraction();
            interaction.setNoteId(noteId);
            interaction.setUserId(userId);
            interaction.setType(1);  // 点赞
            interaction.setCreateTime(LocalDateTime.now());
            return noteInteractionRepository.save(interaction);
        }
    }
}