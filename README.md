# Spring Security Demo

- [Spring Security Demo](#spring-security-demo)
  - [Introduction](#introduction)
  - [Architecture](#architecture)
    - [Introduction](#introduction-1)
    - [main-api-servlet](#main-api-servlet)
    - [main-api-reactive](#main-api-reactive)
    - [microservice](#microservice)
    - [Diagram](#diagram)
  - [Resources](#resources)

## Introduction

This repository is created to demonstrate how to create a Spring Security application using Spring Boot 4.1.1 (Spring Framework 7.x, Spring Security 7.x) with OAuth 2.0 and custom security Authorization rules. The main focuses of this repository include the following:

- How to secure an API with OAuth 2.0
- How to customize Roles from the JWT token
- How to add custom Roles from another source
- How to pass the Authorization down to another microservice
- How to call another microservice using client credentials
- Authorization masking with `@HandleAuthorizationDenied` and `@AuthorizeReturnObject`
- Architectural verification with [Spring Modulith](https://docs.spring.io/spring-modulith/reference/)
- Utilizing only Spring to accomplish each task

## Architecture

### Introduction

This project contains 3 applications, two that represent the servlet and reactive approach to creating an API and one that is a basic secured microservice that the first two services call. All modules use Spring Modulith for architectural verification.

### main-api-servlet

This application is an API created using Spring Boot using a servlet approach. This application provides endpoints that provide demos including the following:

- RestClient and RestTemplate
- Method Level Security with `@PreAuthorize`
- Authorization Masking with `@HandleAuthorizationDenied` and `@AuthorizeReturnObject`
- Custom Security Filters
- Integration tests using the following:
  - MockMvc
  - MockRestServiceServer
  - MockServerRestClientCustomizer
  - WithMockUser

### main-api-reactive

This application is an API created using Spring Boot using a reactive approach. This application provides endpoints that provide demos including the following:

- WebClient
- Method Level Security with `@PreAuthorize`
- Manual Authorization Masking via `ReactiveSecurityContextHolder`
- Custom Security Filters
- Integration tests using the following:
  - OkHttp
  - WithMockUser

> **Note:** `@AuthorizeReturnObject` and `@HandleAuthorizationDenied` are not yet supported in Spring Security's reactive stack.
> Manual masking via `ReactiveSecurityContextHolder` is used as a workaround. See [spring-projects/spring-security#7594](https://github.com/spring-projects/spring-security/issues/7594) for tracking.

> **TODO:** OkHttp 5.5.0 is available as a further fast-follow upgrade (currently on 5.4.0). It introduces DNS/ECH-related changes that need to be verified against this module's `MockWebServer` usage in tests before upgrading. Flagged by reviewer as non-blocking.

### microservice

This application is a simple Spring Boot microservice using a reactive approach that contains one endpoint that is secured using OAuth 2.0. It demonstrates how to set it up and how to perform an integration test.

### Diagram

![spring-security-diagram](resources/plantuml/spring-security-diagram/Spring%20Security%20Diagram.png)

![spring-security-interaction-diagram](resources/plantuml/spring-security-interaction-diagram/Spring%20Security%20Interaction%20Diagram.png)

## Resources

- [Spring Boot 4.0 Reference Documentation](https://docs.spring.io/spring-boot/4.0/index.html)
- [Spring Framework 7.0 Reference Documentation](https://docs.spring.io/spring-framework/reference/7.0/index.html)
- [Spring Security 7.0 Reference Documentation](https://docs.spring.io/spring-security/reference/7.0/index.html)
- [Spring Modulith Reference Documentation](https://docs.spring.io/spring-modulith/reference/)