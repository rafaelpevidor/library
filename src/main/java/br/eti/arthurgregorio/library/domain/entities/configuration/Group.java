package br.eti.arthurgregorio.library.domain.entities.configuration;

import br.eti.arthurgregorio.library.domain.entities.PersistentEntity;
import br.eti.arthurgregorio.library.infrastructure.misc.DefaultSchemes;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

/**
 * The user group entity
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 26/12/2017
 */
@Entity
@Audited
@ToString(exclude = "grants")
@EqualsAndHashCode(callSuper = true, exclude = "grants")
@Table(name = "groups", schema = DefaultSchemes.CONFIGURATION)
public class Group extends PersistentEntity {

    @Getter
    @Setter
    @NotBlank(message = "{group.name}")
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Getter
    @Setter
    @Column(name = "active", nullable = false)
    private boolean active;
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "id_parent")
    private Group parent;

    @OneToMany(mappedBy = "group", fetch = EAGER, cascade = REMOVE)
    private final List<Grant> grants;

    /**
     * Constructor
     */
    public Group() {
        this.active = true;
        this.grants = Collections.emptyList();
    }

    /**
     * Constructor
     *
     * @param name the name of the {@link Group}
     */
    public Group(String name) {
        this();
        this.name = name;
    }

    /**
     * Constructor
     *
     * @param name   the name of the {@link Group}
     * @param parent his parent
     */
    public Group(String name, Group parent) {
        this();
        this.name = name;
        this.parent = parent;
    }

    /**
     * Method to list all {@link Grant} of this group
     *
     * @return {@link List} of this group {@link Grant}
     */
    public List<Grant> getGrants() {

        final List<Grant> groupGrants = new ArrayList<>(this.grants);

        if (this.parent != null) {
            groupGrants.addAll(this.parent.getGrants());
        }
        return Collections.unmodifiableList(groupGrants);
    }

    /**
     * Method to get all permissions of this group
     *
     * @return all {@link Permissions}
     */
    public Set<String> getPermissions() {
        return this.getGrants().stream()
                .map(Grant::getAuthorization)
                .map(Authorization::getFullPermission)
                .collect(Collectors.toSet());
    }

    /**
     * Method to check if this is an administrators group
     *
     * @return if is the administrators {@link Group}
     */
    public boolean isAdministrator() {
        return this.name.equals("Administradores");
    }
}