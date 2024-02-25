package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    // ログインIDと日付でレポートを検索
    public List<Report> findByEmployeeCodeAndReportDate(String employeeCode, LocalDate reportDate);

}