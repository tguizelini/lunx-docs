package br.com.lunx.sbootannotation.dto;

import br.com.lunx.sbootannotation.validator.pix.Pix;
import br.com.lunx.sbootannotation.validator.pix.validation.PixType;
import jakarta.validation.constraints.NotNull;

public record PixDTO(
    @NotNull
    @Pix(type = PixType.BACEN)
    String bacen,

    @Pix(type = PixType.EMAIL)
    String email,

    @Pix(type = PixType.CPF)
    String cpf
) {} 