# Task Manager — SonarQube Demo

## Sobre o projeto

Projeto didático de gestão de tarefas criado para demonstrar o uso do SonarQube na análise de qualidade de código. Não é um sistema de produção, o objetivo é mostrar, na prática, como o Sonar detecta diferentes categorias de problemas em um projeto real.

O projeto contém problemas intencionais de qualidade distribuídos entre o backend (Java/Spring Boot) e o frontend (Vue 3). Cada trecho problemático está marcado com o comentário `// SONAR-DEMO` para facilitar a localização durante a demonstração.

## Pré-requisitos

- Docker e Docker Compose
- Java 17 (apenas para rodar o backend localmente)
- Node.js 18+ (apenas para rodar o frontend localmente)
- Maven (apenas para o backend)

> O `sonar-scanner` roda dentro de um container. **Não é necessário instalar Java, Node, Maven ou o scanner-cli no host** para executar a análise — basta Docker.

## Como subir o ambiente

```bash
docker compose up -d mysql sonarqube
```

Sobe MySQL e SonarQube. O serviço `sonar-scanner` está no profile `scan` e só roda sob demanda (ver seção de análise).

Aguardar o SonarQube inicializar em [localhost:9000](http://localhost:9000) — pode levar cerca de 1 minuto.

Login padrão: `admin` / `admin` (o Sonar vai pedir para trocar no primeiro acesso).

Gerar token em **My Account → Security → Generate Token** e exportar:

```bash
export SONAR_TOKEN=seu_token_aqui
```

## Como rodar o backend

```bash
cd backend
mvn spring-boot:run
```

API disponível em `localhost:8080`.

## Como rodar o frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend disponível em `localhost:5173`.

## Como rodar a análise do SonarQube

O token nunca deve ser commitado. Use variável de ambiente (`SONAR_TOKEN`).

O scanner roda como container do próprio `docker-compose.yml` (serviço `sonar-scanner`, profile `scan`), no mesmo network do SonarQube. Por isso, o `SONAR_HOST_URL` interno é `http://sonarqube:9000` (não `localhost`).

### Frontend (via container, sem Node/Java no host)

Gerar cobertura antes (uma vez, no host — precisa Node) e disparar o scanner:

```bash
cd frontend
npm install
npm run test:coverage
cd ..

docker compose --profile scan run --rm sonar-scanner
```

O scanner lê `frontend/sonar-project.properties` e envia o resultado para o SonarQube na rede interna do compose.

### Backend (via Maven no host)

O plugin do Sonar para Maven faz o build + análise em um passo só, então roda no host:

```bash
cd backend
mvn clean verify sonar:sonar "-Dsonar.token=SONAR_TOKEN" "-Dsonar.host.url=http://localhost:9000"
```

> Se preferir não instalar Java/Maven no host, dá para usar a imagem `maven:3.9-eclipse-temurin-17` em um `docker run` apontando para o mesmo network do compose. Para a demo, manter o Maven local é mais simples.

## Derrubar o ambiente

```bash
docker compose down
```

Para apagar também os dados do Sonar e do MySQL:

```bash
docker compose down -v
```

## Problemas intencionais e o que cada um demonstra no Sonar

| Arquivo | Tipo de problema | O que o Sonar mostra | Tag SONAR-DEMO |
|---|---|---|---|
| `AuthService.java` | Log de senha em texto claro | Vulnerability / Security Hotspot | `log de senha` |
| `AuthService.java` | Comparação de String com `==` | Bug | `comparação com ==` |
| `UserService.java` | `Optional.get()` sem verificar presença | Bug | `Optional.get() sem verificar` |
| `TaskRepository.java` | Query com concatenação de String | Vulnerability | `concatenação em query` |
| `TaskService.java` | Método longo com muitas responsabilidades | Code Smell | `método longo` |
| `TaskService.java` | Número mágico sem constante nomeada | Code Smell | `número mágico` |
| `TaskService.java` | Lógica de prioridade duplicada | Code Smell / Duplication | `lógica duplicada` |
| `DashboardService.java` | Variáveis com nomes sem significado | Code Smell | `variáveis sem nome` |
| `TaskCommentService.java` | Bloco `catch` genérico ignorado silenciosamente | Code Smell / Bug | `catch genérico ignorado` |
| `DashboardService.java`, `TaskCommentService.java`, controllers | Classes sem nenhum teste | Coverage | `sem testes` |
| `TasksView.vue` | Componente com mais de 200 linhas | Code Smell | `múltiplas responsabilidades` |
| `TasksView.vue` | Função `formatDate` duplicada (já existe em `useDate.js`) | Duplication | `função de formatação duplicada` |
| `TasksView.vue`, `DashboardView.vue` | `console.log` esquecido | Code Smell | `console.log esquecido` |
| `TaskDetailView.vue` | Acesso a `task.category.name` sem verificar se `category` é null | Bug | `acesso sem verificação` |
| `authStore.js`, services Vue | Sem nenhum teste | Coverage | `store sem cobertura de testes` |
