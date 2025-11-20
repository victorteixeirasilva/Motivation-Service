package tech.inovasoft.inevolving.ms.motivation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.MessageResponseDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.service.client.api.ApiClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.api.dto.UserEmailDTO;
import tech.inovasoft.inevolving.ms.motivation.service.client.email_service.EmailClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.email_service.dto.EmailRequest;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.TasksClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.Task;

import java.util.List;
import java.util.Random;

@Service
public class MotivationService {

    @Autowired
    private ApiClientService apiClientService;
    @Autowired
    private TasksClientService tasksClientService;
    @Autowired
    private EmailClientService emailClientService;
    @Autowired
    private DreamsService dreamsService;

    public MessageResponseDTO sendEmailForUsersWithLateTasks() {
        ResponseEntity<List<UserEmailDTO>> responseUsers;

        try {
            responseUsers = apiClientService.getUsersIsVerifiedAndActive();
        } catch (Exception e) {
            return new MessageResponseDTO("Erro em getUsersIsVerifiedAndActive");
        }

        if (responseUsers.getStatusCode().is2xxSuccessful() && responseUsers.getBody() != null) {
            for (UserEmailDTO user : responseUsers.getBody()) {
                ResponseEntity<List<Task>> responseTasks;

                try {
                    responseTasks = tasksClientService.getTasksLate(user.id());
                } catch (Exception e) {
                    responseTasks = null;
//                    continue;
                }

                String body = "";

                if (responseTasks != null) {

                    body = "<!DOCTYPE html>\n" +
                            "<html lang=\"pt-BR\">\n" +
                            "  <head>\n" +
                            "    <meta charset=\"UTF-8\" />\n" +
                            "    <title>Tarefas Atrasadas</title>\n" +
                            "  </head>\n" +
                            "  <body style=\"margin:0; padding:0; background-color:#F2EAE3; font-family:Arial, sans-serif;\">\n" +
                            "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px; margin:auto; background-color:#E7E2D6; padding:40px; border-radius:8px;\">\n" +
                            "      <tr>\n" +
                            "        <td>\n" +
                            "          <h2 style=\"color:#0C052E; text-align:center;\">⏰ Suas tarefas atrasadas</h2>\n" +
                            "          <p style=\"color:#0C4F64; font-size:16px;\">Olá você tem, " + responseTasks.getBody().size() + " tarefa(s) atrasada(s): \\n</p>\n" +
                            "          <ul style=\"padding-left:20px; color:#0C052E;\">";

                    for (Task task : responseTasks.getBody()) {
                        body = body +
                                "<li style=\"margin-bottom:10px;\">\n" +
                                "   <strong>"+ task.nameTask() +"</strong><br />\n" +
                                "   <span style=\"color:#E76148;\">Data da Tarefa: "+ task.dateTask() +"</span>\n" +
                                "</li>";
                    }

                    body = body + "</ul>\n" +
                            "          <p style=\"margin-top:30px; font-size:14px; color:#B3AFA1;\">Organize-se e mantenha sua produtividade em dia \uD83D\uDCAA</p>\n" +
                            "        </td>\n" +
                            "      </tr>\n" +
                            "    </table>\n" +
                            "  </body>\n" +
                            "</html>";

                    try {
                        emailClientService.sendEmail(new EmailRequest(user.email(), "Tarefas atrasadas", body));
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                        throw new RuntimeException("ERROR: " + e.getMessage());
                    }
                } else {
                    body = "<!DOCTYPE html>\n" +
                            "<html lang=\"pt-BR\">\n" +
                            "  <head>\n" +
                            "    <meta charset=\"UTF-8\" />\n" +
                            "    <title>Tarefas Atrasadas</title>\n" +
                            "  </head>\n" +
                            "  <body style=\"margin:0; padding:0; background-color:#F2EAE3; font-family:Arial, sans-serif;\">\n" +
                            "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px; margin:auto; background-color:#E7E2D6; padding:40px; border-radius:8px;\">\n" +
                            "      <tr>\n" +
                            "        <td>\n" +
                            "          <h2 style=\"color:#0C052E; text-align:center;\">⏰ Suas tarefas atrasadas</h2>\n" +
                            "          <p style=\"color:#0C4F64; font-size:16px;\">Olá, Meus parabéns, você não tem nenhuma tarefa, atrasada!</p>\n" +
                            "          <p style=\"margin-top:30px; font-size:14px; color:#B3AFA1;\">Organize-se e mantenha sua produtividade em dia \uD83D\uDCAA</p>\n" +
                            "        </td>\n" +
                            "      </tr>\n" +
                            "    </table>\n" +
                            "  </body>\n" +
                            "</html>";
                    try {
                        emailClientService.sendEmail(new EmailRequest(user.email(), "Tarefas atrasadas", body));
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                        throw new RuntimeException("ERROR: " + e.getMessage());
                    }
                }
            }
            return new MessageResponseDTO("Emails enviado com sucesso");
        } else {
            return new MessageResponseDTO("Erro ao enviar emails");
        }
    }

    public MessageResponseDTO sendEmailForUsersDisconnected() {
        ResponseEntity<List<UserEmailDTO>> responseUsers;

        try {
            responseUsers = apiClientService.getUsersDisconnectedAndActive();
        } catch (Exception e) {
            return new MessageResponseDTO("Erro em getUsersIsVerifiedAndActive");
        }

        if (responseUsers.getStatusCode().is2xxSuccessful() && responseUsers.getBody() != null) {
            for (UserEmailDTO user : responseUsers.getBody()) {
                List<Dreams> dreams;

                try {
                    dreams = dreamsService.getDreamsByUserId(user.id());
                } catch (Exception e) {
                    continue;
                }

                String body = "";

                if (!dreams.isEmpty()) {
                    body = "\nOlá, notamos sua ausência, vamos te ajudar a se reencontrar: \n";

                    Random random = new Random();
                    int idexRandom = random.nextInt(dreams.size());
                    Dreams dream = dreams.get(idexRandom);
                    body += "\nSonho: " + dream.getName() + " - " + dream.getDescription() + "\n";

                    emailClientService.sendEmail(new EmailRequest(user.email(), "Sentimos a sua falta! seus sonhos te esperam!", body));
                } else {
                    body = "Você não tem sonhos cadastrados, cadastre-os para que possamos te ajudar a sempre manter a diciplina!";
                    emailClientService.sendEmail(new EmailRequest(user.email(), "Sentimos a sua falta! seus sonhos te esperam!", body));
                }
            }
            return new MessageResponseDTO("Emails enviado com sucesso");
        } else {
            return new MessageResponseDTO("Erro ao enviar emails");
        }
    }
}
