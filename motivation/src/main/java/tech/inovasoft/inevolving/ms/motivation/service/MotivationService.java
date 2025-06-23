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
                    continue;
                }

                String body = "";

                if (responseTasks.getStatusCode().is2xxSuccessful() && responseTasks.getBody() != null) {

                    body = "Olá você tem, " + responseTasks.getBody().size() + " tarefa(s) atrasada(s): \n";

                    for (Task task : responseTasks.getBody()) {
                        body = body + "Tarefa: " + task.nameTask() + " - " + task.descriptionTask() + "\n";
                    }

                    emailClientService.sendEmail(new EmailRequest(user.email(), "Tarefas atrasadas", body));
                } else {
                    body = "Olá você tem, 0 tarefa(s) atrasada(s), meus parabens!";
                    emailClientService.sendEmail(new EmailRequest(user.email(), "Tarefas atrasadas", body));
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
