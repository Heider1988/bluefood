# bluefood
Sistema de Cadastro de Restaurante e Clientes

Sistema onde Restaurantes e Clientes conseguem efetuar seus devidos cadastros para atender as demandas conforme a necessidade. O sistema é parecido com o fluxo do Ifood. O cliente realiza um pedido e o restaurante recebe e atualiza o status conforme o pedido vai avançando.

O sistema foi desenolvido através da linguagem Java com Spring Boot, Spring Security, Spring Data, JPQL e o Thymeleaf para o front-end. Lembrando que o módulo sbpay foi adicionado apenas para simular a compra de um possível cliente. Esse módulo é a parte e pode ficar em qualquer servidor fora da aplicação. Nesse módulo apenas foi criado um web service para aprovar ou não uma compra. Se o cartão começa com 4 dígitos , então é aprovado, se não é reprovado.

O sistema está rodando no heroku como estivesse em produção.

Link do sistema bluefood: https://bluefood2.herokuapp.com/login Link do módulo de pagamento caso queira testar via postman: https://sbpay1.herokuapp.com/sbpay/pay
