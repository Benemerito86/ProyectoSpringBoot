package com.tuorg.saas_core;

import com.tuorg.saas_core.model.*;
import com.tuorg.saas_core.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepo;
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;
    private final SubscriptionRepository subRepo;

    public DataInitializer(PlanRepository planRepo,
                           UserRepository userRepo,
                           ProfileRepository profileRepo,
                           SubscriptionRepository subRepo) {
        this.planRepo = planRepo;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.subRepo = subRepo;
    }

    @Override
    public void run(String... args) {
        if (planRepo.count() == 0) {
            Plan basic = new Plan();
            basic.setCode("BASIC");
            basic.setName("Basic");
            basic.setLevel("BASIC");
            basic.setPriceMonthlyCents(999L);
            basic.setCurrency("EUR");
            planRepo.save(basic);

            Plan premium = new Plan();
            premium.setCode("PREMIUM");
            premium.setName("Premium");
            premium.setLevel("PREMIUM");
            premium.setPriceMonthlyCents(2999L);
            premium.setCurrency("EUR");
            planRepo.save(premium);
        }

        if (userRepo.count() == 0) {
            User u = new User();
            u.setEmail("dev@test.com");
            u.setPasswordHash("$2a$10$dummyHashForDevelopment");
            userRepo.save(u);

            Profile p = new Profile();
            p.setUser(u);
            p.setFullName("Desarrollador SaaS");
            p.setCountry("ES");
            profileRepo.save(p);

            if (subRepo.count() == 0) {
                Plan basicPlan = planRepo.findAll().stream()
                        .filter(pl -> "BASIC".equals(pl.getCode()))
                        .findFirst()
                        .orElseThrow();

                Subscription demoSub = new Subscription();
                demoSub.setUser(u);
                demoSub.setPlan(basicPlan);
                demoSub.setStatus(SubscriptionStatus.ACTIVE);
                demoSub.setBillingAnchorAt(OffsetDateTime.now().minusDays(35));
                demoSub.setNextBillingAt(OffsetDateTime.now().minusDays(1));
                subRepo.save(demoSub);

                System.out.println("✅ Suscripción PENDIENTE creada para demo renovación");
            }
        }
    }
}
