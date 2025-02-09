package com.EveryDollar.demo.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.EveryDollar.demo.entity.UserEntity;
import com.EveryDollar.demo.service.ReportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Report")
class ReportController {
    @GetMapping("/")
    public String renderNetworth(HttpSession session, Model model) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            model.addAttribute("username", loggedInUser.getUsername());

            LocalDate currentDate = LocalDate.now();
            DayOfWeek currentDay = currentDate.getDayOfWeek();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

            model.addAttribute("currentDay", currentDay.name()); 
            model.addAttribute("currentDate", currentDate.format(formatter)); 
            
            return "FinancialReport/index"; 
        } else {
            return "redirect:/User_login/login.html"; 
        }
    }
}

@RestController
@RequestMapping("/financial-report")
public class FinancialReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/all")
    @ResponseBody
    public List<Map<String, Object>> getAllReports(HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        List<Map<String, Object>> allReports = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            allReports.add(reportService.generateMonthlyReport(loggedInUser, month));
        }
        return allReports;
    }
}