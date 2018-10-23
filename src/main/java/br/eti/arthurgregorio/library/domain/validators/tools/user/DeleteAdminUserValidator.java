package br.eti.arthurgregorio.library.domain.validators.tools.user;

import br.eti.arthurgregorio.library.domain.model.entities.tools.User;
import br.eti.arthurgregorio.library.domain.model.exception.BusinessLogicException;
import br.eti.arthurgregorio.library.domain.validators.BusinessValidator;
import br.eti.arthurgregorio.library.infrastructure.cdi.qualifier.AuthenticatedUser;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * {@link BusinessValidator} to validate if you are deleting the admin
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 2.0.0, 23/10/2018
 */
@Dependent
public class DeleteAdminUserValidator implements UserDeletingValidator {

    @Inject
    @AuthenticatedUser
    private User principal;
    
    /**
     * {@inheritDoc }
     *
     * @param value
     */
    @Override
    public void validate(User value) {
        
        final String principalUsername = this.principal.getUsername();
        
        // prevent to delete you own user 
        if (principalUsername.equals(value.getUsername())) {
            throw BusinessLogicException.create("error.user.delete-principal");
        }
        
        // prevent to delete the main admin
        if (value.isAdministrator()) {
            throw BusinessLogicException.create("error.user.delete-administrator");
        }
    }
}
