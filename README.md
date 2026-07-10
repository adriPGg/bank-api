# 🏦 Bank API

REST API desarrollada con Spring Boot para simular la gestión de un sistema bancario.

## 🚀 Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- BCrypt
- Maven
- Lombok

## ✨ Funcionalidades

- ✅ CRUD de usuarios
- ✅ Validación de datos
- ✅ Manejo global de excepciones
- ✅ Contraseñas cifradas con BCrypt
- ✅ Autenticación mediante JWT

## 📌 Próximas funcionalidades

- Roles (USER / ADMIN)
- Gestión de cuentas bancarias
- Transferencias
- Historial de movimientos
- Refresh Token
- Docker
- Tests

## 📂 Arquitectura

controller/
service/
repository/
entity/
dto/
config/
exception/

## 🔐 Autenticación

POST /auth/login

Devuelve un JWT válido durante 1 hora.

## 👨‍💻 Autor

Adrián Pérez
