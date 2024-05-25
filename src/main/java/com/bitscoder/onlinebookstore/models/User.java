package com.bitscoder.onlinebookstore.models;

import com.bitscoder.onlinebookstore.constant.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name"),
                @UniqueConstraint(columnNames = "email")
        })
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Roles roles;
    @OneToMany(mappedBy = "user")
    private List<Book> books;


    public User() {
        super();
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "User"+ UUID.randomUUID().toString().substring(0, 5);
    }
}
