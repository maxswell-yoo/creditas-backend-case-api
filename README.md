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

**Clone o Repositório:**

- ```bash
   git clone https://github.com/maxswell-yoo/creditas-backend-case-api.git
   cd creditas-backend-case-api
  ```
Configuração do Ambiente:

**Crie uma Network docker com um Subnet Específico:**


Para garantir que seus containers se comuniquem ou com IPs fixos 
e/ou dentro de um intervalo específico, você pode criar uma network personalizada no Docker com um subnet definido. 
Siga os passos abaixo:


- Execute o seguinte comando no terminal para criar uma network chamada `proxy` com a faixa de IP `172.19.0.0/16`:

   ```bash
   docker network create --driver=bridge --subnet 172.19.0.0/16 proxy
   ```
> **Aviso:** Verifique se você já não tem uma network com o mesmo nome ou na mesma faixa de IP
   
**Configure as variáveis de ambiente:**

- Copie o arquivo de exemplo de ambiente `.env.example` para criar o arquivo de variáveis `.env`:

   ```
    cp .env.example .env
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
    # DOCKER ENV
    DOCKER_CREDITAS_API_CONTAINER_IP=172.19.0.10          # IP fixo para o container da API de crédito (necessário para comunicação interna)
    DOCKER_CREDITAS_API_CONTAINER_EXTERNAL_PORT=8080       # Porta externa mapeada para a API de crédito
    DOCKER_CONTAINER_CREDITAS_API_INTERNAL_PORT=8080         # Porta interna da API de crédito

    # SPRING ENV
    CREDITAS_SPRING_PORT=8080                              # Porta do servidor Tomcat da API de crédito
    CREDITAS_API_SERVER_TOMCAT_THREADS_MAX=200             # Número máximo de threads do servidor Tomcat
    CREDITAS_API_MAIL_HOST=smtp.example.com                # Host do servidor SMTP para envio de e-mails
    CREDITAS_API_MAIL_PORT=587                             # Porta do servidor SMTP
    CREDITAS_API_MAIL_USERNAME=user@example.com            # Usuário do servidor SMTP
    CREDITAS_API_MAIL_PASSWORD=password                    # Senha do servidor SMTP
    CREDITAS_API_MAIL_ENABLED=false                        # Flag para ativar ou desativar o envio de e-mail
    ```
- Observações:
   > Para testar com Gatling, é fundamental que a variável `CREDITAS_API_MAIL_ENABLED` esteja definida como false, evitando que o envio de e-mails interfira nos testes de desempenho.
    
   > O container da API necessita de um IP fixo para facilitar a comunicação interna(por exemplo, pelo container do Gatling), 
   > enquanto o container do Gatling não precisa obrigatoriamente de um IP fixo.
  > No entanto, se desejar, você pode configurá-lo no seu arquivo `docker-compose.gatling.yml`.    

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
- Esse comando constrói a imagem dos testes (usando o Dockerfile e o target configurado) e inicia o container, 
que utiliza a variável de ambiente `URL_SIMULATION` para se conectar à API.
- > **Observação:** ao executar o container do gatling os testes de carga já se iniciam, e ao finalizar os testes o container é finalizado.
>**Aviso:** Se estiver usando linux ou qualquer sub-system, e deseja ver os logs dos testes de carga, então será necessário executar o seguinte comando:

- ```bash
  sudo chown -R $(whoami):$(whoami) ./gatling-reports
  ```

---
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
  "months": 12,
  "email": "cliente@exemplo.com",
  "currency": "BRL"
}
```
Exemplo de cURL:
```
curl -X POST "http://localhost:8080/simulate-loan" \
  -H "Content-Type: application/json" \
  -d '{
        "loanAmount": 10000,
        "birthDate": "11/02/2004",
        "months": 12,
        "email": "cliente@exemplo.com",
        "currency": "BRL"
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
  "months": 0,
  "email": "invalido",
  "currency": null
}
```
Exemplo de cURL:
```
curl -X POST "http://localhost:8080/simulate-loan" \
  -H "Content-Type: application/json" \
  -d '{
        "loanAmount": -5000,
        "birthDate": "31/12/2050",
        "months": 0,
        "email": "invalido",
        "currency": null
      }'
