package com.tuorg.saas_core.controller;

import com.tuorg.saas_core.model.*;
import com.tuorg.saas_core.repository.PlanRepository;
import com.tuorg.saas_core.repository.SubscriptionRepository;
import com.tuorg.saas_core.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SubscriptionController {

    private final SubscriptionRepository subRepo;
    private final PlanRepository planRepo;
    private final UserRepository userRepo;

    // Constructor injection... (omitted for brevity)
    public SubscriptionController(SubscriptionRepository subRepo, PlanRepository planRepo, UserRepository userRepo) {
        this.subRepo = subRepo;
        this.planRepo = planRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("plans", planRepo.findAll());
        model.addAttribute("subscriptions", subRepo.findAll());
        return "index";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam Long planId) {
        User user = userRepo.findAll().get(0); // Cogemos el primero para el ejemplo rápido
        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        Subscription s = new Subscription();
        s.setUser(user);
        s.setPlan(plan);
        s.setBillingAnchorAt(java.time.OffsetDateTime.now());
        s.setNextBillingAt(java.time.OffsetDateTime.now().plusDays(plan.getDurationDays()));
        s.setStatus(SubscriptionStatus.ACTIVE);

        subRepo.save(s);
        return "redirect:/";
    }
}
