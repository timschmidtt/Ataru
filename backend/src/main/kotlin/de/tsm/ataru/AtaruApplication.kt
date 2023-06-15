package de.tsm.ataru

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AtaruApplication

fun main(args: Array<String>) {
    runApplication<AtaruApplication>(*args)
}
