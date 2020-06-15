package com.sfwl.bh.config;

import com.google.gson.Gson;
import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/11 11:06
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) ->
                        Mono.defer(() -> Mono.just(swe.getResponse())).flatMap(response -> {
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            DataBuffer buffer = response.bufferFactory().wrap(new Gson().toJson(new BaseModel<>(ResultStatus.UNAUTHORIZED)).getBytes());
                            return response.writeWith(Mono.just(buffer)).doOnError(error -> DataBufferUtils.release(buffer));
                        })).accessDeniedHandler((swe, e) ->
                        Mono.defer(() -> Mono.just(swe.getResponse())).flatMap(response -> {
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            DataBuffer buffer = response.bufferFactory().wrap(new Gson().toJson(new BaseModel<>(ResultStatus.FORBIDDEN)).getBytes());
                            return response.writeWith(Mono.just(buffer)).doOnError(error -> DataBufferUtils.release(buffer));
                        })).and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/doLogin").permitAll() // 登录
                .pathMatchers("/doRegister").permitAll() // 注册
                .pathMatchers("/message/**").permitAll() // 验证码
                .pathMatchers("/user/resetPasswd").permitAll() // 重置密码
                .pathMatchers("/device/infoWs").permitAll()
                .pathMatchers("/bh/ws").permitAll()
                .pathMatchers("/test/**").permitAll()
                .anyExchange().authenticated()
                .and().build();
    }

}