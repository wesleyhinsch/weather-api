# 🌤 Weather API

> API de previsão do tempo com integração ao **OpenWeatherMap** e notificações via **Telegram Bot**, deployada automaticamente no **Google Cloud Run**.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green?logo=springboot)
![Google Cloud](https://img.shields.io/badge/Google%20Cloud-Run-blue?logo=googlecloud)
![Telegram](https://img.shields.io/badge/Telegram-Bot-26A5E4?logo=telegram)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-black?logo=githubactions)

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Funcionalidades](#-funcionalidades)
- [Endpoints da API](#-endpoints-da-api)
- [Bot do Telegram](#-bot-do-telegram)
- [Tokens e Credenciais](#-tokens-e-credenciais)
- [Configuração do Google Cloud](#-configuração-do-google-cloud)
- [Configuração do GitHub Actions](#-configuração-do-github-actions)
- [Como Rodar Localmente](#-como-rodar-localmente)
- [Deploy](#-deploy)
- [Estrutura do Projeto](#-estrutura-do-projeto)

---

## 📖 Sobre o Projeto

A **Weather API** é uma aplicação que fornece relatórios detalhados de previsão do tempo para cidades brasileiras. Os usuários podem se cadastrar via **Telegram Bot** e receber automaticamente o relatório do clima **todo dia às 8h da manhã**.

### 🔄 Fluxo Principal

```
Usuário abre o Telegram → Inicia o bot → Envia "Cidade - UF"
  → API cadastra o usuário
  → Consulta o OpenWeatherMap
  → Envia o relatório no Telegram
  → Todo dia às 8h envia atualização automática
```

---

## 🏗 Arquitetura

O projeto segue a **Arquitetura Hexagonal (Ports & Adapters)**:

```
┌─────────────────────────────────────────────────────┐
│                    DOMAIN                            │
│  ┌─────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │  Model   │  │    Ports     │  │    Service      │  │
│  └─────────┘  └──────────────┘  └────────────────┘  │
├─────────────────────────────────────────────────────┤
│                 INFRASTRUCTURE                       │
│                                                      │
│  Inbound Adapters          Outbound Adapters         │
│  ┌──────────────┐          ┌───────────────────┐     │
│  │ REST API     │          │ OpenWeatherMap     │     │
│  │ Telegram     │          │ Telegram Bot       │     │
│  │ Webhook      │          │ H2 Database        │     │
│  └──────────────┘          └───────────────────┘     │
└─────────────────────────────────────────────────────┘
```

### 🔄 Fluxo de Deploy

```
git push (main) → GitHub Actions → Build Docker → Push Artifact Registry → Deploy Cloud Run
```

---

## 🛠 Tecnologias

| Tecnologia | Uso |
|---|---|
| ☕ **Java 21** | Linguagem principal |
| 🍃 **Spring Boot 3.4.1** | Framework backend |
| 🗄 **H2 Database** | Banco de dados embarcado |
| 🌐 **OpenWeatherMap API** | Dados de previsão do tempo |
| 🤖 **Telegram Bot API** | Notificações e interação com usuário |
| 🐳 **Docker** | Containerização |
| ☁️ **Google Cloud Run** | Hospedagem serverless |
| 📦 **Artifact Registry** | Registro de imagens Docker |
| ⚙️ **GitHub Actions** | CI/CD automático |
| 📝 **Swagger/OpenAPI** | Documentação da API |

---

## ✨ Funcionalidades

- ✅ Consulta de previsão do tempo em tempo real por cidade/UF
- ✅ Relatório detalhado por período (manhã, tarde, noite)
- ✅ Detecção de chuva por período com volume em mm
- ✅ Bot do Telegram para cadastro e consulta
- ✅ Envio automático de relatório diário às 8h
- ✅ Gerenciamento de cidades cadastradas via Telegram
- ✅ Deploy automático a cada push via GitHub Actions
- ✅ Swagger UI para documentação e testes

---

## 🔌 Endpoints da API

### 🏥 Health Check

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/health` | Verifica se a API está no ar |

**Resposta:**
```json
{ "status": "UP" }
```

### 🌤 Previsão do Tempo

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/forecast/current?city={cidade}&uf={uf}` | Retorna o relatório do clima |

**Exemplo:** `GET /forecast/current?city=Morro Reuter&uf=RS`

**Resposta:**
```json
{
  "city": "Morro Reuter",
  "uf": "RS",
  "summary": "🌤 Previsão do tempo - Morro Reuter/RS\n📅 31/03/2026\n─────────────────────\n\n🌅 Manhã (6h-12h)\n  🌡 Média: 20°C (18°C ~ 22°C)\n  ☁️ nuvens dispersas\n\n☀️ Tarde (12h-18h)\n  🌡 Média: 27°C (25°C ~ 29°C)\n  ☁️ nublado\n\n🌙 Noite (18h-23h)\n  🌡 Média: 22°C (20°C ~ 24°C)\n  🌧 chuva leve\n  💧 Volume: 1.2mm\n─────────────────────\n🌡 Geral: 18°C ~ 29°C\n🌧 Chuva prevista: noite\n☂️ Leve um guarda-chuva!",
  "tempMin": 18.5,
  "tempMax": 29.3,
  "willRain": true,
  "periods": [...]
}
```

### 📋 Subscriptions

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/subscription` | Cadastra e envia o primeiro relatório |
| `GET` | `/subscription` | Lista todos os cadastros |
| `DELETE` | `/subscription/{id}` | Remove um cadastro |

### 🤖 Telegram Webhook

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/telegram/webhook` | Recebe mensagens do Telegram |

---

## 🤖 Bot do Telegram

**Bot:** [@hinsch_weather_report_bot](https://t.me/hinsch_weather_report_bot)

### Comandos

| Comando | Descrição |
|---|---|
| `/start` | Inicia o bot e mostra instruções |
| `/minhas` | Lista suas cidades cadastradas |
| `/remover {id}` | Remove uma cidade pelo ID |
| `Cidade - UF` | Cadastra uma cidade (ex: `Morro Reuter - RS`) |

### Exemplo de Interação

```
👤 Usuário: /start
🤖 Bot: ☀️ Bem-vindo ao Weather Report!
        Envie a cidade e UF para receber o relatório diário.
        Exemplo: Morro Reuter - RS

👤 Usuário: Rio de Janeiro - RJ
🤖 Bot: ✅ Cidade cadastrada! Você receberá o relatório todo dia às 8h.
        🌤 Previsão do tempo - Rio de Janeiro/RJ
        📅 31/03/2026
        ─────────────────────
        🌅 Manhã (6h-12h)
          🌡 Média: 26°C (24°C ~ 28°C)
          ☁️ nuvens dispersas
        ☀️ Tarde (12h-18h)
          🌡 Média: 32°C (30°C ~ 34°C)
          ☁️ céu limpo
        🌙 Noite (18h-23h)
          🌡 Média: 27°C (25°C ~ 29°C)
          ☁️ nublado
        ─────────────────────
        🌡 Geral: 24°C ~ 34°C
        ☀️ Sem previsão de chuva para hoje!
```

---

## 🔑 Tokens e Credenciais

O projeto utiliza **3 credenciais** que são configuradas como variáveis de ambiente:

### 1. 🌐 OpenWeatherMap API Key

| Item | Detalhe |
|---|---|
| **Variável** | `OPENWEATHERMAP_API_KEY` |
| **Onde obter** | https://home.openweathermap.org/api_keys |
| **Plano** | Gratuito (1.000 chamadas/dia) |
| **Uso** | Consultar dados de previsão do tempo |

**Como obter:**
1. Crie uma conta em https://home.openweathermap.org/users/sign_up
2. Acesse https://home.openweathermap.org/api_keys
3. Copie a API Key (pode levar até 2h para ativar)

### 2. 🤖 Telegram Bot Token

| Item | Detalhe |
|---|---|
| **Variável** | `TELEGRAM_BOT_TOKEN` |
| **Onde obter** | Telegram → @BotFather |
| **Plano** | Gratuito |
| **Uso** | Enviar e receber mensagens via Telegram |

**Como obter:**
1. Abra o Telegram e busque **@BotFather**
2. Envie `/newbot`
3. Escolha um nome e username para o bot
4. Copie o token fornecido

**Registrar Webhook (após deploy):**
```bash
curl "https://api.telegram.org/bot{TOKEN}/setWebhook?url=https://{SUA_URL}/telegram/webhook"
```

### 3. ☁️ Google Cloud Service Account Key

| Item | Detalhe |
|---|---|
| **Secret GitHub** | `GCP_SA_KEY` |
| **Onde obter** | Google Cloud Console |
| **Uso** | Autenticação do GitHub Actions com o Google Cloud |

**Como obter:**
```bash
# Criar service account
gcloud iam service-accounts create github-actions --display-name="GitHub Actions"

# Conceder permissões
gcloud projects add-iam-policy-binding {PROJECT_ID} \
  --member="serviceAccount:github-actions@{PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding {PROJECT_ID} \
  --member="serviceAccount:github-actions@{PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/artifactregistry.writer"

gcloud projects add-iam-policy-binding {PROJECT_ID} \
  --member="serviceAccount:github-actions@{PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/iam.serviceAccountUser"

# Gerar chave JSON
gcloud iam service-accounts keys create key.json \
  --iam-account=github-actions@{PROJECT_ID}.iam.gserviceaccount.com
```

> ⚠️ O conteúdo do `key.json` deve ser adicionado como secret `GCP_SA_KEY` no GitHub e o arquivo local deve ser **deletado** após isso.

---

## ☁️ Configuração do Google Cloud

### Pré-requisitos
- Conta no Google Cloud com billing ativo
- Google Cloud CLI instalado

### Passo a passo

**1. Criar projeto e vincular billing:**
```bash
gcloud init
gcloud billing projects link {PROJECT_ID} --billing-account={BILLING_ACCOUNT_ID}
```

**2. Habilitar APIs necessárias:**
```bash
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com
```

**3. Criar repositório Docker no Artifact Registry:**
```bash
gcloud artifacts repositories create weather-api \
  --repository-format=docker \
  --location=southamerica-east1 \
  --description="Weather API Docker images"
```

**4. Criar Service Account para GitHub Actions:**
> Veja a seção [Google Cloud Service Account Key](#3-️-google-cloud-service-account-key)

**5. Configurar variáveis de ambiente no Cloud Run (após primeiro deploy):**
```bash
gcloud run services update weather-api \
  --region=southamerica-east1 \
  --set-env-vars="OPENWEATHERMAP_API_KEY={SUA_KEY},TELEGRAM_BOT_TOKEN={SEU_TOKEN}"
```

**6. Registrar webhook do Telegram:**
```bash
curl "https://api.telegram.org/bot{TOKEN}/setWebhook?url=https://{URL_CLOUD_RUN}/telegram/webhook"
```

---

## ⚙️ Configuração do GitHub Actions

### Secrets necessários

Vá em **Settings → Secrets and variables → Actions** no repositório:

| Secret | Descrição |
|---|---|
| `GCP_SA_KEY` | Conteúdo do `key.json` da Service Account |

### Workflow

O arquivo [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml) executa automaticamente a cada push na branch `main`:

1. ✅ Checkout do código
2. ✅ Autenticação no Google Cloud
3. ✅ Build da imagem Docker (multi-stage)
4. ✅ Push da imagem para o Artifact Registry
5. ✅ Deploy no Cloud Run

---

## 🚀 Como Rodar Localmente

### Pré-requisitos
- Java 21
- Maven 3.9+

### 1. Clone o repositório
```bash
git clone https://github.com/wesleyhinsch/weather-api.git
cd weather-api
```

### 2. Configure as variáveis de ambiente

**Windows (PowerShell):**
```powershell
$env:OPENWEATHERMAP_API_KEY="sua-api-key"
$env:TELEGRAM_BOT_TOKEN="seu-bot-token"
```

**Linux/Mac:**
```bash
export OPENWEATHERMAP_API_KEY=sua-api-key
export TELEGRAM_BOT_TOKEN=seu-bot-token
```

### 3. Execute a aplicação
```bash
mvn spring-boot:run
```

### 4. Acesse
- 🌐 **API:** http://localhost:8080
- 📝 **Swagger:** http://localhost:8080/swagger-ui.html
- 🏥 **Health:** http://localhost:8080/health
- 🗄 **H2 Console:** http://localhost:8080/h2-console

---

## 🚀 Deploy

O deploy é **automático** a cada push na branch `main`:

```bash
git add .
git commit -m "sua alteração"
git push
```

### Acompanhar deploy
- **GitHub Actions:** https://github.com/wesleyhinsch/weather-api/actions
- **Cloud Run Console:** https://console.cloud.google.com/run?project=weather-api-deploy
- **Logs:** https://console.cloud.google.com/run/detail/southamerica-east1/weather-api/logs?project=weather-api-deploy

---

## 📁 Estrutura do Projeto

```
weather-api/
├── 📄 .github/
│   └── workflows/
│       └── deploy.yml              # CI/CD GitHub Actions
├── 📄 src/main/java/com/weather/api/
│   ├── 📦 domain/
│   │   ├── model/
│   │   │   ├── ForecastPeriod.java     # Previsão por período do dia
│   │   │   ├── Subscription.java       # Cadastro do usuário
│   │   │   └── WeatherForecast.java    # Relatório completo do dia
│   │   └── port/
│   │       └── WeatherProviderPort.java # Interface do serviço de clima
│   ├── 📦 infrastructure/
│   │   ├── adapter/
│   │   │   ├── inbound/rest/
│   │   │   │   ├── HealthController.java           # GET /health
│   │   │   │   ├── WeatherController.java          # GET /forecast/current
│   │   │   │   ├── SubscriptionController.java     # CRUD /subscription
│   │   │   │   └── TelegramWebhookController.java  # POST /telegram/webhook
│   │   │   └── outbound/
│   │   │       ├── persistence/
│   │   │       │   ├── SubscriptionEntity.java
│   │   │       │   ├── SubscriptionJpaRepository.java
│   │   │       │   └── SubscriptionPersistenceAdapter.java
│   │   │       ├── telegram/
│   │   │       │   └── TelegramAdapter.java        # Envio de mensagens
│   │   │       └── weather/
│   │   │           └── OpenWeatherMapAdapter.java   # Consulta OpenWeatherMap
│   │   └── config/
│   │       ├── BeanConfig.java                      # Beans do Spring
│   │       └── WeatherScheduler.java                # Cron diário às 8h
│   └── WeatherApplication.java                      # Main
├── 📄 src/main/resources/
│   └── application.yml              # Configurações
├── 📄 Dockerfile                    # Multi-stage build
├── 📄 pom.xml                       # Dependências Maven
└── 📄 README.md                     # Este arquivo
```

---

## 📊 Links Úteis

| Recurso | URL |
|---|---|
| 🌐 **API (Produção)** | https://weather-api-titbsv6ozq-rj.a.run.app |
| 📝 **Swagger UI** | https://weather-api-titbsv6ozq-rj.a.run.app/swagger-ui.html |
| 🤖 **Bot Telegram** | https://t.me/hinsch_weather_report_bot |
| ⚙️ **GitHub Actions** | https://github.com/wesleyhinsch/weather-api/actions |
| ☁️ **Cloud Run Console** | https://console.cloud.google.com/run?project=weather-api-deploy |
| 📦 **Artifact Registry** | https://console.cloud.google.com/artifacts?project=weather-api-deploy |

---

## 📄 Licença

Este projeto é de uso pessoal/educacional.

---

> Desenvolvido com ☕ e ☁️ por [Wesley Hinsch](https://github.com/wesleyhinsch)
