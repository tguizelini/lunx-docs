package tech.buildrun.sboot_sqs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.sboot_sqs.dto.MyMessage;
import tech.buildrun.sboot_sqs.service.MessageSender;

/**
 * Controller REST para produção de mensagens SQS.
 * Não conhece detalhes de filas SQS - delega toda a lógica para o MessageSender.
 */
@RestController
@RequestMapping("producer")
@RequiredArgsConstructor
public class ProducerController {

    private final MessageSender messageSender;

    @PostMapping("send")
    public ResponseEntity sendMessage(@RequestBody MyMessage message) {
        messageSender.send(message);
        return ResponseEntity.ok().build();
    }
}
