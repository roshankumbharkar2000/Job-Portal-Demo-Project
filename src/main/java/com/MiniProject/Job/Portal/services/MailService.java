package com.MiniProject.Job.Portal.services;

public interface MailService {
    void sendRegistrationMail(String to, String name);

    void sendEmail(String to, String subject, String body);
}
