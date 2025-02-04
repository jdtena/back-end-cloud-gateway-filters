package com.unir.gateway.decorator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.gateway.model.GatewayRequest;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;

/**
 * Este decorador maneja las peticiones PATCH del Gateway.
 * Extiende ServerHttpRequestDecorator y modifica la petición según sea necesario.
 * El método PATCH se usa típicamente para actualizaciones parciales de recursos.
 */
@Slf4j
public class PatchRequestDecorator extends ServerHttpRequestDecorator {

    private final GatewayRequest gatewayRequest;
    private final ObjectMapper objectMapper;

    public PatchRequestDecorator(GatewayRequest gatewayRequest, ObjectMapper objectMapper) {
        super(gatewayRequest.getExchange().getRequest());
        this.gatewayRequest = gatewayRequest;
        this.objectMapper = objectMapper;
    }

    /**
     * Sobrescribe el método para devolver PATCH como método HTTP.
     *
     * @return el método HTTP PATCH
     */
    @Override
    @NonNull
    public HttpMethod getMethod() {
        return HttpMethod.PATCH;
    }

    /**
     * Sobrescribe el método para construir y devolver la URI de la petición.
     *
     * @return la URI de la petición
     */
    @Override
    @NonNull
    public URI getURI() {
        return UriComponentsBuilder
                .fromUri((URI) gatewayRequest.getExchange().getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR))
                .build().toUri();
    }

    /**
     * Sobrescribe el método para devolver las cabeceras de la petición.
     *
     * @return las cabeceras HTTP
     */
    @Override
    @NonNull
    public HttpHeaders getHeaders() {
        return gatewayRequest.getHeaders();
    }

    /**
     * Sobrescribe el método para manejar el cuerpo de la petición PATCH.
     * Convierte el cuerpo a bytes usando ObjectMapper.
     *
     * @return un Flux de DataBuffer conteniendo el cuerpo de la petición
     */
    @Override
    @NonNull
    @SneakyThrows
    public Flux<DataBuffer> getBody() {
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        byte[] bodyData = objectMapper.writeValueAsBytes(gatewayRequest.getBody());
        DataBuffer buffer = bufferFactory.allocateBuffer(bodyData.length);
        buffer.write(bodyData);
        return Flux.just(buffer);
    }
}