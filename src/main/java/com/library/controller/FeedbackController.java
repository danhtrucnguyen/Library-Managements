package com.library.controller;

import com.library.model.Feedback;
import com.library.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

//    @GetMapping("/feedback")
//    public String feedbackForm(Model model) {
//        model.addAttribute("feedback", new Feedback());
//        return "feedback-form";
//    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestBody Feedback feedback, Model model) {
        // Save the feedback using the service
        feedbackService.saveFeedback(feedback);

        // Add a success message to the model
        model.addAttribute("message", "Phản hồi đã được gửi thành công!");
        return "base";
    }

    @GetMapping("/admin/feedback_list")
    public String getAllFeedbacks(Model model) {
        model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        return "/admin/feedback_list";
    }
}