```
Resposta Esperada (Exemplo):
```json
{
  "loanAmount": "o campo loanAmount deve conter um número positivo",
  "birthDate": "o campo birthDate deve conter uma passada ou a data atual",
  "months": "o campo months deve conter um número inteiro positivo",
  "email": "o campo deve ter o formato de e-mail",
  "currency": "o campo currency não pode ser vazio"
}
```
---
## Estrutura do Projeto e Decisões de Arquitetura

Este projeto foi desenvolvido seguindo os princípios da Clean Architecture, de modo que cada parte do sistema tem uma responsabilidade bem definida e é desacoplada das demais. A ideia central é isolar a lógica de negócio dos detalhes de infraestrutura, facilitando a manutenção e a evolução do sistema.

### Camada de Domínio

A camada de domínio contém a essência do negócio. Aqui residem as entidades, como a classe `Loan`, que representa um empréstimo, e as regras de negócio que definem como os cálculos financeiros devem ser realizados. Toda a lógica de cálculo, como a fórmula PMT para calcular a parcela mensal, é implementada utilizando `BigDecimal` com alta precisão, empregando `MathContext.DECIMAL128` para minimizar erros de arredondamento.

Dentro dessa camada, utilizamos padrões como:

#### Provider Pattern
Nesse design pattern, temos o `BaseInterestRateProvider` que é responsável por determinar a taxa de juros base a ser aplicada com base na a data de nascimento do cliente.
- **Como Funciona:**  
  O provider calcula a idade do cliente a partir da data de nascimento e, com base em faixas etárias definidas, retorna a taxa de juros anual apropriada 
  através do método `getBaseInterestRate`.
  - > Um cliente com 24 anos pode receber uma taxa de 5% ao ano, enquanto um cliente com 45 anos tem uma taxa de 2% ao ano.
    - **Exemplo:**
    ```java
     import java.time.LocalDate;
    
     // Obtém a taxa base com base na idade
     BigDecimal baseAnnualRate = BaseInterestRateProvider.getBaseInterestRate(LocalDate.of(2004, 2, 11));
    ```
 ---

O `BaseCurrencyConversionRateProvider` é outro exemplo, ele é responsável por determinar a taxa de conversão entre duas moedas com base em regras pré-definidas.
- **Como Funciona:**  
  Esse provider mantém uma lista de regras de conversão, onde cada regra associa uma moeda de origem a uma moeda de destino e define uma taxa de conversão fixa. Quando o método `getConversionRate` é chamado, ele verifica qual regra se aplica à conversão solicitada e retorna a taxa correspondente.
    - > Por exemplo, para converter de USD para BRL, a regra pode definir uma taxa de 5.0. Já para converter de EUR para BRL, a taxa pode ser 6.0.
        - **Exemplo:**
      ```java
      import com.github.maxswellyoo.creditas.domain.enums.Currency;
      import java.math.BigDecimal;
  
      // Obtém a taxa de conversão para transformar USD em BRL
      BigDecimal conversionRate = BaseCurrencyConversionRateProvider.getConversionRate(Currency.USD, Currency.BRL);
      ```
---
- **Vantagem:**  
  Centralizando a lógica de decisão em um único local, o provider garante consistência na aplicação das regras de negócio relacionadas à definição das taxas de juros e conversão de moedas. Se as faixas etárias ou as taxas precisarem ser alteradas, essa alteração é feita no provider sem que seja necessário modificar outras partes do sistema.

  
#### Factory Pattern

As fábricas centralizam a criação dos objetos necessários, encapsulando a lógica de instanciar a implementação correta de acordo com parâmetros dinâmicos. No projeto, temos duas fábricas principais:

- **InterestRateRuleFactory:**  
  Essa fábrica é responsável por criar a implementação correta da interface `InterestRateRule`. A interface `InterestRateRule` define o contrato para obter a taxa de juros anual, através de um método chamado `getAnnualRate()`.  
  Para encapsular as regras de juros concretas, o projeto possui implementações como a **FixedInterestRateRule**:

- > Vale destacar que a definição da taxa anual (`AnnualRate`) depende das implementações da interface `InterestRateRule`, ou seja, o `InterestRateRuleProvider` obtém uma taxa base com base na idade do cliente, mas é a implementação concreta de `InterestRateRule` – por exemplo, 
a `FixedInterestRateRule` – que efetivamente calcula e retorna a taxa aplicada, podendo usar a taxa base inalterada ou ajustá-la conforme as regras de negócio específicas.

- > **FixedInterestRateRule: Implementação concreta de `InterestRateRule`:**  
      Esta implementação retorna uma taxa de juros fixa, ou seja, ela simplesmente retorna a taxa base calculada para o cliente (por exemplo, 5% ao ano para clientes até 25 anos).  
      
 - > Quando o cenário definido for FIXED, o `InterestRateRuleFactory` retorna uma instância de `FixedInterestRateRule`, garantindo que a taxa base seja aplicada sem modificações adicionais.


  - **Como Funciona `InterestRateRuleFactory`:**  
    A fábrica recebe a taxa base (obtida a partir do `InterestRateRuleProvider`) e o cenário (FIXED). Se o cenário for FIXED, a fábrica cria e retorna uma instância de `FixedInterestRateRule`. Caso o cenário seja VARIABLE, a fábrica poderá retornar outra implementação que aplique ajustes ou fatores adicionais à taxa, conforme as regras do negócio.

    - Exemplo:
      ```java
      import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;

      //Retorna FixedInterestRateRule (taxa base sem alterações)
      InterestRateRule rule = InterestRateRuleFactory.getRule(baseAnnualRate, InterestRateScenario.FIXED);
      ```
---
- **Como Funciona `CurrencyConversionStrategyFactory`:**  
  Outra fábrica do projeto é a `CurrencyConversionStrategyFactory`, Essa fábrica centraliza a criação de estratégias para conversão de moedas com base no tipo de conversão desejado. Ao receber um tipo (por exemplo, DEFAULT), a fábrica retorna uma instância concreta da interface `CurrencyConversionStrategy` que define como a conversão deve ser realizada. Por exemplo, se o tipo for DEFAULT, ela retorna uma instância de `DefaultCurrencyConversionStrategy`, que converte o valor usando a taxa fornecida sem aplicar ajustes adicionais. Caso novos tipos de conversão sejam necessários, basta registrar novas implementações na fábrica, mantendo o restante do sistema desacoplado das particularidades da conversão.

    - **Exemplo:**
      ```java
      import com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType;
      import com.github.maxswellyoo.creditas.domain.strategy.CurrencyConversionStrategy;
  
      // Retorna a estratégia DefaultCurrencyConversionStrategy, que aplica a conversão usando a taxa fornecida sem alterações adicionais.
      CurrencyConversionStrategy strategy = CurrencyConversionStrategyFactory.getStrategy(CurrencyConversionType.DEFAULT);
      ```

---

- **PaymentCalculationStrategyFactory:**  
  Essa fábrica é responsável por selecionar e instanciar a estratégia de cálculo de parcelas correta com base no tipo de cálculo desejado (por exemplo, FIXED).
  - > Para o cálculo fixo, ela retornará uma instância de `FixedPaymentCalculationStrategy`. Caso haja outra estratégia (como um cálculo de amortização decrescente), a fábrica poderá retornar a implementação correspondente.
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
Nesse design pattern, temos de exemplo a interface `PaymentCalculationStrategy` que define um contrato para o cálculo das parcelas mensais de um empréstimo. Essa interface possibilita a implementação de diferentes algoritmos de cálculo sem que o código que os utiliza precise conhecer os detalhes da lógica. Por exemplo:


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
- **DefaultCurrencyConversionStrategy:**  
  Outro exemplo, é a interface `CurrencyConversionStrategy` que define um contrato para converter um valor de uma moeda para outra utilizando uma taxa de conversão, permitindo a implementação de diferentes algoritmos sem que o código consumidor precise conhecer os detalhes internos.

  > Na implementação padrão, a classe `DefaultCurrencyConversionStrategy` realiza a validação dos valores de entrada para garantir que nem o valor a ser convertido nem a taxa de conversão sejam negativos, e em seguida multiplica o valor pela taxa de conversão para obter o resultado final. Caso os parâmetros sejam inválidos, uma `IllegalArgumentException` é lançada, evitando cálculos incorretos.
    - **Exemplo:**
      ```java
      // Suponha que usamos uma fábrica para obter a estratégia padrão
      CurrencyConversionStrategy conversionStrategy = CurrencyConversionStrategyFactory.getStrategy(CurrencyConversionType.DEFAULT);
      
      // Converte 100 unidades de uma moeda para outra utilizando uma taxa de conversão de 5.0
      BigDecimal convertedAmount = conversionStrategy.convert(BigDecimal.valueOf(100), BigDecimal.valueOf(5.0));
      
      // Resultado esperado: 100 * 5.0 = 500.0
      ```
- **Vantagem:**  
  Essa implementação garante que o processo de conversão seja realizado de forma consistente e segura, permitindo que o algoritmo de conversão seja alterado ou estendido sem impactar o restante do sistema.



> Esses padrões – Strategy, Factory e Provider – trabalham juntos para garantir o isolamento dos detalhes de implementação e permitir que mudanças sejam feitas com mínimo impacto no restante do código.
---
### Serviço de Conversão de Moedas e Fluxo da Conversão

 Utilizando os padrões acima, temos o serviço `CurrencyConversionService` que é responsável por gerenciar o fluxo de conversão um valor de uma moeda para outra, utilizando regras e estratégias definidas. O fluxo de conversão ocorre da seguinte forma:

- Primeiro, o método verifica se a moeda de origem e a moeda alvo são iguais. Se forem, ele retorna o valor original sem realizar conversão.
  - **Exemplo:**
    ```
    if (fromCurrency.equals(targetCurrency)) {
            return amount;
        }
    ```
- Caso contrário, o serviço obtém a taxa de conversão chamando o método `getConversionRate` do `BaseCurrencyConversionRateProvider`, que percorre um conjunto de regras para determinar a taxa apropriada entre as moedas solicitadas.
  - **Exemplo:**
    ```java
    BigDecimal conversionRate = BaseCurrencyConversionRateProvider.getConversionRate(fromCurrency, targetCurrency);
    ```
- Em seguida, o serviço utiliza a `CurrencyConversionStrategyFactory` para obter a estratégia de conversão adequada com base no tipo de conversão desejado (por exemplo, `DEFAULT`).
  - **Exemplo:**
    ```java
    CurrencyConversionStrategy conversionStrategy = CurrencyConversionStrategyFactory.getStrategy(currencyConversionType);
    ```
- Por fim, o método chama o método `convert` da estratégia selecionada, passando o valor original e a taxa de conversão, e retorna o valor convertido.
  - **Exemplo:**
      ```
      return conversionStrategy.convert(amount, conversionRate);
      ```
Essa abordagem torna o processo de conversão modular e extensível, permitindo que alterações na lógica de conversão ou a inclusão de novas moedas sejam realizadas de forma isolada, sem impactar as demais funcionalidades do sistema.

---
### Entidade Loan e o Fluxo da Simulação do Empréstimo

Ainda na camada de domínio, temos a entidade `Loan` que representa o objeto central do negócio, encapsulando os dados essenciais de um empréstimo simulado: o valor do empréstimo, data de nascimento do cliente, número de parcelas, parcela mensal, email do cliente,
valor total a ser pago, juros totais e moeda .

#### Fluxo da Simulação

 O fluxo começa com o método estático `simulateLoan` que é um **factory method** que centraliza toda a lógica de simulação do empréstimo, seguindo estes passos:

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
   Utilizando a taxa base e o parâmetro `scenario` (por exemplo, FIXED), o método chama a fábrica `InterestRateRuleFactory.getRule(...)`.
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
   Com o parâmetro `calculationType` (por exemplo, FIXED), o método chama a fábrica `PaymentCalculationStrategyFactory.getStrategy(...)`, que retorna uma implementação de:
  - **PaymentCalculationStrategy:** Uma interface que define o método para calcular a parcela mensal.
 
    - **Exemplo:**
    ```java
    import com.github.maxswellyoo.creditas.domain.enums.CalculationType;     
    PaymentCalculationStrategy strategy = PaymentCalculationStrategyFactory.getStrategy(CalculationType.FIXED);
    ```
5. **Cálculo da Parcela Mensal e Totais:**  
   logo em seguida, o método chama `strategy.calculateMonthlyPayment(loanAmount, rule, months)` para calcular a parcela mensal com base no valor do empréstimo, na regra de juros e no número de parcelas.  
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
      return new Loan(loanAmount, birthDate, months, monthlyPayment, totalAmount, totalInterest, email, fromCurrency);
     ```
