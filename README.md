# Projeto: Jogos de Plataformas Móveis — Relatório para Defesa

- **Autor:** (seu nome)
- **Curso:** (nome do curso)
- **Orientador:** (nome do orientador)
- **Data:** (data da defesa)

## Resumo

Este projeto apresenta um jogo de plataformas desenvolvido para dispositivos móveis com o objetivo de aplicar conceitos de desenvolvimento Android, design de jogos e interação móvel. O relatório descreve objetivos, arquitetura, metodologias, principais funcionalidades, resultados alcançados e recomendações para a apresentação de defesa.

## Objetivos

- **Geral:** Desenvolver um protótipo de jogo de plataformas para Android, funcional e demonstrável.
- **Específicos:**
  - Implementar mecânicas de movimento, colisão e física simples.
  - Criar níveis e elementos gráficos coerentes com a jogabilidade.
  - Garantir que o jogo rode em dispositivos móveis com desempenho aceitável.
  - Documentar a arquitetura e as escolhas técnicas.

## Metodologia

- Ciclo de desenvolvimento iterativo: planeamento → implementação → testes → refinamento.
- Ferramentas: Android Studio, Kotlin, Gradle.
- Estratégia de teste: testes manuais em emuladores e em dispositivos reais, validação de performance e usabilidade.

## Arquitetura do Sistema

- Aplicação Android com estrutura modularizada no pacote `app`.
- Principais componentes:
  - Camada de apresentação: telas e UI (Jetpack Compose / Views conforme o projeto).
  - Lógica de jogo: classes responsáveis por mecânicas, física e estados do jogo.
  - Recursos: imagens, sons, níveis e configurações em `res/`.

Breve diagrama (texto):

App UI ↔ GameController ↔ PhysicsEngine

## Tecnologias e Bibliotecas

- Kotlin
- Android SDK
- Gradle
- (Listar outras libs usadas: ex. Jetpack, libs de áudio, etc.)

## Implementação — Funcionalidades Principais

- Movimentação do jogador (andar, pular)
- Detecção de colisões com plataformas e obstáculos
- Sistema de pontuação/vidas
- Múltiplos níveis (design básico)
- Menus: início, pausa, e fim de jogo

## Demonstração para a Defesa

Sugestões para apresentação prática (3–5 minutos):

1. Abrir a aplicação e mostrar o menu inicial.
2. Demonstrar jogabilidade: movimento, salto e reação a obstáculos.
3. Explicar rapidamente a arquitetura do código e uma ou duas decisões técnicas relevantes (ex.: porquê usar X abordagem para colisões).
4. Mostrar um problema que foi resolvido durante o desenvolvimento e como foi abordado.

Pontos a destacar ao avaliador:

- Robustez das mecânicas básicas
- Responsividade em dispositivo móvel
- Clareza do código e modularidade

## Resultados e Avaliação

- Estado atual: protótipo funcional com N níveis e mecânicas essenciais.
- Métricas observadas: taxa de frames média (se medida), uso de memória aproximado em testes.
- Limitações conhecidas: IA básica, número limitado de níveis, ausência de persistência avançada.

## Conclusões

O projeto demonstra a integração de conceitos de desenvolvimento Android e design de jogos, resultando num protótipo jogável que cumpre os objetivos principais. A experiência permitiu consolidar conhecimentos em programação Kotlin, ciclo de vida Android e otimização de desempenho para mobile.

## Trabalhos Futuros

- Adicionar níveis adicionais e sistema de progressão
- Implementar salvamento de estado (persistência)
- Melhorar física e IA dos inimigos
- Polir gráficos, animações e efeitos sonoros

## Instruções para Executar (Resumo Rápido)

Requisitos: Android Studio 2020+ e Android SDK compatível.

1. Abrir a pasta do projeto no Android Studio.
2. Sincronizar o Gradle.
3. Conectar um dispositivo Android ou usar um emulador.
4. Executar a app a partir do Android Studio ou usar o Gradle:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

No Windows, use `gradlew.bat` em vez de `./gradlew`.

## Referências

- (Listar artigos, tutoriais e documentação consultada)

## Anexos

- Incluir capturas de ecrã e vídeos da jogabilidade em `app/src/main/res/` ou numa pasta `assets/docs/`.

---

Se quiser, posso adaptar este relatório — por exemplo preenchendo os campos em falta (nome, orientador), adicionando métricas concretas, ou incluindo imagens/screenshots. Quer que eu adicione automaticamente imagens ou que eu faça commit do ficheiro agora?
