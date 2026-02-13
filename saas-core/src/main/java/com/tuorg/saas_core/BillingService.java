package com.tuorg.saas_core;

import com.tuorg.saas_core.model.Invoice;
import com.tuorg.saas_core.model.InvoiceLine;
import com.tuorg.saas_core.model.Profile;
import com.tuorg.saas_core.model.Subscription;
import com.tuorg.saas_core.repository.InvoiceRepository;
import com.tuorg.saas_core.repository.ProfileRepository;
import com.tuorg.saas_core.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    @Autowired private SubscriptionRepository subs;
    @Autowired private InvoiceRepository invoices;
    @Autowired private ProfileRepository profiles;

    @Transactional
    public int procesarRenovacionesAhora() {
        var pendientes = subs.findPendientesRenovacion();
        pendientes.forEach(this::generarFacturaYAvanzar);
        return pendientes.size();
    }

    private void generarFacturaYAvanzar(Subscription s) {
        Profile p = profiles.findById(s.getUser().getId()).orElse(null);
        String country = (p != null && p.getCountry() != null) ? p.getCountry() : "ES";
        double ivaRate = tasaIVA(country);

        long baseCents = s.getPlan().getPriceMonthlyCents();
        long taxCents  = Math.round(baseCents * ivaRate);
        long totalCents = baseCents + taxCents;

        Invoice inv = new Invoice();
        inv.setSubscription(s);
        inv.setPeriodStart(s.getBillingAnchorAt());
        inv.setPeriodEnd(s.getNextBillingAt());
        inv.setTotalCents(totalCents);
        inv.setCurrency(s.getPlan().getCurrency());
        inv.setStatus("ISSUED");

        InvoiceLine l1 = new InvoiceLine();
        l1.setInvoice(inv);
        l1.setLineType("SUBSCRIPTION");
        l1.setDescription("Plan " + s.getPlan().getName());
        l1.setAmountCents(baseCents);

        InvoiceLine l2 = new InvoiceLine();
        l2.setInvoice(inv);
        l2.setLineType("TAX");
        l2.setDescription("IVA " + country + " (" + Math.round(ivaRate * 100) + "%)");
        l2.setAmountCents(taxCents);

        inv.getLines().add(l1);
        inv.getLines().add(l2);

        invoices.save(inv);

        s.setBillingAnchorAt(s.getNextBillingAt());
        s.setNextBillingAt(s.getNextBillingAt().plusMonths(1));
        subs.save(s);
    }

    private double tasaIVA(String country) {
        return "ES".equals(country) ? 0.21 : 0.0;
    }
}