Essa abordagem centraliza toda a lógica de simulação do empréstimo, tornando o sistema modular, pois qualquer alteração nas regras de cálculo ou de juros é feita apenas nas implementações específicas e/ou nas fábricas.

---

## Camada de Aplicação

A camada de aplicação atua como intermediária entre a interface do usuário e o domínio, coordenando o fluxo de trabalho para simular um empréstimo e disparar notificações. Exemplo disso, é o caso de uso `SimulateLoanUseCase`, que realiza as seguintes operações:

- Recebe os dados de entrada: valor do empréstimo, data de nascimento, número de parcelas, e-mail do cliente e a moeda em que o valor foi informado.
- Converte o valor do empréstimo para a moeda base (BRL) utilizando o serviço de conversão de moedas, garantindo que os cálculos financeiros sejam realizados de forma consistente.
- Invoca o método `Loan.simulateLoan`, que utiliza regras e estratégias definidas na camada de domínio para calcular os valores do empréstimo (como parcela mensal, total a pagar e juros).
- Persiste o empréstimo simulado por meio da interface `LoanGateway`, abstraindo a implementação concreta de armazenamento.
- Envia uma notificação por e-mail com os resultados da simulação utilizando o `EmailGateway`.
- Retorna o objeto `Loan` persistido, que contém todos os valores calculados.

