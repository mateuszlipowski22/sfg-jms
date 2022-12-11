package guru.springframework.sfgjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgjms.config.JMSConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage(){
        System.out.println("I am sending a message");

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();

        jmsTemplate.convertAndSend(JMSConfig.MY_QUEUE, message);

        System.out.println("Message sent");
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReciveMessage() throws JMSException {
        System.out.println("I am sending a message");

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();

        Message receiveMsg = jmsTemplate.sendAndReceive(JMSConfig.MY_SEND_RDV_QUEUE, session -> {
            Message helloMassage = null;
            try {
                helloMassage = session.createTextMessage(objectMapper.writeValueAsString(message));
                helloMassage.setStringProperty("_type", "guru.springframework.sfgjms.model.HelloWorldMessage");

                System.out.println("Sending Message");

                return helloMassage;
            } catch (JsonProcessingException e) {
                throw new JMSException("boom");
            }
        });

        System.out.println(receiveMsg.getBody(String.class));
    }
}
