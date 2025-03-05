# creditas-backend-case-api

Este projeto é um simulador de empréstimo desenvolvido com Java 21 e Spring Boot, seguindo os princípios da Clean
Architecture. O projeto utiliza Docker e Docker Compose para facilitar o setup, a execução de testes (incluindo testes
de desempenho com Gatling) e a implantação em diferentes ambientes.

## Índice

- [Pré-requisitos](#pré-requisitos)
- [Arquivos de Configuração e Ambiente](#arquivos-de-configuração-e-ambiente)
- [Setup do Ambiente](#setup-do-ambiente)
- [Exemplos de Requisições](#exemplos-de-requisições)
- [Estrutura do Projeto e Decisões de Arquitetura](#estrutura-do-projeto-e-decisões-de-arquitetura)
- [Testes](#Testes)

## Pré-requisitos

- Docker version **27.5.0** (ou superior)
- Docker Compose
- (Opcional) Maven e Java 21 para desenvolvimento local

## Arquivos de Configuração e Ambiente

1. **Clone o Repositório:**

   ```bash
   git clone https://seu-repositorio.git
   cd seu-repositorio

Configuração do Ambiente:

**Crie uma Network docker com um Subnet Específico:**


Para garantir que seus containers se comuniquem ou com IPs fixos 
e/ou dentro de um intervalo específico, você pode criar uma network personalizada no Docker com um subnet definido. 
Siga os passos abaixo:


- Execute o seguinte comando no terminal para criar uma network chamada `proxy` com a faixa de IP `172.19.0.0/16`:

   ```bash
   docker network create --driver=bridge --subnet 172.19.0.0/16 proxy
   ```

**Configure as variáveis de ambiente:**

- Copie o arquivo de exemplo de ambiente `.env.example` para criar o arquivo de variáveis `.env`:

   ```
    cp env.example .env
   ```

- Copie o Docker Compose para a API:

   ```bash
   cp docker-compose.example.yml docker-compose.yml
   ```
  
- Copie o Docker Compose para o Gatling `(ferramenta de teste de carga)`:

   ```bash
   cp docker-compose.gatling.example.yml docker-compose.gatling.yml
   ```
  
- Edite o arquivo .env conforme necessário. Ele deve conter variáveis como:
  
    ```
    DOCKER_CREDITAS_API_CONTAINER_IP=                  // IP FIXO DA API DE CRÉDITO
    DOCKER_CONTAINER_CREDITAS_API_INTERNAL_PORT=      // PORTA INTERNA DA API DE CRÉDITO
    DOCKER_CREDITAS_API_CONTAINER_EXTERNAL_PORT=     // PORTA EXTERNA DA API DE CRÉDITO
    CREDITAS_SPRING_PORT=                           // PORTA DO SERVIDOR TOMCAT DA API DE CRÉDITO
    CREDITAS_API_SERVER_TOMCAT_THREADS_MAX=        // NÚMERO MÁXIMO DE THREADS DO SERVIDOR TOMCAT
    CREDITAS_API_ACTIVE_PROFILE=                  //  PERFIL DO SPRING ATIVO 
    ```

### Setup do Ambiente

Para que os testes do Gatling sejam executados com sucesso, é crucial que o container da API esteja em execução antes que o container do Gatling inicie seus testes.



**Build da Imagem da API:**

- Primeiro, construa a imagem da sua API utilizando o seguinte comando (baseado no seu Dockerfile):

   ```bash
   docker build -t case-creditas-api --progress=plain .
   ```
   O serviço da API está definido no arquivo `docker-compose.yml`. Ele é construído a partir do seu Dockerfile e exposto na rede conforme configurado.

**Suba Todos os Containers com Docker Compose:**

- Com a imagem da API construída, você pode iniciar o container da API definido no `docker-compose.yml` com:

   ```bash
   docker compose up -d
   ```
- Aṕos isso, você pode subir o container que irá realizar o teste de carga atráves do serviço de testes do Gatling,
lembrando que o container do gatling depende do container da API. 
Para iniciar apenas o container de teste de carga, execute:

  ```bash
  docker compose -f docker-compose.gatling.yml up --build
  ```
- Esse comando constrói a imagem dos testes (usando o Dockerfile ou o target configurado) e inicia o container, 
que utiliza a variável de ambiente `URL_SIMULATION`
para se conectar à API.

- > **Aviso:** Após subir os containers, verifique se eles estão em execução e se a API está acessível:

  ```bash
  docker ps
  docker network inspect proxy
  ```

## Exemplos de Requisições

### Simular Empréstimo

**Endpoint:**  
`POST /simulate-loan`

**Request Body (JSON):**

```json
{
  "loanAmount": 10000,
  "birthDate": "11/02/2004",
  "months": 12
}
```
Exemplo de cURL:
```
curl -X POST "http://localhost:8080/simulate-loan" \
  -H "Content-Type: application/json" \
  -d '{
        "loanAmount": 10000,
        "birthDate": "11/02/2004",
        "months": 12
      }'
```
Resposta Esperada (Exemplo):
```json
{
  "totalAmount": 10272.84,
  "monthlyInstallment": 856.07,
  "months": 12,
  "totalInterest": 272.84
}
```

### Exemplo de Requisição Inválida

**Endpoint:**  
`POST /simulate-loan`

**Request Body (JSON):**

```json
{
  "loanAmount": -5000,
  "birthDate": "31/12/2050",
  "months": 0
}
```
Exemplo de cURL:
```
curl -X POST "http://localhost:8080/simulate-loan" \
  -H "Content-Type: application/json" \
  -d '{
        "loanAmount": -5000,
        "birthDate": "31/12/2050",
        "months": 0
      }'
```
Resposta Esperada (Exemplo):
```json
{
  "months": "o campo months deve conter um número inteiro positivo",
  "birthDate": "o campo birthDate deve conter uma passada ou a data atual",
  "loanAmount": "o campo loanAmount deve conter um número positivo"
}
```
---
## Estrutura do Projeto e Decisões de Arquitetura

Este projeto foi desenvolvido seguindo os princípios da Clean Architecture, de modo que cada parte do sistema tem uma responsabilidade bem definida e é desacoplada das demais. A ideia central é isolar a lógica de negócio dos detalhes de infraestrutura, facilitando a manutenção e a evolução do sistema.

### Camada de Domínio

A camada de domínio contém a essência do negócio. Aqui residem as entidades, como a classe `Loan`, que representa um empréstimo, e as regras de negócio que definem como os cálculos financeiros devem ser realizados. Toda a lógica de cálculo, como a fórmula PMT para calcular a parcela mensal, é implementada utilizando `BigDecimal` com alta precisão, empregando `MathContext.DECIMAL128` para minimizar erros de arredondamento.

Dentro dessa camada, utilizamos padrões como:

#### Provider Pattern
O `BaseInterestRateProvider` é responsável por determinar a taxa de juros base a ser aplicada com base na a data de nascimento do cliente.
- **Como Funciona:**  
  O provider calcula a idade do cliente a partir da data de nascimento e, com base em faixas etárias definidas, retorna a taxa de juros anual apropriada 
  através do método `getBaseInterestRate`.
  - > Um cliente com 24 anos pode receber uma taxa de 5% ao ano, enquanto um cliente com 45 anos pode ter uma taxa diferente.
    - **Exemplo:**
    ```java
     import java.time.LocalDate;
    
     // Obtém a taxa base com base na idade
     BigDecimal baseAnnualRate = BaseInterestRateProvider.getBaseInterestRate(LocalDate.of(2004, 2, 11));
    ```
 
- **Vantagem:**  
  Centralizando a lógica de decisão em um único local, o provider garante consistência na aplicação das regras de negócio relacionadas à definição das taxas de juros. Se as faixas etárias ou as taxas precisarem ser alteradas, essa alteração é feita no provider sem que seja necessário modificar outras partes do sistema.

  
#### Factory Pattern

As fábricas centralizam a criação dos objetos necessários, encapsulando a lógica de instanciar a implementação correta de acordo com parâmetros dinâmicos. No projeto, temos duas fábricas principais:

- **InterestRateRuleFactory:**  
  Essa fábrica é responsável por criar a implementação correta da interface `InterestRateRule`. A interface `InterestRateRule` define o contrato para obter a taxa de juros anual, através de um método como `getAnnualRate()`.  
  Para encapsular as regras de juros concretas, o projeto possui implementações como a **FixedInterestRateRule**:

- > Vale destacar que a definição da taxa anual (`AnnualRate`) depende das implementações da interface `InterestRateRule`. 
O `InterestRateRuleProvider` obtém uma taxa base com base na idade do cliente, mas é a implementação concreta de `InterestRateRule` – por exemplo, 
a `FixedInterestRateRule` – que efetivamente calcula e retorna a taxa aplicada, podendo usar a taxa base inalterada ou ajustá-la conforme as regras de negócio específicas.



- > **FixedInterestRateRule: Implementação concreta de `InterestRateRule`:**  
      Esta implementação retorna uma taxa de juros fixa, ou seja, ela simplesmente retorna a taxa base calculada para o cliente (por exemplo, 5% ao ano para clientes até 25 anos).  
      Quando o cenário definido for FIXED, o `InterestRateRuleFactory` retorna uma instância de `FixedInterestRateRule`, garantindo que a taxa base seja aplicada sem modificações adicionais.


  - **Como Funciona:**  
    A fábrica recebe a taxa base (obtida a partir do `InterestRateRuleProvider`) e o cenário (FIXED ou VARIABLE). Se o cenário for FIXED, a fábrica cria e retorna uma instância de `FixedInterestRateRule`. Caso o cenário seja VARIABLE, a fábrica poderá retornar outra implementação que aplique ajustes ou fatores adicionais à taxa, conforme as regras do negócio.

    - Exemplo:
      ```java
      import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;

      //Retorna FixedInterestRateRule (taxa base sem alterações)
      InterestRateRule rule = InterestRateRuleFactory.getRule(baseAnnualRate, InterestRateScenario.FIXED);
      ```

---

- **PaymentCalculationStrategyFactory:**  
  Essa fábrica é responsável por selecionar e instanciar a estratégia de cálculo de parcelas correta com base no tipo de cálculo desejado (por exemplo, FIXED ou DECREASING).
  - > Para o cálculo fixo, ela retornará uma instância de `FixedPaymentCalculationStrategy`. Caso haja outra estratégia (como um cálculo de amortização decrescente), a fábrica poderá retornar a implementação correspondente, sem que o caso de uso precise ser alterado.
    - **Exemplo**:
    ```java
    import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
    
    // Retorna FixedPaymentCalculationStrategy (que usa da formula PMT de parcelas fixas)    
    PaymentCalculationStrategy strategy = PaymentCalculationStrategyFactory.getStrategy(CalculationType.FIXED);
    ```
       
- **Vantagem:**  
  Ao centralizar a lógica de criação, as fábricas permitem alterar ou estender a forma como os objetos são instanciados sem impactar outras partes do código. 
  Isso facilita a manutenção e a evolução do sistema, pois qualquer alteração nas regras de juros ou na estratégia de cálculo pode ser feita dentro das fábricas e nas suas implementações específicas, 
  sem modificar o fluxo de execução dos casos de uso.

---

#### Strategy Pattern
A interface `PaymentCalculationStrategy` define um contrato para o cálculo das parcelas mensais de um empréstimo. Essa interface possibilita a implementação de diferentes algoritmos de cálculo sem que o código que os utiliza precise conhecer os detalhes da lógica. Por exemplo:


- **FixedPaymentCalculationStrategy:**  
  Implementa o contrato definido pela interface para calcular a parcela fixa (usando a fórmula PMT). Essa implementação utiliza operações com `BigDecimal` e alta precisão (por exemplo, `MathContext.DECIMAL128`) para garantir resultados consistentes em cálculos financeiros.
  - **Exemplo:**
    ```java
    InterestRateRule rule = InterestRateRuleFactory.getRule(baseAnnualRate, scenario);
    PaymentCalculationStrategy strategy = PaymentCalculationStrategyFactory.getStrategy(calculationType);
    
    // Retorna a parcela mensal fixa
    BigDecimal monthlyPayment = strategy.calculateMonthlyPayment(BigDecimal.valueOf(10000), rule, 12);
    ```

- **Vantagem:**  
  Se, no futuro, for necessário mudar o algoritmo de cálculo (por exemplo, para um método com cálculo variável ou outra estratégia de cálculo), basta implementar outra classe que estenda `PaymentCalculationStrategy`, sem modificar a lógica dos casos de uso que a consomem.

---

> Esses padrões – Strategy, Factory e Provider – trabalham juntos para garantir o isolamento dos detalhes de implementação e permitir que mudanças sejam feitas com mínimo impacto no restante do código.
### Entidade Loan e o Fluxo da Simulação do Empréstimo

A entidade `Loan` representa o objeto central do negócio, encapsulando os dados essenciais de um empréstimo simulado: o valor do empréstimo, data de nascimento do cliente, número de parcelas, parcela mensal, valor total a ser pago e juros totais.

#### Fluxo da Simulação

O método estático `simulateLoan` é um **factory method** que centraliza toda a lógica de simulação do empréstimo, seguindo estes passos:

1. **Validação Inicial:**  
   O método começa verificando se a data de nascimento (`birthDate`) não está no futuro. Caso esteja, é lançada uma exceção, garantindo que apenas dados válidos sejam processados.
   - **Exemplo:**
     ```
     if (birthDate.isAfter(LocalDate.now())) {
               throw new IllegalArgumentException("A data de nascimento não pode ser futura.");
     }
     ```
2. **Determinação da Taxa Base:**  
   O método invoca o `BaseInterestRateProvider.getBaseInterestRate(birthDate)`, que calcula uma taxa base com base na idade do cliente. Essa taxa base serve como referência para as regras de juros.
   - **Exemplo:**
    ```java
     import java.time.LocalDate;
    
     // Obtém a taxa base com base na idade
     BigDecimal baseAnnualRate = BaseInterestRateProvider.getBaseInterestRate(LocalDate.of(2004, 2, 11));
    ```
3. **Seleção da Regra de Juros:**  
   Utilizando a taxa base e o parâmetro `scenario` (por exemplo, FIXED ou VARIABLE), o método chama a fábrica `InterestRateRuleFactory.getRule(...)`.
  - **Interface InterestRateRule:** Define o contrato para retornar a taxa anual de juros.
  - **FixedInterestRateRule:** Uma implementação típica que, no cenário FIXED, retorna a taxa base sem alterações adicionais.  
    Essa etapa garante que a lógica de ajuste ou modificação da taxa esteja encapsulada na regra apropriada, conforme as necessidades do negócio.
    - **Exemplo:**
      ```java
      import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;

      //Retorna FixedInterestRateRule (taxa base sem alterações)
      InterestRateRule rule = InterestRateRuleFactory.getRule(baseAnnualRate, InterestRateScenario.FIXED);
      ```
4. **Seleção da Estratégia de Cálculo:**  
   Com o parâmetro `calculationType` (por exemplo, FIXED ou DECREASING), o método chama a fábrica `PaymentCalculationStrategyFactory.getStrategy(...)`, que retorna uma implementação de:
  - **PaymentCalculationStrategy:** Uma interface que define o método para calcular a parcela mensal.
 
    - **Exemplo:**
    ```java
    import com.github.maxswellyoo.creditas.domain.enums.CalculationType;     
    PaymentCalculationStrategy strategy = PaymentCalculationStrategyFactory.getStrategy(CalculationType.FIXED);
    ```
5. **Cálculo da Parcela Mensal e Totais:**  
   O método chama `strategy.calculateMonthlyPayment(loanAmount, rule, months)` para calcular a parcela mensal com base no valor do empréstimo, na regra de juros e no número de parcelas.  
   Em seguida, multiplica a parcela mensal pelo número de meses para determinar o valor total a ser pago e calcula os juros totais como a diferença entre o total pago e o valor inicial do empréstimo.
   - **Exemplo:**
     ```java
     BigDecimal monthlyPayment = strategy.calculateMonthlyPayment(BigDecimal.valueOf(10000), rule, 12);
     
     BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(months));
     
     BigDecimal totalInterest = totalAmount.subtract(loanAmount);
     ```
6. **Criação da Entidade Loan:**  
   Finalmente, com todos os dados calculados, o método retorna uma nova instância de `Loan`, consolidando o resultado da simulação em um único objeto.
   - **Exemplo:**
     ```
     return new Loan(loanAmount, birthDate, months, monthlyPayment, totalAmount, totalInterest);
     ```
Essa abordagem centraliza toda a lógica de simulação do empréstimo, tornando o sistema modular, pois qualquer alteração nas regras de cálculo ou de juros é feita apenas nas implementações específicas e/ou nas fábricas.

---

## Camada de Aplicação

A camada de aplicação atua como intermediária entre a interface de usuário e o domínio, orquestrando o fluxo de trabalho para simular um empréstimo. O caso de uso `SimulateLoanUseCase` exemplifica essa função:

- Recebe os dados de entrada (valor do empréstimo, data de nascimento e número de parcelas) da camada de apresentação.
- Invoca o método `Loan.simulateLoan`, que integra as regras e estratégias definidas na camada de domínio.
- Persiste o empréstimo simulado por meio da interface `LoanGateway`.
- Retorna o objeto `Loan` com os cálculos (parcela mensal, total a pagar e juros) já realizados.

> Essa organização permite que os cálculos financeiros sejam delegados à camada de domínio, enquanto a persistência é abstraída por meio da implementação do gateway (LoanGateway). 
Dessa forma, o caso de uso permanece focado na orquestração do fluxo de dados, possibilitando que a implementação concreta do armazenamento seja modificada sem afetar a lógica de negócio.

---

## Camada de Infraestrutura

Nesta camada, conectamos o nosso domínio às tecnologias externas – basicamente, fazemos a “ponte” entre o que o negócio precisa e como os dados são persistidos e expostos. Vou explicar como cada parte foi pensada:

### Controllers
O **LoanController** é o ponto de entrada da nossa API. Ele recebe as requisições HTTP (no endpoint `POST /simulate-loan`), valida os dados de entrada (através do DTO `SimulateLoanRequest` com as anotações de validação) e encaminha as informações para o caso de uso `SimulateLoanUseCase`. Depois, converte o resultado (um objeto `Loan`) em um DTO de resposta (`SimulateLoanResponse`) usando o `LoanDTOMapper`. Essa abordagem deixa o controller focado apenas em lidar com a comunicação HTTP, sem misturar a lógica de negócio.

### Gateways
Para a persistência, usamos o **LoanRepositoryGateway**. Ele implementa a interface `LoanGateway`, que define um contrato simples para salvar um empréstimo simulado. No gateway, a responsabilidade é:
- Converter o objeto do domínio (`Loan`) em uma entidade de persistência (`LoanEntity`) por meio do `LoanEntityMapper`.
- Salvar essa entidade usando o repositório JPA (interface `LoanRepository`).
- Converter a entidade salva de volta para o objeto do domínio.
  Dessa forma, a aplicação que usa o gateway não precisa saber nada sobre o banco de dados ou sobre o JPA – ela só chama o método para salvar e recebe o resultado.

### Persistência
Na camada de persistência, temos:
- **LoanRepository:** uma interface que estende `JpaRepository`, responsável por oferecer operações CRUD para a entidade `LoanEntity`.
- **LoanEntity:** a classe mapeada para a tabela `LOAN`. Ela define todos os campos necessários (valor do empréstimo, data de nascimento, número de parcelas, parcela mensal, total pago e juros) e garante, através das anotações JPA, que os dados essenciais não sejam nulos.

### Fluxo Geral

1. **Recepção da Requisição:**  
   O controller recebe a requisição HTTP com os dados de simulação, que são validados e convertidos para um objeto `SimulateLoanRequest`.

2. **Processamento no Caso de Uso:**  
   O controller encaminha os dados para o caso de uso `SimulateLoanUseCase`. Esse caso de uso delega a lógica de cálculo à camada de domínio (utilizando padrões como Strategy, Factory e Provider) para simular o empréstimo e retorna um objeto `Loan` com todos os valores calculados.

3. **Persistência e Retorno:**  
   O objeto `Loan` obtido é passado para o gateway (`LoanRepositoryGateway`), que o converte em uma entidade de persistência (`LoanEntity`), o salva no banco de dados e reconverte a entidade salva de volta para um objeto do domínio. Esse objeto `Loan` é então retornado pelo caso de uso para o controller.

4. **Resposta ao Cliente:**  
   O controller recebe o objeto `Loan` do caso de uso, o transforma em um `SimulateLoanResponse` por meio do `LoanDTOMapper` e envia essa resposta ao cliente com status HTTP 201 (Created).



> Essa divisão deixa o sistema mais organizado, flexível e fácil de manter, já que qualquer alteração na forma de persistir dados ou na lógica de negócio pode ser feita de forma isolada, sem impactar as demais camadas.

### Decisões de Arquitetura

A decisão de adotar a Clean Architecture permitiu separar claramente as responsabilidades:
- **Domínio:** Centraliza as regras e cálculos financeiros, usando padrões de projeto (Strategy, Factory e Provider) para manter o código flexível e extensível.


- **Aplicação:** Orquestra a lógica de negócio e garante que os dados sejam transformados corretamente para a interface do usuário.


- **Infraestrutura:** Isola os detalhes técnicos (como acesso a banco de dados e configuração do servidor) da lógica central.


> Esta organização não só facilita os testes unitários e de integração em cada camada, mas também permite que o ambiente de produção seja construído e implantado de forma reprodutível usando Docker e Docker Compose, com configurações dinâmicas definidas por variáveis de ambiente.

Em resumo, a estrutura do projeto foi pensada para proporcionar alta manutenibilidade, escalabilidade e testabilidade, permitindo que cada parte evolua de forma independente sem impactar as demais.

---

## Testes

> Este projeto possui uma suíte abrangente de testes que garante a robustez e a performance do sistema. As estratégias de teste abrangem tanto testes unitários em cada camada quanto testes de integração que validam a comunicação entre os componentes.

### Testes Unitários

Os testes unitários foram desenvolvidos para validar a funcionalidade isolada de cada componente, garantindo que a lógica de negócio e a orquestração sejam implementadas corretamente.


- **Domínio:**  
  Aqui, testamos as regras de negócio e os cálculos financeiros. Um teste unitário importante valida que o sistema consegue executar 100.000 cálculos (por exemplo, utilizando a estratégia de cálculo com `BigDecimal` e `MathContext.DECIMAL128`) em menos de 1000ms. Esse teste assegura a performance e a precisão dos algoritmos financeiros.


- **Aplicação:**  
  Os casos de uso, como o `SimulateLoanUseCase`, são testados para confirmar que eles orquestram corretamente a simulação do empréstimo. Isso inclui a invocação do método estático do domínio e a delegação para a persistência via `LoanGateway`. Dessa forma, o teste garante que o fluxo de dados desde a entrada até a persistência está funcionando corretamente.


- **Infraestrutura:**
    - **Gateways:**  
      O `LoanRepositoryGateway` é testado de forma isolada para assegurar que ele converte os objetos do domínio em entidades de persistência e vice-versa, sem depender de uma conexão real com o banco de dados.

---

### Testes de Integração

Os testes de integração garantem que os diferentes componentes do sistema se comuniquem corretamente e que os fluxos completos funcionem como esperado:

- **Repositórios:**  
  São testados usando um banco de dados em memória (H2). Os testes verificam se o `LoanRepository` realiza corretamente a operação de salvar.

- **Controllers:**  
  Testes de integração com MockMvc simulam requisições HTTP para os endpoints (como o `POST /simulate-loan`), verificando todo o fluxo – desde a validação dos dados de entrada e a execução do caso de uso até a transformação em DTO e a resposta final ao cliente. Esses testes asseguram que a API esteja respondendo com o status e os dados corretos.

--- 
### Testes de Desempenho (Gatling)
- **Objetivo:** Garantir que a aplicação lide bem com alta volumetria requisições simultâneas.
- **O que é testado:**
    - A capacidade da API de processar requisições sob diferentes cargas, com fases de warm-up e de carga intensa.
    - Simulações definidas com o Gatling, que injetam usuários de forma gradual e constante, monitorando tempos de resposta e throughput.
- **Execução:**
    - Os testes Gatling são containerizados, permitindo sua execução sem depender de uma instalação local do Java ou Gatling.

