package com.tuorg.saas_core;

import com.tuorg.saas_core.model.*;
import com.tuorg.saas_core.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepo;
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;

    public DataInitializer(PlanRepository planRepo, UserRepository userRepo, ProfileRepository profileRepo) {
        this.planRepo = planRepo;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    @Override
    public void run(String... args) {
        if (planRepo.count() == 0) {
            Plan basic = new Plan();
            basic.setCode("BASIC");
            basic.setName("Basic");
            basic.setLevel("BASIC");
            basic.setPriceMonthlyCents(999L); // $9.99
            basic.setCurrency("USD");
            planRepo.save(basic);

            Plan premium = new Plan();
            premium.setCode("PREMIUM");
            premium.setName("Premium");
            premium.setLevel("PREMIUM");
            premium.setPriceMonthlyCents(2999L); // $29.99
            premium.setCurrency("USD");
            planRepo.save(premium);
        }

        if (userRepo.count() == 0) {
            User u = new User();
            u.setEmail("dev@test.com");
            u.setPasswordHash("$2a$10$dummyHashForDevelopment"); // Placeholder hash for demo
            userRepo.save(u);

            Profile p = new Profile();
            p.setUser(u);
            p.setFullName("Desarrollador SaaS");
            p.setCountry("ES");
            profileRepo.save(p);
        }
    }
}
