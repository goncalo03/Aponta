# Projeto: Aponta — Relatório para Defesa (Aplicação Android)

- **Autor:** (seu nome)
- **Curso:** (nome do curso)
- **Orientador:** (nome do orientador)
- **Data:** (data da defesa)

## Resumo

Este repositório contém a aplicação Android "Aponta" (pacote `com.ipca.aponta`). Trata‑se de uma aplicação móvel desenvolvida para (descrever objetivo principal: ex. gestão de apontamentos, localização de pontos de interesse, apoio a fichas de campo, etc.). O relatório documenta objetivos, arquitetura, metodologias, funcionalidades implementadas, resultados e orientações para a apresentação de defesa.

## Objetivos

- **Geral:** Entregar uma aplicação Android funcional que resolva o problema definido pelo enunciado (substituir pelo objetivo concreto do projeto).
- **Específicos:**
  - Implementar fluxo(s) principais da aplicação (ex.: autenticação, gestão de itens, visualização, sincronização).
  - Garantir usabilidade adequada em dispositivos móveis.
  - Documentar a arquitetura e as escolhas técnicas.

## Metodologia

- Desenvolvimento iterativo com pequenas entregas e testes manuais.
- Ferramentas: Android Studio, Kotlin, Gradle.
- Testes: testes manuais em emulador e dispositivo real; verificação de performance básica e usabilidade.

## Arquitetura do Sistema

- Aplicação Android organizada no módulo `app`.
- Principais camadas e componentes:
  - UI: Activities/Fragments ou Jetpack Compose para ecrãs e navegação.
  - Camada de domínio: lógica de negócios e casos de uso.
  - Persistência: armazenamento local (ex.: Room/SharedPreferences/files) e/ou sincronização remota se aplicável.
  - Recursos: imagens, strings e layouts em `res/`.

Diagrama (texto):

UI ↔ ViewModel/Controller ↔ Repositório ↔ (Local DB / API)

## Tecnologias e Bibliotecas

- Kotlin
- Android SDK
- Gradle
- (Adicionar bibliotecas específicas usadas: Jetpack, Retrofit, Room, Coroutines, etc.)

## Funcionalidades Principais (exemplos — ajuste conforme o projeto)

- Ecrã inicial com listagem de itens/registos
- Detalhe de item e ações CRUD (criar/editar/apagar)
- Pesquisa e filtragem
- Persistência local e sincronização (se aplicável)
- Configurações e gestão de conta (autenticação se existir)

Se a app tiver funcionalidades específicas (mapas, câmaras, sensores), substitua/adicione aqui.

## Demonstração para a Defesa

Roteiro sugerido (3–6 minutos):

1. Apresentar objetivo da aplicação e o problema que resolve.
2. Abrir a app e navegar pelo fluxo principal (ex.: criar e visualizar um registo).
3. Demonstrar funcionalidades chave (pesquisa, edição, sincronização, mapa, etc.).
4. Explicar uma decisão técnica relevante (ex.: escolha de arquitetura, persistência ou comunicação com backend).
5. Mostrar um desafio resolvido durante o desenvolvimento e o resultado.

Pontos a reforçar durante a defesa:

- Usabilidade e fluxo do utilizador
- Robustez e tratamento de erros
- Organização do código e separação de responsabilidades

## Resultados e Avaliação

- Estado atual: aplicação funcional com as features listadas acima (substituir por estado real: número de telas, features implementadas).
- Métricas observadas: tempos de resposta, utilização aproximada de memória, estabilidade em testes manuais.
- Limitações conhecidas: (indicar limitações como falta de testes automatizados, ausência de API, etc.)

## Conclusões

A aplicação demonstra a implementação dos requisitos principais e a capacidade de projetar um fluxo móvel consistente. O desenvolvimento permitiu consolidar conhecimentos em Kotlin, ciclo de vida Android e práticas de arquitetura.

## Trabalhos Futuros

- Implementar testes automatizados (unit/instrumentation)
- Melhorar experiência do utilizador (UI/UX) e acessibilidade
- Adicionar persistência remota ou autenticação, se aplicável
- Otimizações de performance e perfilamento

## Instruções para Executar (Resumo Rápido)

Requisitos: Android Studio e Android SDK compatível.

1. Abrir a pasta do projeto no Android Studio.
2. Sincronizar o Gradle.
3. Conectar um dispositivo Android ou abrir um emulador.
4. Executar a app a partir do Android Studio ou usar o Gradle:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

No Windows, use `gradlew.bat` em vez de `./gradlew`.

## Referências

- (Adicionar documentação, artigos e tutoriais usados)

## Anexos

- Incluir capturas de ecrã e vídeos da aplicação em `assets/docs/` ou `docs/`.

---

