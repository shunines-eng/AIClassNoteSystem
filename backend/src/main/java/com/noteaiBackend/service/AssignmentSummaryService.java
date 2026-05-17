package com.noteaiBackend.service;

import com.noteaiBackend.entity.AssignmentSummary;
import com.noteaiBackend.repository.AssignmentSummaryRepository;
import com.noteaiBackend.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentSummaryService {

    private final AssignmentSummaryRepository assignmentSummaryRepository;
    private final NoteRepository noteRepository;
    private final LLMService llmService;

    public Optional<AssignmentSummary> getSummaryByClassId(Long classId) {
        return assignmentSummaryRepository.findByClassId(classId);
    }

    public AssignmentSummary generateAndSaveSummary(Long classId) throws IOException {
        // 1. 获取统计数据
        List<Double> allScores = noteRepository.findScoresByClassId(classId);
        List<Object[]> taskStats = noteRepository.findTaskStatsByClassId(classId);
        List<Object[]> top5Notes = noteRepository.findTop5NotesByClassId(classId);
        String className = noteRepository.findClassNameByClassId(classId);

        double totalAvgScore = 0;
        double totalMaxScore = 0;
        double totalMinScore = 0;
        double totalMedianScore = 0;

        if (!allScores.isEmpty()) {
            Collections.sort(allScores);
            totalAvgScore = allScores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            totalMaxScore = allScores.get(allScores.size() - 1);
            totalMinScore = allScores.get(0);
            if (allScores.size() % 2 == 0) {
                totalMedianScore = (allScores.get(allScores.size() / 2) + allScores.get(allScores.size() / 2 - 1)) / 2;
            } else {
                totalMedianScore = allScores.get(allScores.size() / 2);
            }
        }

        // 2. 构建提示词
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位资深的教育专家。请根据以下课程详细数据，为教师提供一份深入的课程学情总结和教学改进建议。\n\n");
        prompt.append("课程名称：").append(className).append("\n\n");
        
        prompt.append("【全课成绩概览】\n");
        prompt.append("- 平均分：").append(String.format("%.2f", totalAvgScore)).append("\n");
        prompt.append("- 最高分：").append(String.format("%.2f", totalMaxScore)).append("\n");
        prompt.append("- 最低分：").append(String.format("%.2f", totalMinScore)).append("\n");
        prompt.append("- 中位数分：").append(String.format("%.2f", totalMedianScore)).append("\n\n");

        prompt.append("【各作业完成度与表现】\n");
        for (Object[] row : taskStats) {
            String title = (String) row[0];
            long submitted = ((Number) row[1]).longValue();
            long total = ((Number) row[2]).longValue();
            double completionRate = total > 0 ? (double) submitted / total * 100 : 0;
            Double maxS = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            Double minS = row[4] != null ? ((Number) row[4]).doubleValue() : 0;
            Double avgS = row[5] != null ? ((Number) row[5]).doubleValue() : 0;
            
            prompt.append(String.format("- 作业《%s》: 完成率 %.1f%% (%d/%d), 最高分 %.1f, 最低分 %.1f, 平均分 %.1f\n", 
                    title, completionRate, submitted, total, maxS, minS, avgS));
        }
        prompt.append("\n");

        prompt.append("【优秀笔记 Top 5】\n");
        if (top5Notes.isEmpty()) {
            prompt.append("暂无评分记录。\n");
        } else {
            for (Object[] row : top5Notes) {
                prompt.append(String.format("- 《%s》 (作者: %s, 分数: %.1f)\n", row[0], row[2], ((Number) row[1]).doubleValue()));
            }
        }
        prompt.append("\n");

        prompt.append("请从以下几个方面进行总结和分析：\n");
        prompt.append("1. **学情深度诊断**：结合平均分、中位数和完成率，分析学生的整体知识掌握程度和学习积极性。\n");
        prompt.append("2. **重难点识别**：通过各作业表现差异，识别出学生普遍感到困难的知识模块。\n");
        prompt.append("3. **培优补差建议**：针对最高分与最低分的差距，以及优秀笔记的表现，提出分层教学建议。\n");
        prompt.append("4. **教学策略优化**：为教师提供至少 3 条具体的、可操作的下一阶段教学改进方案。\n");

        // 3. 调用 AI 总结
        String summaryContent = llmService.getCompletion(prompt.toString());

        System.out.println("输出内容"+summaryContent);
        // 4. 保存到数据库
        AssignmentSummary summary = assignmentSummaryRepository.findByClassId(classId)
                .orElse(new AssignmentSummary());
        
        summary.setClassId(classId);
        summary.setSummary(summaryContent);
        summary.setCreateTime(LocalDateTime.now());

        return assignmentSummaryRepository.save(summary);
    }
}
