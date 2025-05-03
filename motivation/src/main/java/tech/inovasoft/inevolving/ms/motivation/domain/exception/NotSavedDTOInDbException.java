package tech.inovasoft.inevolving.ms.motivation.domain.exception;

import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;

public class NotSavedDTOInDbException extends Exception{
    public NotSavedDTOInDbException(DreamRequestDTO dto) {
        super("Could not save ("+dto+") to the Database");
    }
}
