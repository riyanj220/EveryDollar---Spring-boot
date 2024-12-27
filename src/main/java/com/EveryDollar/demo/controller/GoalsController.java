package com.EveryDollar.demo.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.EveryDollar.demo.dto.GoalsRequest;
import com.EveryDollar.demo.entity.GoalsEntity;
import com.EveryDollar.demo.entity.UserEntity;
import com.EveryDollar.demo.service.GoalsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Goals")
class GoalsRender {
    @GetMapping("/")
    public String renderGoals(HttpSession session, Model model) {
        // Get logged-in user details from session
        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Add user data to the model
            model.addAttribute("username", loggedInUser.getUsername());

            LocalDate currentDate = LocalDate.now();
            DayOfWeek currentDay = currentDate.getDayOfWeek();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

            // Add day and date to the model
            model.addAttribute("currentDay", currentDay.name()); // E.g., "WEDNESDAY"
            model.addAttribute("currentDate", currentDate.format(formatter)); // E.g., "February 22, 2023"
            
            return "ActionPlan/index"; // Ensure the correct template name
        } else {
            return "redirect:/User_login/login.html"; // Redirect to login page if session is invalid
        }
    }
}

@RestController
@RequestMapping("/goals")
public class GoalsController {

    @Autowired
    private GoalsService goalsService;

    // Add a new goal
    @PostMapping("/add")
    @ResponseBody
    public String addGoal(@RequestBody GoalsRequest request, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        goalsService.addGoal(
                request.getName(),
                request.getDate(),
                request.getTargetValue(),
                request.getProgressValue(),
                loggedInUser
        );
        return "Goal added successfully!";
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteGoal(@RequestParam String name, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        goalsService.deleteGoal(name, loggedInUser);
        return "Goal deleted successfully!";
    }


    // Update progress of a goal
    @PutMapping("/update")
    @ResponseBody
    public String updateGoalProgress(@RequestBody ProgressUpdateRequest request, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        goalsService.updateProgress(request.getName(), request.getProgressValue(), loggedInUser);
        return "Goal progress updated successfully!";
    }

    // Get all goals for the logged-in user
    @GetMapping
    @ResponseBody
    public List<GoalsEntity> getCurrentMonthGoals(HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        return goalsService.getCurrentMonthGoals(loggedInUser);
    }
}

class ProgressUpdateRequest {
    private String name;
    private int progressValue;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }
}
