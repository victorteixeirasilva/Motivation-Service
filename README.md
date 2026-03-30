# Motivation Service - Microserviço de Engajamento e Performance Pessoal

**Converta metas esquecidas em ação diária com automações inteligentes de motivação, visão de futuro e retenção de usuários.**

## Introdução e Visão Geral

Em plataformas digitais, reter usuários ativos e engajados é um desafio constante. Muitas pessoas abandonam o uso do sistema após alguns dias, deixam tarefas acumularem e perdem conexão com seus objetivos de longo prazo. Esse cenário impacta diretamente indicadores de produto, receita recorrente e satisfação do cliente.

O **Motivation Service** resolve esse problema ao atuar como uma camada estratégica de engajamento dentro do ecossistema InEvolving. Ele centraliza automações motivacionais, gestão de sonhos do usuário e geração de Vision Board, transformando dados em estímulos práticos para aumentar disciplina, consistência e retorno à plataforma.

### Benefícios de negócio e produto

- **Aumento de retenção:** reativa usuários inativos com mensagens personalizadas e contexto emocional.
- **Maior recorrência de uso:** conecta tarefas, sonhos e motivação para criar hábito de acesso contínuo.
- **Experiência mais humana:** combina tecnologia e propósito, elevando percepção de valor do produto.
- **Escalabilidade operacional:** automatiza ações que, manualmente, seriam caras e lentas.
- **Base técnica robusta:** arquitetura moderna com APIs, segurança por token e integração entre serviços.

## Funcionalidades Principais

1. **Disparo de e-mails para tarefas atrasadas**
   - Identifica usuários ativos e verificados.
   - Consulta tarefas em atraso em serviços integrados.
   - Envia e-mails motivacionais dinâmicos com resumo das pendências.
   - **Valor agregado:** reduz procrastinação, melhora produtividade e aumenta taxa de retorno ao sistema.

2. **Reengajamento de usuários desconectados**
   - Localiza usuários ativos que estão offline há mais de um dia.
   - Seleciona sonhos do usuário e cria mensagem motivacional personalizada.
   - Envia e-mail com chamada para retorno imediato à plataforma.
   - **Valor agregado:** reativa usuários com comunicação de alto impacto emocional e contextual.

3. **Gestão completa de sonhos (CRUD)**
   - Cadastro de sonhos com informações estruturadas.
   - Edição e atualização com validação de autorização.
   - Exclusão segura com regras de integridade.
   - Consulta individual e listagem por usuário.
   - **Valor agregado:** organiza objetivos pessoais e cria uma base de dados rica para personalização.

4. **Geração automática de Vision Board**
   - Seleciona sonhos aleatórios de forma inteligente (até 100 itens).
   - Integra com serviço externo para gerar composição visual.
   - Retorna URL da imagem final para consumo no front-end.
   - **Valor agregado:** transforma metas em visualização concreta, aumentando foco e comprometimento.

5. **Segurança com validação de token**
   - Valida token em todos os endpoints sensíveis.
   - Bloqueia acesso não autorizado com resposta consistente.
   - **Valor agregado:** protege dados do usuário e reforça confiabilidade da solução.

6. **Integração entre microserviços com cache de autenticação**
   - Comunicação com serviços de API, tarefas, e-mail e gerador de vision board.
   - Reaproveitamento de tokens em cache para reduzir sobrecarga.
   - **Valor agregado:** melhor desempenho, menor latência e maior resiliência operacional.

## Tecnologias Utilizadas

### Linguagens e plataforma

- **Java 21**
- **SQL (MySQL)**

### Frameworks e ecossistema

- **Spring Boot 3.4.5**
- **Spring Web**
- **Spring Data JPA**
- **Spring Actuator**
- **Spring Cloud OpenFeign**
- **Spring Cloud Config Client**
- **Springdoc OpenAPI (Swagger UI)**

### Bibliotecas e utilitários

- **Lombok**
- **Java JWT (Auth0)**
- **MySQL Connector/J**

### Testes

- **Spring Boot Starter Test**
- **Mockito**
- **JUnit**
- **Rest-Assured**

### DevOps e infraestrutura

