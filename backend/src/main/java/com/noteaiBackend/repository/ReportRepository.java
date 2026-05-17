package com.noteaiBackend.repository;

import com.noteaiBackend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    
    // 根据举报类型查询
    List<Report> findByType(Integer type);
    
    // 根据举报类型和对应ID查询
    List<Report> findByTypeAndTypeId(Integer type, Integer typeId);
    
    // 根据举报人ID查询
    List<Report> findByUserId(Integer userId);
    
    // 根据举报类型和举报人ID查询
    List<Report> findByTypeAndUserId(Integer type, Integer userId);
    
    // 根据举报时间倒序查询
    List<Report> findAllByOrderByCreateTimeDesc();
    
    // 根据举报类型和举报时间倒序查询
    List<Report> findByTypeOrderByCreateTimeDesc(Integer type);
}