> Essa organização permite que a conversão de moedas e os cálculos financeiros sejam delegados à camada de domínio, enquanto a persistência e o envio de notificações são tratados pelos respectivos gateways. Assim, o caso de uso se concentra na orquestração do fluxo de dados, permitindo que alterações na implementação de persistência ou na lógica de envio de e-mails sejam realizadas sem impactar a lógica de negócio.

---

## Camada de Infraestrutura

Nesta camada, conectamos o nosso domínio às tecnologias externas – basicamente, fazemos a “ponte” entre o que o negócio precisa, além de definir como os dados são persistidos e expostos. Vou explicar como cada parte foi pensada:

### Controllers
O **LoanController** é o ponto de entrada da nossa API. Ele recebe as requisições HTTP (no endpoint `POST /simulate-loan`), valida os dados de entrada (através do DTO `SimulateLoanRequest` com as anotações de validação) e encaminha as informações para o caso de uso `SimulateLoanUseCase`. Depois, converte o resultado (um objeto `Loan`) em um DTO de resposta (`SimulateLoanResponse`) usando o `LoanDTOMapper`. Essa abordagem deixa o controller focado apenas em lidar com a comunicação HTTP, sem misturar a lógica de negócio.

### Gateways

#### LoanRepositoryGateway
Para a persistência, usamos o **LoanRepositoryGateway**, que implementa a interface `LoanGateway`. Sua responsabilidade é:

