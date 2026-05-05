package com.example.demo.configs

import org.apache.catalina.connector.Connector
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TomcatConfig {
    @Bean
    fun servletContainerCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> =
        WebServerFactoryCustomizer { factory ->
            factory.addAdditionalTomcatConnectors(createHttpConnector())
        }

    private fun createHttpConnector(): Connector {
        val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
        connector.scheme = "http"
        connector.port = 8080
        connector.secure = false
        connector.redirectPort = 8443
        return connector
    }
}
