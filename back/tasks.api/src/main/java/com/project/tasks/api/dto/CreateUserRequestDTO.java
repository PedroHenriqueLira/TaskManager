package com.project.tasks.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;

    @Size(min=6, max = 16, message = "Password deve ter no máximo entre 6 e 16 caracteres.")
    @NotBlank(message = "Password é obrigatória")
    private String password;

    @NotBlank(message = "Documento é obrigatório")
    @Size(min = 11, max = 20, message = "Documento deve ter entre 11 e 20 caracteres")
    private String documento;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 250, message = "Endereço deve ter no máximo 250 caracteres")
    private String endereco;
}
