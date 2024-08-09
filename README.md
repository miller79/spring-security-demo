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

This repository is created to demonstrate how to create a Spring Security application that uses OAuth 2.0 with custom security Authorization rules. The main focuses of this repository include the following:

- How to secure an API with OAuth 2.0
- How to customize Roles from the JWT token
- How to add custom Roles from another source
- How to pass the Authorization down to another microservice
- How to call another microservice using client credentials
- Utilizing only Spring to accomplish each task

## Architecture

### Introduction

This project contains 3 applications, two that represent the servlet and reactive approach to creating an API and one that is a basic secured microservice that the first two services call.

### main-api-servlet

This application is an API created using Spring Boot using a servlet approach. This application provides endpoints that provide demos including the following:

- RestClient and RestTemplate
- Method Level Security
- Custom Security Filters
- Integration tests using the following:
  - MockMvc
  - MockRestServiceServer
  - MockServerRestClientCustomizer
  - WithMockUser

### main-api-reactive

This application is an API created using Spring Boot using a reactive approach. This application provides endpoints that provide demos including the following:

- WebClient
- Method Level Security
- Custom Security Filters
- Integration tests using the following:
  - OkHttp
  - WithMockUser

### microservice

This application is a simple Spring Boot microservice using a reactive approach that contains one endpoint that is secured using OAuth 2.0. It demonstrates how to set it up and how to perform an integration test.

### Diagram

![spring-security-diagram](resources/plantuml/spring-security-diagram/Spring%20Security%20Diagram.png)

![spring-security-interaction-diagram](resources/plantuml/spring-security-interaction-diagram/Spring%20Security%20Interaction%20Diagram.png)

## Resources

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/index.html)
- [Spring Framework Reference Documentation](https://docs.spring.io/spring-framework/reference/index.html)
- [Spring Security Reference Documentation](https://docs.spring.io/spring-security/reference/index.html)