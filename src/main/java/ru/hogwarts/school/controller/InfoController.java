package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InfoController {

    @Value("${server.port}")
    private Integer serverPort;

    @Operation(summary = "Получить порт сервера")
    @GetMapping("/getPort")
    public ResponseEntity<Integer> getPort() {
        return ResponseEntity.ok(serverPort);
    }

    @Operation(summary = "Получить сумму чисел от 1 до 1_000_000")
    @GetMapping("/sum")
    public Long calculateSum() {
//        long startStream = System.nanoTime();
//        Integer reduce = Stream
//                .iterate(1, a -> a + 1)
//                .limit(1_000_000)
//                .reduce(0, (a, b) -> a + b);
//        long endStream = System.nanoTime();
//
//        long streamTime = TimeUnit.NANOSECONDS.toMillis(endStream - startStream);
//        log.info("Время расчёта суммы : {}", streamTime);
//
//        return reduce;

        long startStream = System.nanoTime();
        long sum = LongStream.rangeClosed(1, 1_000_000)
                .parallel()
                .reduce(0, Long::sum);
        long endStream = System.nanoTime();

        long streamTime = TimeUnit.NANOSECONDS.toMillis(endStream - startStream);
        log.info("Время расчёта суммы : {}", streamTime);

        return sum;
    }
}
