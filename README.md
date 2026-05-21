# 🚗 Fipe Analyzer API

Uma API RESTful robusta desenvolvida em Java com Spring Boot para consulta, orquestração e análise de histórico de preços de veículos, consumindo dados da Tabela Fipe.

Este projeto foi construído com foco em boas práticas de Engenharia de Software, servindo como um **BFF (Backend For Frontend)** que consolida dados de APIs externas e fornece endpoints limpos e tipados para consumo web.

---

# 🛠️ Tecnologias e Ferramentas

- **Linguagem:** Java 17+
- **Framework:** Spring Boot 3
- **Banco de Dados:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Gerenciador de Dependências:** Maven
- **Testes de API:** Postman

---

# 🏗️ Arquitetura e Boas Práticas Aplicadas

O desenvolvimento desta API foi guiado por padrões de mercado e arquitetura limpa:

- **Padrão DTO (Data Transfer Object):**  
  Isolamento da camada de persistência da camada de apresentação, garantindo segurança (como a não exposição de senhas) e contratos de API estáveis.

- **Global Exception Handling (`@RestControllerAdvice`):**  
  Captura e padronização centralizada de erros, retornando respostas JSON estruturadas (RFC 7807) para falhas de validação ou de servidor.

- **Anti-Corruption Layer & Facade Pattern:**  
  Abstração da complexidade da API externa da Tabela Fipe. O endpoint de histórico orquestra múltiplas chamadas síncronas e devolve um único payload consolidado para o frontend.

- **Segurança e Identidade:**  
  Utilização de `UUID` como chave primária para entidades sensíveis (Usuários), mitigando ataques de enumeração (IDOR).

- **Princípios SOLID:**  
  Forte aplicação do SRP (Princípio da Responsabilidade Única) e DIP (Princípio da Inversão de Dependência) na separação entre Controllers, Services e Repositories.

- **Twelve-Factor App:**  
  Configurações e credenciais sensíveis injetadas via variáveis de ambiente.

---

# 🚀 Como Executar o Projeto Localmente

## 📋 Pré-requisitos

- Java 17 ou superior instalado
- PostgreSQL rodando localmente na porta `5432`
- Banco de dados chamado `fipe_analyzer`
- Maven instalado *(opcional, já que o projeto utiliza Maven Wrapper)*

---

## ▶️ Passos para execução

### 1. Clone o repositório

```bash
git clone https://github.com/Anterosuehtam/tabelafipe.git
```

### 2. Acesse a pasta do projeto

```bash
cd tabelafipe
```

### 3. Configure as variáveis de ambiente

Defina as seguintes variáveis na sua IDE ou sistema operacional:

```env
DB_USER=postgres
DB_PASSWORD=sua_senha
```

### 4. Execute a aplicação

#### Linux / macOS

```bash
./mvnw spring-boot:run
```

#### Windows

```bash
mvnw.cmd spring-boot:run
```

---

A aplicação estará disponível em:

```txt
http://localhost:8080
```

---

## 📍 Endpoints Principais

### 🚘 Veículos (Tabela Fipe)

**Listar marcas**
```http
GET /api/veiculos/{tipo}/marcas
```

Retorna as marcas de um tipo de veículo (`carros`, `motos` ou `caminhões`).

---

**Listar modelos por marca**
```http
GET /api/veiculos/{tipo}/{codigoMarca}/modelos?nome={filtro}
```

Retorna os modelos de uma marca, permitindo filtragem opcional via Query Params.

---

**Histórico de preços**
```http
GET /api/veiculos/{tipo}/{codigoMarca}/{codigoModelo}/historico
```

Orquestra a busca do histórico completo de preços de um veículo ao longo dos anos.

---

### 👤 Usuários

**Criar usuário**
```http
POST /api/usuarios
```

Cria um novo usuário no banco de dados.

---

# 👨‍💻 Autor

**Matheus Antero**

- GitHub: https://github.com/Anterosuehtam
- LinkedIn: https://www.linkedin.com/in/matheus-antero-/