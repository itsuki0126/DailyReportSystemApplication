package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // ログインIDと日付でレポートを検索
    public Report findByEmployeeCodeAndReportDate(String employeeCode, LocalDate reportDate) {
        return reportRepository.findByEmployeeCodeAndReportDate(employeeCode, reportDate);
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report) {

        // チェック用に、Employeeを取得しreportにセット
        Report tempReport = findById(report.getId());
        Employee tempEmployee = tempReport.getEmployee();
        report.setEmployee(tempEmployee);

        // チェック用に、employeeCodeとreprtDateを取得
        String employeeCode = report.getEmployee().getCode();
        LocalDate reportDate = report.getReportDate();

        // 画面で表示中の従業員 かつ 入力した日付の日報データをDBから取得（存在しない場合はnull）
        Report dbReport = reportRepository.findByEmployeeCodeAndReportDate(employeeCode, reportDate);

        // 画面で表示中のもの以外に、登録されている日報がある場合エラー
        if (dbReport != null && !dbReport.getId().equals(report.getId())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        // 必要事項を取得しセット
        boolean deleteFlag = dbReport.isDeleteFlg();
        report.setDeleteFlg(deleteFlag);

        LocalDateTime createdAt = dbReport.getCreatedAt();
        report.setCreatedAt(createdAt);

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        // 保存（更新）
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

}
