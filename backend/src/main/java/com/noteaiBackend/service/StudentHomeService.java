package com.noteaiBackend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.noteaiBackend.repository.NoteRepository;
import com.noteaiBackend.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

/**
 * 学生首页服务层
 * 负责学生首页的数据组装业务逻辑
 */
@Service
@RequiredArgsConstructor
public class StudentHomeService {

    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    /**
     * 获取学生首页数据
     *
     * @param userId 学生用户ID
     * @return 包含热门笔记和待完成作业的Map
     */
    public Map<String, Object> getHomeData(Integer userId) {
        Map<String, Object> data = new HashMap<>();

        // 1. 获取热门笔记
        List<Object[]> hotNotesRaw = noteRepository.findHotNotes(5);
        List<Map<String, Object>> hotNotes = new ArrayList<>();
        for (Object[] row : hotNotesRaw) {
            Map<String, Object> note = new HashMap<>();
            note.put("id", row[0]);
            note.put("title", row[1]);
            note.put("summary", row[3]);
            note.put("authorName", row[5]);
            note.put("likes", row[16]);
            hotNotes.add(note);
        }
        data.put("hotNotes", hotNotes);

        // 2. 获取待完成作业
        List<Object[]> pendingRaw = taskRepository.findPendingAssignmentsByStudentId(userId);
        List<Map<String, Object>> pendingAssignments = new ArrayList<>();
        for (Object[] row : pendingRaw) {
            Map<String, Object> task = new HashMap<>();
            task.put("id", row[0]);
            task.put("classId", row[1]);
            task.put("title", row[2]);
            task.put("deadline", row[4]);
            task.put("courseName", row[8]);
            pendingAssignments.add(task);
        }
        data.put("pendingAssignments", pendingAssignments);

        return data;
    }
}