Notas: substitua as secções com exemplos por descrições concretas da sua app (funcionalidades reais, métricas e imagens). Se quiser, eu posso preencher automaticamente os campos com base no código do projeto — por exemplo extraindo o `applicationId` de `build.gradle.kts`, listando dependências usadas e inserindo capturas de ecrã encontradas no `res/`.

## Estrutura do projecto

- `app/` — módulo principal Android com código fonte, recursos e configuração do Gradle.
- `app/src/main/java/com/ipca/aponta/` — código fonte Kotlin (actividades, fragments, viewmodels, adaptadores).
- `app/src/main/res/` — recursos (layouts, drawables, strings, imagens).
- `app/build.gradle.kts` — configuração do módulo (dependências, applicationId).
- `gradle/` e ficheiros de configuração na raiz (`settings.gradle.kts`, `gradle.properties`) — configuração de build.

Organização proposta das packages (exemplo):

- `ui` — ecrãs e componentes de interface (`home`, `detail`, `settings`)
- `data` — fontes de dados, repositorios, mapeamentos
- `domain` — casos de uso e modelos de domínio
- `utils` — helpers e utilitários

## Lista de funcionalidades da aplicação

- Navegação entre ecrãs principais (lista, detalhe, criação/edição)
- CRUD de registos (criar, ler, atualizar, apagar)
- Pesquisa e filtragem de itens
- Persistência local (ex.: Room) e gestão de configurações
- Notificações locais ou sincronização (se aplicável)
- Autenticação e gestão de utilizadores (se implementado)

Adicione/ajuste esta lista com as funcionalidades concretas da sua app.

## Desenhos, esquemas e protótipos da aplicação

- Protótipos de ecrã: incluir imagens ou links para protótipos (Figma, imagens em `docs/` ou `assets/docs/`).
- Wireframes simples:

  - Ecrã Inicial: lista de registos + botão de ação (CRUD)
  - Ecrã Detalhe: campos do registo + ações (editar, eliminar)
  - Ecrã Configurações: opções do utilizador

Placeholder para imagens:

![Wireframe Home](docs/wireframe-home.png)  
![Wireframe Detalhe](docs/wireframe-detail.png)

Se não existir ainda, crie a pasta `docs/` e coloque aí os protótipos ou screenshots.

## Modelo de dados

Exemplo de modelo de dados (entidades principais):

- `Item` {
  - `id`: Long
  - `title`: String
  - `description`: String
  - `createdAt`: DateTime
  - `updatedAt`: DateTime
}

- `User` (se aplicável) {
  - `id`: Long
  - `username`: String
  - `email`: String
}

ER (texto):

`User` 1 — N `Item`

Sugestão: adicionar um diagrama ER em `docs/modelo-dados.png` ou gerar um `.drawio` para anexar.

## Implementação do projecto

- Padrão arquitetural: (ex.: MVVM com ViewModel + Repository)
- Fluxo de dados: UI → ViewModel → Repositório → Fonte de dados (Room / API)
- Principais classes/ficheiros a referir na defesa:
  - `MainActivity` / `NavHost` — pontos de entrada e navegação
  - `HomeScreen` / `HomeViewModel` — ecrã principal e lógica de apresentação
  - `Repository` / `Dao` — acesso a dados locais

Trechos técnicos que pode destacar:

- Como é feita a persistência local (Room / SharedPreferences)
- Como são tratados os erros e os estados de carregamento (Sealed classes / Resource wrappers)
- Estratégia de gestão de dependências e injeção (ex.: Hilt/Koin ou factory simples)

## Tecnologias usadas

- Linguagem: Kotlin
- IDE/Build: Android Studio, Gradle (Kotlin DSL)
- Bibliotecas (exemplos a listar concretamente):
  - Jetpack (Lifecycle, ViewModel, Navigation)
  - Room (persistência local)
  - Retrofit / OkHttp (comunicação HTTP, se houver)
  - Coroutines / Flow (concorrência)
  - Hilt ou Koin (injeção de dependências)

Consulte `app/build.gradle.kts` para a lista completa de dependências.

## Dificuldades

- Integração de bibliotecas e resolução de conflitos de dependências.
- Garantir comportamento consistente entre emulador e dispositivo real.
- Implementação de sincronização remota (se aplicável) e gestão de situações offline.
- Tempo limitado para polir UI/UX e escrever testes automatizados.

Descreva aqui problemas concretos que surgiram no projeto e como foram resolvidos.

## Conclusões

- O projecto demonstra um fluxo de desenvolvimento Android completo: especificação, implementação, validação manual e documentação.
- Pontos fortes: arquitetura modular, funcionalidades principais implementadas, código organizado para manutenção.
- Pontos a melhorar: testes automatizados, maior cobertura de funcionalidades, polimento de UX e persistência remota.

---

Se quiser, faço agora uma verificação automática do código para preencher: `applicationId`, lista de dependências em `build.gradle.kts`, e procurar screenshots em `app/src/main/res/` para anexar ao `docs/`. Quer que proceda com isso?