- Converter o objeto do domínio (`Loan`) em uma entidade de persistência (`LoanEntity`) utilizando o `LoanEntityMapper`.
- Salvar essa entidade no banco de dados através do repositório JPA (`LoanRepository`).
- Converter a entidade salva de volta para o objeto do domínio.

Dessa forma, a camada de aplicação não precisa conhecer os detalhes de como os dados são armazenados; ela simplesmente chama o método para salvar e recebe o resultado.

#### SendEmailGateway
O **SendEmailGateway** implementa a interface `EmailGateway` e é responsável por enviar notificações por e-mail com os resultados da simulação de empréstimo. Sua lógica é a seguinte:

- Utiliza o `JavaMailSender` para criar e enviar um e-mail.
- Constrói o conteúdo do e-mail por meio do `EmailTemplateBuilder`, que formata os dados do objeto `Loan` em um template HTML.
- Configura o assunto, o destinatário e o corpo do e-mail.
- Em caso de erro no envio, registra o erro e lança uma exceção para que o problema seja tratado adequadamente.

Dessa forma, o envio de e-mail fica desacoplado da lógica de persistência e dos cálculos financeiros, permitindo que a notificação seja facilmente alterada ou desativada sem impactar outras partes do sistema.

### Persistência
Na camada de persistência, temos:
- **LoanRepository:** uma interface que estende `JpaRepository`, responsável por oferecer operações CRUD para a entidade `LoanEntity`.
- **LoanEntity:** a classe mapeada para a tabela `LOAN`. Ela define todos os campos necessários (valor do empréstimo, data de nascimento, número de parcelas, parcela mensal, total pago, juros e moeda) e garante, através das anotações JPA, que os dados essenciais não sejam nulos.

