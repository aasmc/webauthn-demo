package ru.aasmc.webauthndemo

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<WebauthnDemoApplication>().with(TestcontainersConfiguration::class).run(*args)
}
