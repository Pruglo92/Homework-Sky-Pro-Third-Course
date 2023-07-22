package ru.hogwarts.school.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InfoController.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class InfoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${server.port}")
    private Integer serverPort;

    @Test
    @DisplayName("Тест на получение порта")
    public void testGetPort() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getPort"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(serverPort.toString()));
    }

    @Test
    @DisplayName("Тест на расчёт суммы чисел от 1 до 1_000_000")
    public void testCalculateSum() throws Exception {
        long expectedSum = LongStream.rangeClosed(1, 1_000_000).parallel().reduce(0, Long::sum);
        String expectedLogMessage = "Время расчёта суммы : ";
        ByteArrayOutputStream logOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(logOutput));

        mockMvc.perform(MockMvcRequestBuilders.get("/sum"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(expectedSum)));

        String logOutputString = logOutput.toString();
        assertTrue(logOutputString.contains(expectedLogMessage));
    }
}
