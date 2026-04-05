package com.project.customer_experience.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "Nombre de usuario obligatorio")
    @Column (unique = true)
    private String username;

    @NotBlank(message = "Email obligatorio")
    @Email(message = "Formato de Email inválido")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener una longitud mínima de 8 caracteres")
    private String password;

    private String firstname;
    private String lastname;
}
