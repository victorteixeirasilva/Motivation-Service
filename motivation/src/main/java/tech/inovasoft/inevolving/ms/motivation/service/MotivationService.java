package tech.inovasoft.inevolving.ms.motivation.service;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.MessageResponseDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.MicroServices;
import tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.TokenCache;
import tech.inovasoft.inevolving.ms.motivation.service.client.api.ApiClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.api.dto.UserEmailDTO;
import tech.inovasoft.inevolving.ms.motivation.service.client.email_service.EmailClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.email_service.dto.EmailRequest;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.TasksClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.Task;

import java.util.List;
import java.util.Random;

import static tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.MicroServices.*;

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

    @Autowired
    private TokenCache tokenCache;

    private String cachedTokenGateway;
    private String cachedTokenTasks;
    private String cachedTokenEmail;

    private String getValidTokenGateway() {
        if (cachedTokenGateway == null) {
            cachedTokenGateway = tokenCache.getToken(GATEWAY_SERVICE);
        }
        return cachedTokenGateway;
    }

    private String getValidTokenTasks() {
        if (cachedTokenTasks == null) {
            cachedTokenTasks = tokenCache.getToken(TASKS_SERVICE);
        }
        return cachedTokenTasks;
    }

    private String getValidTokenEmail() {
        if (cachedTokenEmail == null) {
            cachedTokenEmail = tokenCache.getToken(EMAIL_SERVICE);
        }
        return cachedTokenEmail;
    }

    public MessageResponseDTO sendEmailForUsersWithLateTasks() {
        ResponseEntity<List<UserEmailDTO>> responseUsers;

        try {
            responseUsers = apiClientService.getUsersIsVerifiedAndActive(getValidTokenGateway());
        } catch (FeignException.Unauthorized unauthorized) {
            cachedTokenGateway = null;
            return sendEmailForUsersWithLateTasks();
        } catch (Exception e) {
            return new MessageResponseDTO("Erro em getUsersIsVerifiedAndActive");
        }

        if (responseUsers.getStatusCode().is2xxSuccessful() && responseUsers.getBody() != null) {
            for (UserEmailDTO user : responseUsers.getBody()) {
                ResponseEntity<List<Task>> responseTasks;

                try {
                    responseTasks = tasksClientService.getTasksLate(user.id(), getValidTokenTasks());
                } catch (FeignException.Unauthorized unauthorized) {
                    cachedTokenTasks = null;
                    return sendEmailForUsersWithLateTasks();
                } catch (Exception e) {
                    responseTasks = null;
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
                        emailClientService.sendEmail(new EmailRequest(user.email(), "Tarefas atrasadas", body), getValidTokenEmail());
                    } catch (FeignException.Unauthorized unauthorized) {
                        cachedTokenEmail = null;
                        return sendEmailForUsersWithLateTasks();
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
                        emailClientService.sendEmail(new EmailRequest(user.email(), "Tarefas atrasadas", body), getValidTokenEmail());
                    } catch (FeignException.Unauthorized unauthorized) {
                        cachedTokenEmail = null;
                        return sendEmailForUsersWithLateTasks();
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
            responseUsers = apiClientService.getUsersDisconnectedAndActive(getValidTokenGateway());
        } catch (FeignException.Unauthorized unauthorized) {
            cachedTokenGateway = null;
            return sendEmailForUsersDisconnected();
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
//                    body += "\nSonho: " + dream.getName() + " - " + dream.getDescription() + " - " + dream.getUrlImage() + "\n";
                    body += "<!DOCTYPE html>\n" +
                            "<html lang=\"pt-BR\">\n" +
                            "<head>\n" +
                            "  <meta charset=\"UTF-8\" />\n" +
                            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                            "  <title>Seu Sonho do Dia</title>\n" +
                            "</head>\n" +
                            "\n" +
                            "<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f0f8ff;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;\">\n" +
                            "  <div style=\"max-width:600px;margin:20px auto;background-color:#ffffff;border-radius:12px;box-shadow:0 4px 12px rgba(0,0,0,0.1);overflow:hidden;border:1px solid #e0e0e0;\">\n" +
                            "\n" +
                            "    <!-- Cabeçalho -->\n" +
                            "    <div style=\"background-color:#6495ed;padding:25px 20px;text-align:center;color:#ffffff;border-bottom:1px solid #5a8be0;\">\n" +
                            "      <h1 style=\"margin:0;font-size:28px;font-weight:bold;line-height:1.2;\">Seu Sonho do Dia</h1>\n" +
                            "    </div>\n" +
                            "\n" +
                            "    <!-- Conteúdo -->\n" +
                            "    <div style=\"padding:25px 20px;\">\n" +
                            "\n" +
                            "      <h2 style=\"font-size:24px;color:#2c3e50;margin-top:0;margin-bottom:15px;text-align:center;line-height:1.3;\">\n" +
                            "        "+dream.getName()+"\n" +
                            "      </h2>\n" +
                            "\n" +
                            "      <div style=\"margin-bottom:25px;text-align:center;\">\n" +
                            "        <img src=\""+dream.getUrlImage()+"\" alt=\"Imagem do Sonho\" style=\"max-width:100%;height:auto;border-radius:8px;display:block;margin:0 auto;border:1px solid #dcdcdc;\" />\n" +
                            "      </div>\n" +
                            "\n" +
                            "      <p style=\"font-size:16px;line-height:1.6;color:#333333;margin:0 0 24px 0;text-align:justify;\">\n" +
                            "        "+dream.getDescription()+"\n" +
                            "      </p>\n" +
                            "\n" +
                            "      <!-- Botão -->\n" +
                            "      <div style=\"text-align:center;margin:0 0 10px 0;\">\n" +
                            "        <a href=\"https://inevolving.inovasoft.tech/\" target=\"_blank\"\n" +
                            "           style=\"display:inline-block;background-color:#2f6fed;color:#ffffff;text-decoration:none;padding:14px 22px;border-radius:10px;font-size:16px;font-weight:bold;\">\n" +
                            "          Entrar no sistema\n" +
                            "        </a>\n" +
                            "      </div>\n" +
                            "\n" +
                            "      <!-- Link fallback (bom para email) -->\n" +
                            "      <p style=\"font-size:12px;line-height:1.4;color:#666666;margin:10px 0 0 0;text-align:center;\">\n" +
                            "        Se o botão não funcionar, copie e cole este link no navegador:<br/>\n" +
                            "        <span style=\"color:#2f6fed;\">https://inevolving.inovasoft.tech/</span>\n" +
                            "      </p>\n" +
                            "\n" +
                            "    </div>\n" +
                            "\n" +
                            "    <!-- Rodapé -->\n" +
                            "    <div style=\"background-color:#f8f8f8;padding:15px 20px;text-align:center;font-size:12px;color:#777777;border-top:1px solid #e0e0e0;\">\n" +
                            "      <p style=\"margin:0;\">&copy; 2026 Inovasoft. Todos os direitos reservados.</p>\n" +
                            "    </div>\n" +
                            "\n" +
                            "  </div>\n" +
                            "</body>\n" +
                            "</html>";

                    try {
                        emailClientService.sendEmail(new EmailRequest(user.email(), "Sentimos a sua falta! seus sonhos te esperam!", body), getValidTokenEmail());
                    } catch (FeignException.Unauthorized unauthorized) {
                        cachedTokenEmail = null;
                        return sendEmailForUsersDisconnected();
                    }
                } else {
                    //TODO: Desenvolver o token cache, para fazer a requisição no email.
                    body = "Você não tem sonhos cadastrados, cadastre-os para que possamos te ajudar a sempre manter a diciplina!";
                    try {
                        emailClientService.sendEmail(new EmailRequest(user.email(), "Sentimos a sua falta! seus sonhos te esperam!", body), getValidTokenEmail());
                    } catch (FeignException.Unauthorized unauthorized) {
                        cachedTokenEmail = null;
                        return sendEmailForUsersDisconnected();
                    }
                }
            }
            return new MessageResponseDTO("Emails enviado com sucesso");
        } else {
            return new MessageResponseDTO("Erro ao enviar emails");
        }
    }
}