- **Maven Wrapper (`mvnw`)**
- **Docker**
- **Docker Compose**
- **GitHub Actions (CI/CD com build e deploy automatizados)**

### Por que essas escolhas técnicas fortalecem o projeto

- **Spring Boot + Spring Cloud:** acelera desenvolvimento de microserviços prontos para produção.
- **OpenFeign:** simplifica integração HTTP entre serviços, mantendo código limpo e manutenível.
- **JPA + MySQL:** persistência confiável para operações transacionais do domínio.
- **Docker + CI/CD:** padroniza ambientes e reduz risco em publicação.
- **OpenAPI/Swagger:** melhora comunicação técnica com times de produto, QA e clientes.

## Demonstração Visual

<!-- Para maximizar impacto em portfólio, adicione evidências visuais de uso real do sistema.

### Placeholders recomendados

- **Screenshot 1 - Documentação da API (Swagger)**
  - Inserir imagem em: `docs/images/swagger-overview.png`
  - Descrição sugerida: "Visão geral dos endpoints do Motivation Service."

- **Screenshot 2 - Fluxo de cadastro de sonhos**
  - Inserir imagem em: `docs/images/create-dream-flow.png`
  - Descrição sugerida: "Exemplo de requisição e resposta de criação de sonho."

- **Screenshot 3 - Vision Board gerado**
  - Inserir imagem em: `docs/images/vision-board-result.png`
  - Descrição sugerida: "Resultado visual produzido a partir dos sonhos do usuário."

- **GIF - Jornada ponta a ponta**
  - Inserir mídia em: `docs/gifs/end-to-end-demo.gif`
  - Descrição sugerida: "Do cadastro do sonho ao envio de motivação e geração do vision board."

- **Vídeo curto (YouTube/Loom)**
  - Link placeholder: `[Assistir demonstração completa](https://SEU-LINK-DEMO-AQUI)` -->

<!-- ### Exemplo de bloco Markdown para mídia -->

<!-- ```md -->
![Swagger do Motivation Service](docs/images/swagger-overview.png)
<!-- ![Vision Board gerado](docs/images/vision-board-result.png)
[Assistir demonstração completa](https://SEU-LINK-DEMO-AQUI) -->
<!-- ``` -->


## Contribuição

Contribuições são bem-vindas e fortalecem o projeto.

1. Faça um fork do repositório.
2. Crie uma branch de feature: `git checkout -b feature/minha-melhoria`
3. Commit suas alterações: `git commit -m "feat: descreva sua melhoria"`
4. Envie para o repositório remoto: `git push origin feature/minha-melhoria`
5. Abra um Pull Request com contexto técnico e evidências (prints/testes).

### Boas práticas para contribuir

- Manter padrão de código e nomenclatura do projeto.
- Incluir testes para mudanças críticas.
- Atualizar documentação quando houver alteração de comportamento.

## Licença

Este projeto está distribuído sob a licença **MIT**.  
Consulte o arquivo `LICENSE` para detalhes.

## Agradecimentos

- Time InEvolving, pela visão de produto orientada a impacto humano.
- Comunidade Spring, pela excelência no ecossistema Java moderno.
- Profissionais e mentores que reforçam a construção de software com propósito e resultado.

## Contato

**Autor:** Victor Teixeira Silva  
**LinkedIn:** [linkedin.com/in/victorteixeirasilva](https://www.linkedin.com/in/victor-teixeira-354a131a3/)  
**GitHub:** [github.com/victorteixeirasilva](https://github.com/victorteixeirasilva)  
**E-mail:** [victor.teixeira@inovasoft.tech](mailto:victor.teixeira@inovasoft.tech)

---

### Destaque para Portfólio

Este projeto demonstra domínio prático em:

- Arquitetura de microserviços com foco em valor de negócio
- Desenvolvimento backend robusto com Java e Spring
- Integração entre serviços com segurança e resiliência
- Automação de engajamento orientada por dados de comportamento
- Boas práticas de DevOps, documentação e escalabilidade

Se você busca um profissional capaz de unir **visão de produto**, **execução técnica de alto nível** e **mentalidade de resultado**, este projeto é uma prova concreta dessa combinação.
