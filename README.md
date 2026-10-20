# Divisão de tarefas

| Pessoa | Responsável por |
| --- | --- |
| Gustavo | Estrutura do repositório, `Main`, `GerenciadorJogo`, `NaveJogador`, loop do jogo, tela de combate em 1ª pessoa |
| Eduardo | `GerenciadorMapa`, `NaveInimiga` (IA), `GerenciadorCombate`, controles do teclado, tela do mapa galáctico |
| Luís | `EntidadeJogo`, `Nave`, `Projetil`, `BaseEspacial`, HUD, revisão dos pull requests, Javadoc, diagrama atualizado |
| Ulisses | `EstadoJogo`, `Danificavel`, `Setor`, `.gitignore`, telas de início e fim, testes, README |

As fases abaixo seguem a ordem de dependência: uma classe só é feita depois das classes que ela usa.

## Fase 1 — Arrumar o repositório e criar a base

O repositório ainda não compila, então a Fase 1 começa corrigindo a estrutura. Os outros só começam a programar depois que o Gustavo subir essa correção, para evitar conflitos.

- [ ] **Gustavo** — Tirar as pastas de dentro de `src/starmaster/Main/` e deixá-las direto em `src/starmaster/`. O caminho da pasta precisa bater com o `package` de cada classe.
- [ ] **Gustavo** — Renomear `main.java` para `Main.java` e a pasta `Interfaces` para `interfaces` (pacotes ficam em minúsculo).
- [ ] **Gustavo** — Corrigir os imports do JavaFX na `Main`, porque `import java.fx.naosei` não existe, e definir as constantes `PIXEIS_X` e `PIXEIS_Y`.
- [ ] **Gustavo** — Subir o `.project` e o `.classpath` do Eclipse, para os colegas importarem o projeto direto. Todos devem dar o nome `JavaFX` para a User Library.
- [ ] **Gustavo** — Adicionar Eduardo, Luís e Ulisses como colaboradores (Settings > Collaborators).
- [ ] **Ulisses** — Criar o `.gitignore` com `bin/` e mover o `StarMaster.png` para uma pasta `docs/`, fora do `src`.
- [ ] **Ulisses** — Criar o enum `EstadoJogo`, que ainda não existe no repositório, e a interface `Danificavel`.
- [ ] **Luís** — Escrever as classes abstratas `EntidadeJogo` e `Nave`: atributos, construtor, `distanciaAte`, `receberDano` e `estaDestruido`.
- [ ] **Eduardo** — Fazer o esqueleto do `GerenciadorMapa`: a grade de setores e o método `obterSetor`.

## Fase 2 — Comportamento

Com a base pronta, cada classe ganha suas ações. Ainda não há tela nesta fase, então dá para testar tudo com `System.out.println`.

- [ ] **Luís** — Fazer o `Projetil` (movimento e `colideCom`) e a `BaseEspacial` (`abastecer` e receber dano).
- [ ] **Ulisses** — Fazer o `Setor`: a lista de inimigos, a base e os métodos `adicionarInimigo`, `removerInimigo`, `temInimigos` e `temBase`.
- [ ] **Gustavo** — Fazer a `NaveJogador`: energia, `atirar`, `girar`, `viajarPara`, `atracar` e `recarregar`.
- [ ] **Gustavo** — Fazer o `GerenciadorJogo`: estados, pontuação, `verificarVitoria` e `verificarDerrota`.
- [ ] **Eduardo** — Fazer a `NaveInimiga`, que é a IA: escolher o alvo, perseguir e atirar.
- [ ] **Eduardo** — Fazer o `GerenciadorCombate`: a lista de projéteis, as colisões, a aplicação de dano e os pontos ganhos.
- [ ] **Eduardo** — Completar o `GerenciadorMapa` com `gerarMapa`, `moverInimigos` e `calcularCustoViagem`.

## Fase 3 — Telas em JavaFX

Tudo desta fase fica no pacote `ui`. A tela só lê os dados dos gerenciadores e não guarda regra do jogo; assim, a lógica continua testável sozinha.

- [ ] **Gustavo** — Criar o loop do jogo com `AnimationTimer`, chamando `GerenciadorJogo.atualizar()` a cada quadro.
- [ ] **Gustavo** — Fazer a tela de combate em 1ª pessoa: desenhar inimigos e projéteis maiores ou menores conforme a distância (`z`).
- [ ] **Eduardo** — Fazer os controles do teclado: girar, atirar, abrir o mapa e atracar.
- [ ] **Eduardo** — Fazer a tela do mapa galáctico: a grade de setores, inimigos e bases, e a escolha do destino da viagem.
- [ ] **Luís** — Fazer o HUD: barras de energia e escudo, pontuação e alerta de base sob ataque.
- [ ] **Ulisses** — Fazer as telas de início, vitória e derrota, com texto e "pressione ENTER".
- [ ] **Ulisses** — Apagar o `ui/NaN.txt` quando a primeira classe do pacote `ui` existir.

## Fase 4 — Refinamento, testes e entrega

A última fase deixa o código limpo e o projeto pronto para apresentar. Como a nota depende do uso de orientação a objetos, a revisão do Luís confere justamente isso.

- [ ] **Luís** — Revisar o código: nomes, encapsulamento (atributos `private`), código repetido e comentários Javadoc.
- [ ] **Luís** — Atualizar o diagrama UML com o que mudou durante o projeto, por exemplo a `Main` herdando de `Application`.
- [ ] **Ulisses** — Jogar e registrar os bugs como Issues no GitHub, cada um com os passos para repetir o erro.
- [ ] **Ulisses** — Escrever o README final: como configurar o JavaFX, como rodar, os controles e os integrantes.
- [ ] **Gustavo e Eduardo** — Corrigir os bugs registrados e ajustar a dificuldade.

## Como trabalhar no GitHub

Cada tarefa vira uma branch e só entra na `main` por pull request. Assim ninguém sobrescreve o código do outro.

1. Antes de começar, atualize a `main` no Eclipse com **Team > Pull**.
2. Crie uma branch com o nome da tarefa, por exemplo `nave-jogador`, em **Team > Switch To > New Branch**.
3. Faça commits pequenos, com mensagens claras, como "NaveJogador: atirar consome energia".
4. Envie com **Push Branch** e abra o pull request no GitHub.
5. O Luís revisa o pull request. Mudanças grandes também passam pelo Gustavo ou pelo Eduardo antes do merge.
6. Não suba o caminho do JavaFX SDK nem a pasta `bin/`. Essa configuração fica na máquina de cada um.

# Bibliografia
-- https://dev.java/learn/javafx/core/layout/

-- https://download.java.net/java/GA/javafx26/docs/api/javafx.graphics/javafx/scene/layout/StackPane.html

-- SLides do professor.

