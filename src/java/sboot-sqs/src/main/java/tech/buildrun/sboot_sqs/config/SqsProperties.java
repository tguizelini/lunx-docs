package tech.buildrun.sboot_sqs.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propriedades de configuração do AWS SQS.
 * 
 * Usa propriedades padrão do Spring Cloud AWS conforme documentação oficial:
 * - spring.cloud.aws.region.static: Região AWS
 * - aws.sqs.queue-name: Nome da fila SQS
 * - aws.sqs.account-id: ID da conta AWS (necessário para construir URL da fila)
 * 
 * QUEUE URL:
 * - Endereço ESPECÍFICO de uma fila SQS
 * - Formato: https://sqs.{region}.amazonaws.com/{accountId}/{queueName}
 * - Exemplo: https://sqs.us-east-1.amazonaws.com/123456789012/minha-fila
 * - USO: Enviar mensagens para uma fila específica
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.sqs")
public class SqsProperties {

    /**
     * Nome da fila SQS.
     * Exemplo: minha-fila
     */
    private String queueName = "minha-fila";
    
    /**
     * ID da conta AWS.
     * Necessário para construir a URL completa da fila.
     * Exemplo: 123456789012
     */
    private String accountId;
    
    /**
     * Região AWS onde a fila está localizada.
     * Lê de spring.cloud.aws.region.static (padrão: us-east-1)
     * Exemplo: us-east-1
     */
    @Value("${spring.cloud.aws.region.static:us-east-1}")
    private String region;

    /**
     * Retorna a URL completa da fila SQS usando o nome padrão configurado.
     * 
     * @return URL formatada da fila SQS padrão
     */
    public String getQueueUrl() {
        return getQueueUrl(queueName);
    }

    /**
     * Retorna a URL completa da fila SQS para um nome de fila específico.
     * 
     * A URL da fila segue o padrão da AWS:
     * https://sqs.{region}.amazonaws.com/{accountId}/{queueName}
     * 
     * @param queueName Nome da fila SQS
     * @return URL formatada da fila SQS
     */
    public String getQueueUrl(String queueName) {
        if (accountId == null || accountId.isEmpty()) {
            throw new IllegalStateException("AWS Account ID não configurado. Configure aws.sqs.account-id no application.properties");
        }
        
        return String.format("https://sqs.%s.amazonaws.com/%s/%s",
                region, accountId, queueName);
    }
}
