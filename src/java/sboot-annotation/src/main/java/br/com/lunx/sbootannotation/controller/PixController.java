package br.com.lunx.sbootannotation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.lunx.sbootannotation.dto.PixDTO;
import br.com.lunx.sbootannotation.validator.pix.PixPattern;
import br.com.lunx.sbootannotation.validator.pix.validation.PixType;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/pix")
public class PixController {

    @PostMapping("/validate")
    public ResponseEntity<String> validatePixKey(
        @Valid @RequestBody PixDTO dto
    ) {
        return ResponseEntity.ok("Chaves válidas");
    }

    @GetMapping("/validate/{key}")
    public ResponseEntity<String> validatePathPixKey(
        @PixPattern(type = PixType.BACEN) @PathVariable String key
    ) {
        return ResponseEntity.ok("Chave PIX no Path param válida");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateQueryPixKey(
        //@PixPattern(type = PixType.EMAIL) @RequestParam String key
        @PixPattern @RequestParam("chavePix") String chavePix
    ) {
        return ResponseEntity.ok("Chave PIX no Query param válida");
    }
} 