-- V1__init.sql

create table users (
                       id bigserial primary key,
                       email varchar(255) not null unique,
                       password_hash varchar(255) not null,
                       created_at timestamptz not null default now()
);

create table profiles (
                          user_id bigint primary key references users(id),
                          full_name varchar(255),
                          tax_id varchar(50),
                          country varchar(2)
);

create table plans (
                       id bigserial primary key,
                       code varchar(50) not null unique,
                       name varchar(100) not null,
                       level varchar(30) not null,
                       price_monthly_cents bigint not null,
                       currency varchar(3) not null,
                       active boolean not null default true
);

create table subscriptions (
                               id bigserial primary key,
                               user_id bigint not null references users(id),
                               status varchar(20) not null,
                               current_plan_id bigint not null references plans(id),
                               billing_anchor_at timestamptz not null,
                               next_billing_at timestamptz not null,
                               canceled_at timestamptz
);

create index idx_subscriptions_user_id on subscriptions(user_id);
create index idx_subscriptions_next_billing_at on subscriptions(next_billing_at);

create table subscription_changes (
                                      id bigserial primary key,
                                      subscription_id bigint not null references subscriptions(id),
                                      old_plan_id bigint references plans(id),
                                      new_plan_id bigint not null references plans(id),
                                      changed_at timestamptz not null default now(),
                                      effective_at timestamptz not null default now(),
                                      proration_amount_cents bigint not null default 0,
                                      currency varchar(3) not null,
                                      actor_user_id bigint references users(id)
);

create index idx_sub_changes_subscription_id on subscription_changes(subscription_id);
create index idx_sub_changes_changed_at on subscription_changes(changed_at);

create table invoices (
                          id bigserial primary key,
                          subscription_id bigint not null references subscriptions(id),
                          period_start timestamptz not null,
                          period_end timestamptz not null,
                          issue_at timestamptz not null default now(),
                          total_cents bigint not null,
                          currency varchar(3) not null,
                          status varchar(20) not null
);

create index idx_invoices_subscription_id on invoices(subscription_id);
create index idx_invoices_issue_at on invoices(issue_at);

create table invoice_lines (
                               id bigserial primary key,
                               invoice_id bigint not null references invoices(id),
                               line_type varchar(30) not null,
                               description varchar(255) not null,
                               amount_cents bigint not null
);

create index idx_invoice_lines_invoice_id on invoice_lines(invoice_id);
