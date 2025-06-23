package tech.inovasoft.inevolving.ms.motivation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseVisionBord;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;
import tech.inovasoft.inevolving.ms.motivation.service.client.gerador_de_vision_bord.GeradorDeVisionBordClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.gerador_de_vision_bord.dto.ImageUrl;
import tech.inovasoft.inevolving.ms.motivation.service.client.gerador_de_vision_bord.dto.RequestGeradorDeVisionBordDTO;

import java.util.*;

@Service
public class DreamsService {

    @Autowired
    private DreamsRepository repository;

    @Autowired
    private GeradorDeVisionBordClientService geradorDeVisionBordClientService;

    private static final int MAX_DREAMS = 200;

    /**
     * @description - Create a new dream | Cria um novo sonho
     * @param dto - Dream information | Informações do sonho
     * @return - Dream created | Sonho criado
     * @throws MaximumNumberOfRegisteredDreamsException - Dream limit reached | Limite de sonhos atingido
     * @throws NotSavedDTOInDbException - Error saving dream | Erro ao salvar sonho
     */
    public Dreams addDream(
            DreamRequestDTO dto
    ) throws MaximumNumberOfRegisteredDreamsException, NotSavedDTOInDbException {
        List<Dreams> dreams = repository.findAllByUserId(dto.idUser());

        if (dreams.size() >= MAX_DREAMS) {
            throw new MaximumNumberOfRegisteredDreamsException();
        }

        try {
            return repository.save(new Dreams(dto));
        } catch (Exception e) {
            throw new NotSavedDTOInDbException(dto);
        }
    }

    /**
     * @description - Update a dream | Atualiza um sonho
     * @param dream - Dream | Sonho
     * @return - Updated dream | Sonho atualizado
     * @throws DreamNotFoundException - Dream not found | Sonho não encontrado
     * @throws UserWithoutAuthorizationAboutThisDreamException - User not allowed to update dream | Usuário sem permissão para atualizar sonho
     */
    public Dreams updateDream(
            Dreams dream
    ) throws DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        Optional<Dreams> dreamOpt = repository.findById(dream.getId());

        if (dreamOpt.isEmpty()){
            throw new DreamNotFoundException();

        } else {
            Dreams newDream = dream;
            UUID idUser = dreamOpt.get().getIdUser();

            if (idUser.equals(newDream.getIdUser())) {
                UUID idDream = dreamOpt.get().getId();
                newDream.setId(idDream);

                return repository.save(newDream);

            } else {
                throw new UserWithoutAuthorizationAboutThisDreamException();
            }
        }
    }

    /**
     * @description - Delete a dream | Deletar um sonho
     * @param idDream - Dream id | Id do sonho
     * @param idUser - User ID | Id do usuário
     * @return - Success Message | Mensagem de sucesso
     * @throws DataBaseException - Error deleting dream | Erro ao deletar sonho
     * @throws DreamNotFoundException - Dream not found | Sonho não encontrado
     * @throws UserWithoutAuthorizationAboutThisDreamException - User not allowed to delete dream | Usuário sem permissão para deletar sonho
     */
    public ResponseDeleteDream deleteDream(
            UUID idDream,
            UUID idUser
    ) throws DataBaseException, DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        Optional<Dreams> dreamOpt;

        try {
            dreamOpt = repository.findById(idDream);
        } catch (Exception e) {
            throw new DataBaseException("Error when getting Dreams.");
        }

        if (dreamOpt.isEmpty()){
            throw new DreamNotFoundException();
        }

        UUID dreamUserId = dreamOpt.get().getIdUser();

        if (!dreamUserId.equals(idUser)){
            throw new UserWithoutAuthorizationAboutThisDreamException();
        }

        Dreams dream = dreamOpt.get();
        try {
            repository.delete(dream);
            return new ResponseDeleteDream("Dream deleted!");
        } catch (Exception e) {
            throw new DataBaseException("Error when deleting Dreams.");
        }
    }

    /**
     * @description - Search for a dream by id | Busca um sonho pelo id
     * @param idDream - Id do sonho | dream id
     * @param idUser - Id do usuário | User ID
     * @return - Sonho | Dream
     * @throws DataBaseException - Erro ao buscar sonho | Error when searching for a dream
     * @throws DreamNotFoundException - Sonho não encontrado | Dream not found
     * @throws UserWithoutAuthorizationAboutThisDreamException - Usuário sem permissão para buscar sonho | User not allowed to search for dream
     */
    public Dreams getDreamByID(
            UUID idDream,
            UUID idUser
    ) throws DataBaseException, DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        Optional<Dreams> dreamOpt;

        try {
            dreamOpt = repository.findById(idDream);
        } catch (Exception e) {
            throw new DataBaseException("Error when getting Dreams.");
        }

        if (dreamOpt.isEmpty()){
            throw new DreamNotFoundException();
        }

        if (!idUser.equals(dreamOpt.get().getIdUser())){
            throw new UserWithoutAuthorizationAboutThisDreamException();
        }

        return dreamOpt.get();
    }

    /**
     * @description - Busca todos os sonhos de um usuário | Search for all the dreams of a user
     * @param idUser - Id do usuário | User ID
     * @return - Sonhos | Dreams
     * @throws DreamNotFoundException - Sonho não encontrado | Dream not found
     * @throws DataBaseException - Erro ao buscar sonho | Error when searching for a dream
     */
    public List<Dreams> getDreamsByUserId(
            UUID idUser
    ) throws DreamNotFoundException, DataBaseException {
        List<Dreams> dreams;

        try {
            dreams = repository.findAllByUserId(idUser);
        } catch (Exception e) {
            throw new DataBaseException("Error when getting Dreams.");
        }

        if (dreams.isEmpty()){
            throw new DreamNotFoundException("No dreams were found");
        }

        return dreams;
    }

    /**
     * @description - Busca 100 sonhos aleatórios | Search 100 random dreams
     * @param allDreams - Sonhos | Dreams
     * @return - Sonhos | Dreams
     */
    public List<Dreams> selectedDreams(
            List<Dreams> allDreams
    ) {
        Collections.shuffle(allDreams);
        return allDreams.subList(0, 100);
    }

    /**
     * @description - Gera um Vision Board | Generate a Vision Board
     * @param idUser - Id do usuário | User ID
     * @return - Imagem | Image
     * @throws DataBaseException - Erro ao buscar sonho | Error when searching for a dream
     * @throws DreamNotFoundException - Sonho não encontrado | Dream not found
     */
    public ResponseVisionBord generateVisionBordByUserId(
            UUID idUser
    ) throws DataBaseException, DreamNotFoundException {
        List<Dreams> allDreams = getDreamsByUserId(idUser);

        if (allDreams.size() < 100) {
            throw new DreamNotFoundException("User has less than 100 dreams, impossible to generate Vision Board.");
        }

        List<Dreams> selectedDreams = selectedDreams(allDreams);
        List<String> links = new ArrayList<>();
        for (Dreams dream : selectedDreams){
            links.add(dream.getUrlImage());
        }

        String user_id = String.valueOf(idUser);
        ImageUrl imageUrl = geradorDeVisionBordClientService.gerador(
                new RequestGeradorDeVisionBordDTO(
                        user_id, links
                )
        );

        return new ResponseVisionBord(imageUrl.image_url());
    }


}
