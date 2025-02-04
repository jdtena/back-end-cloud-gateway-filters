package com.unir.gateway.decorator;

import com.unir.gateway.model.GatewayRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;

/**
 * Este decorador maneja las peticiones DELETE del Gateway.
 * Extiende ServerHttpRequestDecorator y modifica la petición según sea necesario.
 * El método DELETE se usa típicamente para eliminar recursos y normalmente no incluye cuerpo.
 */
@Slf4j
public class DeleteRequestDecorator extends ServerHttpRequestDecorator {

    private final GatewayRequest gatewayRequest;

    public DeleteRequestDecorator(GatewayRequest gatewayRequest) {
        super(gatewayRequest.getExchange().getRequest());
        this.gatewayRequest = gatewayRequest;
    }

    /**
     * Sobrescribe el método para devolver DELETE como método HTTP.
     *
     * @return el método HTTP DELETE
     */
    @Override
    @NonNull
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }

    /**
     * Sobrescribe el método para construir y devolver la URI de la petición.
     * Incluye los parámetros de consulta si existen.
     *
     * @return la URI de la petición
     */
    @Override
    @NonNull
    public URI getURI() {
        return UriComponentsBuilder
                .fromUri((URI) gatewayRequest.getExchange().getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR))
                .queryParams(gatewayRequest.getQueryParams())
                .build()
                .toUri();
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
     * Sobrescribe el método para manejar el cuerpo de la petición DELETE.
     * Como DELETE normalmente no tiene cuerpo, devolvemos un Flux vacío.
     *
     * @return un Flux vacío de DataBuffer
     */
    @Override
    @NonNull
    public Flux<DataBuffer> getBody() {
        return Flux.empty();
    }
}