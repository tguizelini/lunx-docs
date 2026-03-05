package tech.buildrun.sboot_sqs.consumer;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;
import tech.buildrun.sboot_sqs.dto.MyMessage;

/**
 * Consumer que escuta mensagens da fila SQS.
 * 
 * Segue o padrão da documentação oficial do Spring Cloud AWS:
 * - Usa property placeholder ${aws.sqs.queue-name} para ler o nome da fila do application.properties
 * - Usa queueNames conforme documentação oficial
 * - Auto-acknowledgement padrão (mensagem é removida automaticamente após processamento bem-sucedido)
 */
@Component
public class MyConsumer {

    @SqsListener(queueNames = "${aws.sqs.queue-name}")
    public void listen(MyMessage message) {
        System.out.println("SQS::Message received = " + message.content());
    }
}