### Fluxo Geral da Simulação com Conversão de Moedas e Notificação por E-mail

1. **Recepção da Requisição:**  
   O controller recebe a requisição HTTP e valida os dados, convertendo-os em um objeto `SimulateLoanRequest`.

2. **Conversão de Moeda:**  
   Se o valor do empréstimo for informado em uma moeda diferente da base (por exemplo, se for USD e a moeda base for BRL), o caso de uso utiliza o serviço de conversão de moedas para converter esse valor para a moeda base antes de prosseguir com os cálculos.

3. **Processamento da Simulação:**  
   Com o valor convertido (ou o valor original, se as moedas forem idênticas), o caso de uso chama o método `Loan.simulateLoan`, que aplica os cálculos financeiros (por meio dos padrões Strategy, Factory e Provider) para determinar a parcela mensal, o total a pagar e os juros do empréstimo.

4. **Persistência:**  
   O objeto `Loan` gerado é enviado para o `LoanRepositoryGateway`, que:
    - Converte o objeto do domínio em uma entidade de persistência (`LoanEntity`);
    - Salva essa entidade no banco de dados;
    - Reconverte a entidade salva de volta para um objeto do domínio.

5. **Envio de Notificação por E-mail:**  
   Após a persistência, o caso de uso invoca o `EmailGateway` para enviar um e-mail com os resultados da simulação para o endereço informado.

6. **Resposta ao Cliente:**  
   Por fim, o controller transforma o objeto `Loan` (com os cálculos finalizados e persistidos) em um `SimulateLoanResponse` utilizando o `LoanDTOMapper` e retorna essa resposta com status HTTP 201 (Created).



