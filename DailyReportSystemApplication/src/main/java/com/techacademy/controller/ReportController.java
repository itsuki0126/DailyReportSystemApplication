package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final EmployeeService employeeService;

    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());

        return "reports/list";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, Employee employee, Model model) {

        // SpringSecurityを使用して、ログイン中のユーザーのcodeを取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserID = authentication.getName();

        // modelにcodeを登録
        model.addAttribute("code", loggedInUserID);

        // IDから氏名を取得し、modelに登録
        Employee loggedInUser = employeeService.findByCode(loggedInUserID);
        String loggedInUserName = loggedInUser.getName();
        model.addAttribute("loggedInUserName", loggedInUserName);

        return "reports/add";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Employee employee, Model model) {

        System.out.println("Code from form: " + report.getCode());
        System.out.println("Title from form: " + report.getTitle());
        System.out.println("Content from form: " + report.getContent());
        System.out.println("Date from form: " + report.getReportDate());

        // 入力チェック
        if (res.hasErrors()) {
            System.out.println("バリデーションエラー！！！: " + res.getAllErrors());
            return create(report, employee, model);
        }

        ErrorKinds result = reportService.save(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, employee, model);
        }

        reportService.save(report);

        return "redirect:/reports";
    }
}
