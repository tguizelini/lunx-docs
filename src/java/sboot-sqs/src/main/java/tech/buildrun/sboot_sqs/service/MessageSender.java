package tech.buildrun.sboot_sqs.service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.buildrun.sboot_sqs.config.SqsProperties;
import tech.buildrun.sboot_sqs.dto.MyMessage;

/**
 * Serviço responsável por enviar mensagens para filas SQS.
 * Encapsula a lógica de qual fila usar para cada tipo de mensagem.
 * O controller não precisa conhecer detalhes de filas SQS.
 */
@Service
@RequiredArgsConstructor
public class MessageSender {

    private final SqsTemplate sqsTemplate;
    private final SqsProperties sqsProperties;

    /**
     * Envia uma mensagem para a fila padrão configurada.
     * Este método encapsula a escolha da fila, mantendo o controller simples.
     * 
     * @param message Mensagem a ser enviada
     */
    public void send(MyMessage message) {
        String queueUrl = sqsProperties.getQueueUrl();
        sqsTemplate.send(queueUrl, message);
    }

    /**
     * Exemplo de método específico de negócio.
     * Em um sistema real, teríamos métodos como sendPaymentStatus(), sendOrderNotification(), etc.
     * Cada método sabe qual fila usar internamente.
     * 
     * @param message Mensagem a ser enviada
     */
    public void sendToDefaultQueue(MyMessage message) {
        send(message);
    }

    /**
     * Método privado auxiliar para enviar para uma fila específica.
     * Usado internamente pelos métodos públicos que encapsulam a lógica de negócio.
     * 
     * @param queueName Nome da fila de destino
     * @param message Mensagem a ser enviada
     */
    private void sendToQueue(String queueName, MyMessage message) {
        String queueUrl = sqsProperties.getQueueUrl(queueName);
        sqsTemplate.send(queueUrl, message);
    }
}