> Essa divisão deixa o sistema mais organizado, flexível e fácil de manter, já que qualquer alteração na forma de persistir dados ou na lógica de negócio pode ser feita de forma isolada, sem impactar as demais camadas.

### Decisões de Arquitetura

A decisão de adotar a Clean Architecture permitiu separar claramente as responsabilidades:
- **Domínio:** Centraliza as regras e cálculos financeiros, usando padrões de projeto (Strategy, Factory e Provider) para manter o código flexível e extensível.


- **Aplicação:** Orquestra a lógica de negócio e garante que os dados sejam transformados corretamente para a interface do usuário.


- **Infraestrutura:** Isola os detalhes técnicos (como acesso a banco de dados, envio de email e configuração do servidor) da lógica central.


> Esta organização não só facilita os testes unitários e de integração em cada camada, mas também permite que o ambiente de produção seja construído e implantado de forma reprodutível usando Docker e Docker Compose, com configurações dinâmicas definidas por variáveis de ambiente.

Em resumo, a estrutura do projeto foi pensada para proporcionar alta manutenibilidade, escalabilidade e testabilidade, permitindo que cada parte evolua de forma independente sem impactar as demais.

---

## Testes

> Este projeto possui uma suíte abrangente de testes que garante a robustez e a performance do sistema. As estratégias de teste abrangem tanto testes unitários em cada camada quanto testes de integração que validam a comunicação entre os componentes.

### Testes Unitários

Os testes unitários foram desenvolvidos para validar a funcionalidade isolada de cada componente, garantindo que a lógica de negócio e a orquestração sejam implementadas corretamente.


- **Domínio:**  
  Aqui, testamos as regras de negócio e os cálculos financeiros. Vale ressaltar, que um teste unitário importante valida que o sistema consegue executar 100.000 cálculos (por exemplo, utilizando a estratégia de cálculo com `BigDecimal` e `MathContext.DECIMAL128`) em menos de 1000ms. Esse teste assegura a performance e a precisão dos algoritmos financeiros.


- **Aplicação:**  
  Aqui é testado o `SimulateLoanUseCase` para confirmar que ele orquestra corretamente todo o fluxo da simulação do empréstimo. Isso inclui a conversão do valor informado para a moeda base, a invocação do método estático do domínio para calcular os valores financeiros, a delegação para a persistência via `LoanGateway` e o disparo da notificação por e-mail via `EmailGateway`. Dessa forma, o teste garante que o fluxo de dados – desde a entrada até a persistência – está funcionando corretamente.


- **Infraestrutura:**
    - **Gateways:**  
      O `LoanRepositoryGateway` é testado de forma isolada para assegurar que ele converte os objetos do domínio em entidades de persistência e vice-versa, sem depender de uma conexão real com o banco de dados.

---

### Testes de Integração

Os testes de integração garantem que os diferentes componentes do sistema se comuniquem corretamente e que os fluxos completos funcionem como esperado:

- **Repositórios:**  
  São testados usando um banco de dados em memória (H2). Os testes verificam se o `LoanRepository` realiza corretamente a operação de salvar.

- **Controllers:**  
  Testes de integração com MockMvc simulam requisições HTTP para o endpoint (como o `POST /simulate-loan`), verificando todo o fluxo – desde a validação dos dados de entrada e a execução do caso de uso até a transformação em DTO e a resposta final ao cliente. Esses testes asseguram que a API esteja respondendo com o status e os dados corretos.

--- 
### Testes de Desempenho (Gatling)
- **Objetivo:** Garantir que a aplicação lide bem com alta volumetria requisições simultâneas.
- **O que é testado:**
    - A capacidade da API de processar requisições sob diferentes cargas, com fases de warm-up e de carga intensa.
    - Simulações definidas com o Gatling, que injetam usuários de forma gradual e constante, monitorando tempos de resposta e throughput.
- **Execução:**
    - Os testes Gatling são containerizados, permitindo sua execução sem depender de uma instalação local do Java ou Gatling.

