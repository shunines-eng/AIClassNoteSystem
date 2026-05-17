package com.noteaiBackend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.noteaiBackend.entity.Report;
import com.noteaiBackend.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    // 获取所有举报列表
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreateTimeDesc();
    }

    // 根据类型获取举报列表
    public List<Report> getReportsByType(Integer type) {
        return reportRepository.findByTypeOrderByCreateTimeDesc(type);
    }

    // 获取用户举报
    public List<Report> getUserReports() {
        return reportRepository.findByTypeOrderByCreateTimeDesc(Report.TYPE_USER);
    }

    // 获取笔记举报
    public List<Report> getNoteReports() {
        return reportRepository.findByTypeOrderByCreateTimeDesc(Report.TYPE_NOTE);
    }

    // 获取课程举报
    public List<Report> getClassReports() {
        return reportRepository.findByTypeOrderByCreateTimeDesc(Report.TYPE_CLASS);
    }

    // 创建举报
    public Report createReport(Integer type, Integer typeId, Integer userId, String info) {
        Report report = Report.builder()
                .type(type)
                .typeId(typeId)
                .userId(userId)
                .info(info)
                .createTime(LocalDateTime.now())
                .status(Report.STATUS_PENDING)
                .build();
        return reportRepository.save(report);
    }

    // 更新举报状态
    public Report updateStatus(Integer id, Integer status) {
        Report report = getReportById(id);
        report.setStatus(status);
        return reportRepository.save(report);
    }

    // 根据ID获取举报
    public Report getReportById(Integer id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("举报记录不存在"));
    }

    // 删除举报
    public void deleteReport(Integer id) {
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("举报记录不存在");
        }
        reportRepository.deleteById(id);
    }

    // 批量删除举报
    public void deleteReports(List<Integer> ids) {
        reportRepository.deleteAllById(ids);
    }

    // 获取特定类型和ID的举报记录
    public List<Report> getReportsByTypeAndTypeId(Integer type, Integer typeId) {
        return reportRepository.findByTypeAndTypeId(type, typeId);
    }

    // 获取举报统计
    public ReportStatistics getReportStatistics() {
        List<Report> allReports = reportRepository.findAll();
        long userReports = allReports.stream().filter(r -> r.getType() == Report.TYPE_USER).count();
        long noteReports = allReports.stream().filter(r -> r.getType() == Report.TYPE_NOTE).count();
        long classReports = allReports.stream().filter(r -> r.getType() == Report.TYPE_CLASS).count();
        
        return ReportStatistics.builder()
                .totalReports(allReports.size())
                .userReports((int) userReports)
                .noteReports((int) noteReports)
                .classReports((int) classReports)
                .build();
    }

    // 统计类
    @lombok.Builder
    @lombok.Data
    public static class ReportStatistics {
        private int totalReports;
        private int userReports;
        private int noteReports;
        private int classReports;
    }
}
