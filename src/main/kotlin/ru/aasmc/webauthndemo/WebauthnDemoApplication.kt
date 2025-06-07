package ru.aasmc.webauthndemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class WebauthnDemoApplication

fun main(args: Array<String>) {
    runApplication<WebauthnDemoApplication>(*args)
}
