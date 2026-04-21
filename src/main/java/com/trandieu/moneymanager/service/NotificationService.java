package com.trandieu.moneymanager.service;

import com.trandieu.moneymanager.dto.ExpenseDTO;
import com.trandieu.moneymanager.entity.ProfileEntity;
import com.trandieu.moneymanager.repository.ProfileRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
   private final ProfileRepository profileRepository;

   private final ExpenseService expenseService;

   private final EmailService emailService;

   @Value("${money.manager.frontend.url}")
   private String frontendUrl;

   // @Scheduled(cron = "0 * * * * *", zone = "IST")
   @Scheduled(cron = "0 0 22 * * *", zone = "IST")
   public void sendDailyIncomeExpenseReminder() {
      log.info("Jobs started: sendDailyIncomeExpenseReminder()");
      try {
         List<ProfileEntity> profiles = profileRepository.findAll();
         for (ProfileEntity profile : profiles) {

            List<ExpenseDTO> expenses = expenseService.getExpensesForUserOnDate(
                  profile.getId(), java.time.LocalDate.now());
            if (expenses.isEmpty())
               continue;

            double total = expenses.stream().mapToDouble(e -> e.getAmount().doubleValue()).sum();
            String date = java.time.LocalDate.now()
                  .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));

            StringBuilder items = new StringBuilder();
            for (ExpenseDTO e : expenses) {
               items.append("<div class='item'>")
                     .append("<span class='name'>").append(e.getName()).append("</span>")
                     .append("<span class='amt'>–$").append(String.format("%.2f", e.getAmount().doubleValue()))
                     .append("</span>")
                     .append("</div>");
            }

            String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                  "<style>" +
                  "*{margin:0;padding:0;box-sizing:border-box}" +
                  "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;background:#F4F4F4;color:#111}"
                  +
                  ".w{padding:32px 16px}" +
                  ".c{max-width:520px;margin:0 auto;background:#fff;border-radius:12px;border:1px solid #E8E8E8;overflow:hidden}"
                  +
                  ".top{padding:28px 28px 20px}" +
                  ".date{font-size:12px;color:#999;margin-bottom:8px}" +
                  "h1{font-size:20px;font-weight:600;margin-bottom:4px}" +
                  ".greet{font-size:13px;color:#666}" +
                  ".sep{height:1px;background:#F0F0F0;margin:0 28px}" +
                  ".mid{padding:20px 28px}" +
                  ".total-row{display:flex;justify-content:space-between;align-items:baseline;margin-bottom:20px}" +
                  ".total-lbl{font-size:12px;color:#999;margin-bottom:4px}" +
                  ".total-amt{font-size:26px;font-weight:700;margin-bottom:20px}" +
                  ".item{display:flex;justify-content:space-between;padding:10px 0;border-bottom:1px solid #F0F0F0;font-size:14px}"
                  +
                  ".item:last-child{border-bottom:none}" +
                  ".amt{color:#C0392B;font-weight:600}" +
                  ".bot{padding:16px 28px;display:flex;justify-content:space-between;;align-items:center;;background:#FAFAFA;border-top:1px solid #F0F0F0}"
                  +

                  "</style></head><body>" +
                  "<div class='w'><div class='c'>" +
                  "<div class='top'>" +
                  "<div class='date'>" + date + "</div>" +
                  "<h1>Daily expense summary</h1>" +
                  "<p class='greet'>Hello <strong>" + profile.getFullName() + "</strong></p>" +
                  "</div>" +
                  "<div class='sep'></div>" +
                  "<div class='mid'>" +
                  "<div class='total-lbl'>Total spent today</div>" +
                  "<div class='total-amt'>$" + String.format("%.2f", total) + "</div>" +
                  items +
                  "</div>" +
                  "<div class='bot'>" +
                  "<p><a href='" + frontendUrl
                  + "' style='display:inline-block;margin-top:10px;padding:10px 20px;background-color:#007BFF;color:#fff;text-decoration:none;border-radius:5px;'>View Dashboard</a></p>"
                  +
                  "</div>" +
                  "</div></div></body></html>";

            emailService.sendHtmlEmail(profile.getEmail(), "Daily expense summary – " + java.time.LocalDate.now(),
                  html);

         }

      } catch (Exception e) {
         throw new RuntimeException("Error sending daily income/expense reminder: " + e.getMessage(), e);
      }

   }

   // @Scheduled(cron = "0 * * * * *", zone = "IST")
   @Scheduled(cron = "0 0 23 * * *", zone = "IST")
   public void sendDailyExpenseSummary() {
      log.info("Sending daily expense summary emails to all users");
      try {
         List<ProfileEntity> profiles = profileRepository.findAll();

         for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> expenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());

            if (!expenses.isEmpty()) {
               StringBuilder table = new StringBuilder();
               table.append("<table style='width:100%;border-collapse:collapse;'>")
                     .append("<tr><th style='border:1px solid #ddd;padding:8px;text-align:left;'>Name</th>")
                     .append("<th style='border:1px solid #ddd;padding:8px;text-align:left;'>Category</th>")
                     .append("<th style='border:1px solid #ddd;padding:8px;text-align:left;'>Amount</th></tr>");
               int i = 1;
               for (ExpenseDTO expense : expenses) {
                  table.append("<tr>")
                        .append("<td style='border:1px solid #ddd;padding:8px;text-align:left;'>")
                        .append(i++).append(". ").append(expense.getName())
                        .append("</td>")
                        .append("<td style='border:1px solid #ddd;padding:8px;text-align:left;'>")
                        .append(expense.getCategoryName())
                        .append("</td>")
                        .append("<td style='border:1px solid #ddd;padding:8px;text-align:left;'>")
                        .append("$").append(String.format("%.2f", expense.getAmount().doubleValue()))
                        .append("</td>")
                        .append("</tr>");
               }
               table.append("</table>");
               String body = "<p>Hi " + profile.getFullName() + ",</p>" +
                     "<p>Here is the summary of your expenses for "
                     + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")) + ":</p>" +
                     table.toString() +
                     "<p>Total spent: <strong>$"
                     + String.format("%.2f", expenses.stream().mapToDouble(e -> e.getAmount().doubleValue()).sum())
                     + "</strong></p>" +
                     "<p><a href='" + frontendUrl
                     + "' style='display:inline-block;margin-top:10px;padding:10px 20px;background-color:#007BFF;color:#fff;text-decoration:none;border-radius:5px;'>View Dashboard</a></p>";

               emailService.sendHtmlEmail(profile.getEmail(), "Your daily expense summary for " + LocalDate.now(),
                     body);
            }
         }
      } catch (Exception e) {
         throw new RuntimeException("Error sending daily expense summary emails: " + e.getMessage(), e);
      }

   }
}
