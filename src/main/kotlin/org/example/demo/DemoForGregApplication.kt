package org.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DemoForGregApplication

fun main(args: Array<String>) {
    runApplication<DemoForGregApplication>(*args)
}