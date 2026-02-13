package com.tuorg.saas_core.controller;

import com.tuorg.saas_core.BillingService;
import com.tuorg.saas_core.model.*;
import com.tuorg.saas_core.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.tuorg.saas_core.repository.ProfileRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Controller
public class SubscriptionController {

    private final SubscriptionRepository subRepo;
    private final PlanRepository planRepo;
    private final UserRepository userRepo;

    @Autowired private InvoiceRepository invoiceRepo;
    @Autowired private BillingService billing;
    @Autowired private ProfileRepository profileRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public SubscriptionController(SubscriptionRepository subRepo, PlanRepository planRepo, UserRepository userRepo) {
        this.subRepo = subRepo;
        this.planRepo = planRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String msg,
                        @RequestParam(required = false) String error,
                        Model model) {
        model.addAttribute("plans", planRepo.findAll());
        model.addAttribute("subscriptions", subRepo.findAll());
        model.addAttribute("msg", msg);
        model.addAttribute("error", error);
        var demoUser = userRepo.findAll().stream().findFirst().orElse(null);
        model.addAttribute("demoUser", demoUser);

        var demoProfile = (demoUser == null) ? null : profileRepo.findById(demoUser.getId()).orElse(null);
        model.addAttribute("demoProfile", demoProfile);


        return "index";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam Long planId) {
        User user = userRepo.findAll().get(0); // demo
        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        Subscription s = new Subscription();
        s.setUser(user);
        s.setPlan(plan);
        s.setBillingAnchorAt(OffsetDateTime.now());
        s.setNextBillingAt(OffsetDateTime.now().plusDays(plan.getDurationDays()));
        s.setStatus(SubscriptionStatus.ACTIVE);

        subRepo.save(s);
        return "redirect:/";
    }

    @PostMapping("/subscriptions/{id}/delete")
    public String deleteSubscription(@PathVariable Long id) {
        subRepo.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/subscriptions/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam SubscriptionStatus status) {
        Subscription s = subRepo.findById(id).orElseThrow();
        s.setStatus(status);
        subRepo.save(s);
        return "redirect:/";
    }

    @PostMapping("/subscriptions/delete-all")
    public String deleteAllSubscriptions() {
        subRepo.deleteAll();
        return "redirect:/";
    }

    @GetMapping("/facturacion")
    public String facturacion(@RequestParam(defaultValue = "dev@test.com") String email,
                              @RequestParam(required = false) String desde, // YYYY-MM-DD
                              @RequestParam(required = false) String hasta,
                              @RequestParam(required = false) Long minTotalCents,
                              Model model) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe user con email " + email));

        OffsetDateTime dDesde = (desde == null || desde.isBlank())
                ? OffsetDateTime.now().minusYears(1)
                : OffsetDateTime.parse(desde + "T00:00:00Z");

        OffsetDateTime dHasta = (hasta == null || hasta.isBlank())
                ? OffsetDateTime.now()
                : OffsetDateTime.parse(hasta + "T23:59:59Z");

        long min = (minTotalCents == null) ? 0L : minTotalCents;

        List<Invoice> facturas = invoiceRepo.filtrarFacturas(user.getId(), dDesde, dHasta, min);

        model.addAttribute("facturas", facturas);
        model.addAttribute("email", email);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("minTotalCents", minTotalCents);

        return "facturacion";
    }

    @Transactional(readOnly = true)
    @GetMapping("/admin/auditoria")
    public String auditoria(@RequestParam Long subscriptionId, Model model) {

        var opt = subRepo.findById(subscriptionId);
        if (opt.isEmpty()) {
            return "redirect:/?error=No%20existe%20la%20suscripci%C3%B3n%20id%3D" + subscriptionId;
        }

        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Number> revisions = reader.getRevisions(Subscription.class, subscriptionId);

        List<Subscription> historial = revisions.stream()
                .sorted((a, b) -> Integer.compare(b.intValue(), a.intValue())) // newest first
                .map(rev -> reader.find(Subscription.class, subscriptionId, rev))
                .toList();

        model.addAttribute("subscription", opt.get());
        model.addAttribute("revisions", revisions);
        model.addAttribute("historial", historial);

        return "auditoria";
    }
    @PostMapping("/subscriptions/{id}/make-pending")
    public String makePending(@PathVariable Long id) {
        Subscription s = subRepo.findById(id).orElseThrow();
        s.setStatus(SubscriptionStatus.ACTIVE);
        s.setNextBillingAt(OffsetDateTime.now().minusDays(1)); // ayer => pendiente
        subRepo.save(s);
        return "redirect:/?msg=Suscripci%C3%B3n%20" + id + "%20puesta%20pendiente";
    }





    @Transactional
    @PostMapping("/perfil/country")
    public String cambiarPais(@RequestParam Long userId,
                              @RequestParam String country) {

        Profile p = profileRepo.findById(userId).orElse(null);

        if (p == null) {
            p = new Profile();
            User uRef = entityManager.getReference(User.class, userId); // managed reference
            p.setUser(uRef);              // <-- NECESARIO por @MapsId
            p.setFullName("Demo");
        }

        p.setCountry(country.toUpperCase());
        profileRepo.save(p);

        return "redirect:/?msg=Pais%20cambiado%20a%20" + country.toUpperCase();
    }




    @PostMapping("/renovar-ahora")
    public String renovarAhora() {
        int n = billing.procesarRenovacionesAhora();
        return "redirect:/?msg=Renovadas%20" + n;
    }
}
