package com.noteaiBackend.service;

import com.noteaiBackend.dto.CreateNoteVersionDTO;
import com.noteaiBackend.dto.NoteVersionDTO;
import com.noteaiBackend.entity.Note;
import com.noteaiBackend.entity.NoteVersion;
import com.noteaiBackend.repository.NoteRepository;
import com.noteaiBackend.repository.NoteVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteVersionService {
    
    private final NoteVersionRepository noteVersionRepository;
    private final NoteRepository noteRepository;
    
    /**
     * 创建笔记版本
     * 当笔记更新时调用此方法创建新版本
     */
    @Transactional
    public NoteVersion createVersion(CreateNoteVersionDTO createNoteVersionDTO) {
        // 获取当前笔记
        Note note = noteRepository.findById(createNoteVersionDTO.getNoteId())
                .orElseThrow(() -> new RuntimeException("笔记不存在"));
        
        // 获取当前最大版本号
        Integer latestVersion = noteVersionRepository.findLatestVersionByNoteId(createNoteVersionDTO.getNoteId());
        int newVersion = (latestVersion != null) ? latestVersion + 1 : 1;
        
        // 创建版本记录
        NoteVersion noteVersion = NoteVersion.builder()
                .noteId(createNoteVersionDTO.getNoteId())
                .version(newVersion)
                .title(note.getTitle())
                .content(note.getContent())
                .userId(createNoteVersionDTO.getUserId())
                .changeSummary(createNoteVersionDTO.getChangeSummary())
                .createdAt(LocalDateTime.now())
                .build();
        
        NoteVersion savedVersion = noteVersionRepository.save(noteVersion);
        
        // 检查版本数量，超过20个删除旧版本
        cleanupOldVersions(createNoteVersionDTO.getNoteId());
        
        return savedVersion;
    }

    /**
     * 清理旧版本，保留最近的20个
     */
    private void cleanupOldVersions(Integer noteId) {
        List<NoteVersion> versions = noteVersionRepository.findByNoteIdOrderByVersionDesc(noteId);
        if (versions.size() > 20) {
            List<NoteVersion> toDelete = versions.subList(20, versions.size());
            noteVersionRepository.deleteAll(toDelete);
        }
    }
    
    /**
     * 获取笔记的所有版本
     */
    public List<NoteVersionDTO> getVersionsByNoteId(Integer noteId) {
        List<NoteVersion> versions = noteVersionRepository.findByNoteIdOrderByVersionDesc(noteId);
        
        return versions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 获取特定版本
     */
    public NoteVersionDTO getVersion(Integer noteId, Integer version) {
        NoteVersion noteVersion = noteVersionRepository.findByNoteIdAndVersion(noteId, version)
                .orElseThrow(() -> new RuntimeException("版本不存在"));
        
        return convertToDTO(noteVersion);
    }
    
    /**
     * 获取最新版本
     */
    public NoteVersionDTO getLatestVersion(Integer noteId) {
        Integer latestVersion = noteVersionRepository.findLatestVersionByNoteId(noteId);
        if (latestVersion == null) {
            throw new RuntimeException("该笔记没有版本记录");
        }
        
        NoteVersion noteVersion = noteVersionRepository.findByNoteIdAndVersion(noteId, latestVersion)
                .orElseThrow(() -> new RuntimeException("版本不存在"));
        
        return convertToDTO(noteVersion);
    }
    
    /**
     * 版本回退 - 将笔记恢复到指定版本
     */
    @Transactional
    public Note revertToVersion(Integer noteId, Integer version, Integer userId) {
        // 获取目标版本
        NoteVersion targetVersion = noteVersionRepository.findByNoteIdAndVersion(noteId, version)
                .orElseThrow(() -> new RuntimeException("目标版本不存在"));
        
        // 获取当前笔记
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("笔记不存在"));
        
        // 保存当前状态为新版本（作为备份）
        CreateNoteVersionDTO backupDTO = CreateNoteVersionDTO.builder()
                .noteId(noteId)
                .userId(userId)
                .changeSummary("版本回退前备份")
                .build();
        createVersion(backupDTO);
        
        // 恢复笔记内容
        note.setTitle(targetVersion.getTitle());
        note.setContent(targetVersion.getContent());
        note.setUpdateTime(LocalDateTime.now());
        
        // 保存恢复后的笔记
        Note revertedNote = noteRepository.save(note);
        
        // 创建回退版本记录
        NoteVersion revertVersion = NoteVersion.builder()
                .noteId(noteId)
                .version(noteVersionRepository.findLatestVersionByNoteId(noteId) + 1)
                .title(targetVersion.getTitle())
                .content(targetVersion.getContent())
                .userId(userId)
                .changeSummary("回退到版本 " + version)
                .createdAt(LocalDateTime.now())
                .build();
        noteVersionRepository.save(revertVersion);
        
        return revertedNote;
    }
    
    /**
     * 比较两个版本的内容差异
     */
    public String compareVersions(Integer noteId, Integer version1, Integer version2) {
        NoteVersion v1 = noteVersionRepository.findByNoteIdAndVersion(noteId, version1)
                .orElseThrow(() -> new RuntimeException("版本 " + version1 + " 不存在"));
        
        NoteVersion v2 = noteVersionRepository.findByNoteIdAndVersion(noteId, version2)
                .orElseThrow(() -> new RuntimeException("版本 " + version2 + " 不存在"));
        
        // 简单的文本比较（可以扩展为更复杂的差异算法）
        if (v1.getContent().equals(v2.getContent())) {
            return "内容相同";
        }
        
        // 计算基本差异
        String content1 = v1.getContent();
        String content2 = v2.getContent();
        
        // 简单的行级比较
        String[] lines1 = content1.split("\n");
        String[] lines2 = content2.split("\n");
        
        StringBuilder diff = new StringBuilder();
        diff.append("版本 ").append(version1).append(" 与版本 ").append(version2).append(" 比较：\n");
        diff.append("标题变化: ").append(v1.getTitle().equals(v2.getTitle()) ? "相同" : "不同").append("\n");
        diff.append("内容行数变化: ").append(lines1.length).append(" 行 → ").append(lines2.length).append(" 行\n");
        
        // 简单的字符数比较
        diff.append("字符数变化: ").append(content1.length()).append(" 字符 → ").append(content2.length()).append(" 字符\n");
        
        return diff.toString();
    }
    
    /**
     * 删除笔记的所有版本
     */
    @Transactional
    public void deleteVersionsByNoteId(Integer noteId) {
        noteVersionRepository.deleteByNoteId(noteId);
    }
    
    /**
     * 删除特定版本
     */
    @Transactional
    public void deleteVersion(Integer noteId, Integer version) {
        NoteVersion noteVersion = noteVersionRepository.findByNoteIdAndVersion(noteId, version)
                .orElseThrow(() -> new RuntimeException("版本不存在"));
        noteVersionRepository.delete(noteVersion);
    }
    
    /**
     * 将实体转换为DTO
     */
    private NoteVersionDTO convertToDTO(NoteVersion noteVersion) {
        return NoteVersionDTO.builder()
                .id(noteVersion.getId())
                .noteId(noteVersion.getNoteId())
                .version(noteVersion.getVersion())
                .title(noteVersion.getTitle())
                .content(noteVersion.getContent())
                .userId(noteVersion.getUserId())
                .changeSummary(noteVersion.getChangeSummary())
                .createdAt(noteVersion.getCreatedAt())
                .build();
    }
}
