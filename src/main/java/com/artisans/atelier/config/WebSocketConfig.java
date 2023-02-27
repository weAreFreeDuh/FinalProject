package com.artisans.atelier.config;
import lombok.RequiredArgsConstructor;
import com.artisans.atelier.Handler.ChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws/chat")
                .setAllowedOriginPatterns("http://*:9090", "http://*.*.*.*:9090")
                .withSockJS()
                .setClientLibraryUrl("http://localhost:9090/myapp/js/sock-client.js");
        //.withSockJS() 추가
        //setAllowedOrigins("*")에서 *라는 와일드 카드를 사용하면
        //보안상의 문제로 전체를 허용하는 것보다 직접 하나씩 지정해주어야 한다고 한다.
    }
}