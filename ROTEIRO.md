# Roteiro de apresentação — 12 minutos

## 0:00–1:00 — Problema e conceitos básicos

"Uma árvore é um grafo conectado e sem ciclos. Em uma árvore enraizada, a raiz é o primeiro nó, os filhos descendem de um pai e uma folha não possui filhos. Para localizar códigos de músicas, usamos árvores de busca. Uma árvore binária tem até dois filhos por nó. Em uma árvore binária de busca comum, valores menores ficam à esquerda e maiores à direita. Inserções ordenadas podem criar uma cadeia de altura n-1; balancear evita esse problema."

## 1:00–3:00 — k-D (demonstração conceitual)

Uma k-D divide um espaço de k dimensões. Aqui, k=2: cada ponto (x,y) representa duas características fictícias de uma música: x = ano de lançamento menos 1990; y = duração em dezenas de segundos. Alternamos o eixo comparado a cada nível: x, y, x, y. Para construir uma árvore inicialmente balanceada, ordenamos os pontos pelo eixo atual, escolhemos a mediana e repetimos nos dois grupos. Não há rotações obrigatórias como na Rubro-Negra; inserções sucessivas podem desbalancear a k-D, que pode ser reconstruída por medianas. Na busca de vizinho mais próximo, explora-se primeiro o lado do ponto consultado e só se descarta o outro lado se a distância à linha divisória exceder a melhor distância já encontrada.

Exemplo estático de 21 nós (ano relativo a 1990 e duração em dezenas de segundos, dados fictícios):

```text
Raiz: (11, 10) — eixo x
    E: (5, 11) — eixo y
        E: (6, 5) — eixo x
            E: (1, 8) — eixo y
                E: (3, 3) — eixo x
            D: (10, 7) — eixo y
                E: (8, 1) — eixo x
        D: (7, 17) — eixo x
            E: (4, 19) — eixo y
                E: (2, 15) — eixo x
            D: (9, 13) — eixo y
    D: (19, 14) — eixo y
        E: (16, 2) — eixo x
            E: (15, 9) — eixo y
                E: (13, 4) — eixo x
            D: (20, 12) — eixo y
                E: (18, 6) — eixo x
        D: (17, 20) — eixo x
            E: (12, 21) — eixo y
                E: (14, 16) — eixo x
            D: (21, 18) — eixo y
```

Busca exata por (17,20): a partir de (11,10), compare x: 17 > 11, vá à direita. No próximo nó, compare y e prossiga conforme a figura. Em cada nível, use o eixo indicado. O caminho termina quando o par de coordenadas é encontrado. Em aplicações reais, também é possível pesquisar todos os pontos em um retângulo ou o ponto mais próximo. Política do exemplo: pares idênticos são rejeitados; em caso de empate no eixo, use a outra coordenada como critério secundário. A amostra não possui empates em nenhum eixo.

## 3:00–5:30 — Rubro-Negra

Cada nó contém um código, a cor e referências para pai e filhos esquerdo/direito. A raiz e as folhas vazias NIL são pretas. Um nó vermelho não pode ter filho vermelho e todos os caminhos de um nó a suas folhas NIL contêm o mesmo número de nós pretos.

Exemplo de inserção 10, 20, 30 (P = preto, V = vermelho):

```text
Antes da correção       Depois: rotação à esquerda e recoloração
10(P)                         20(P)
    \                         /    \
    20(V)                   10(V)  30(V)
        \
        30(V)
```

Se o pai é preto, não há conflito vermelho-vermelho. Se pai e tio são vermelhos, recolorimos e verificamos o avô. Se o pai é vermelho e o tio preto, usamos rotações e recoloração. As rotações mudam referências sem alterar a ordem das chaves. Na exclusão de um nó preto, pode faltar um preto em um caminho; usamos o irmão e seus filhos para restaurar o equilíbrio, por rotações e/ou recoloração. Uma remoção com dois filhos usa o sucessor (menor chave à direita).

## 5:30–8:00 — Árvore 2-3-4

Não é binária: um nó interno pode ter 2, 3 ou 4 filhos e respectivamente 1, 2 ou 3 chaves ordenadas. Todas as folhas ficam no mesmo nível. Cada intervalo de chaves indica o filho correto para a busca.

Exemplo de inserção 10, 20, 30, 40:

```text
[10 | 20 | 30]       divide e promove 20       [20]
                                               /    \
                                            [10]  [30 | 40]
```

Exemplo de exclusão 10 (empréstimo via pai):

```text
        [20]                 [30]
        /  \                 /  \
     [10] [30|40]          [20] [40]
```

Quando o filho tem uma chave e o irmão tem pelo menos duas, uma chave do pai desce e uma do irmão sobe. Se os vizinhos disponíveis também têm apenas uma chave, dois filhos se fundem com a chave separadora do pai. A raiz pode desaparecer quando fica vazia; o filho vira a nova raiz. No código a correção de ocupação é preventiva, antes de descer. Na remoção de chave interna, pode-se usar predecessor, sucessor ou fusão.

Exemplo de fusão: raiz [20], filhos [10] e [30]. Para excluir 10, primeiro fundimos [10|20|30]; depois removemos 10. A nova raiz é [20|30].

## 8:00–11:00 — Demonstração do programa

1. Execute `java -cp out Interface` e mostre 31 códigos iniciais nas páginas rubro-negra e 2-3-4. A versão gráfica possui botões buscar, cadastrar e excluir correspondentes às opções abaixo. O catálogo usa metadados fictícios e não reproduz áudio. Para os cadastros novos, preencha os dados fictícios no formulário. P = preto, V = vermelho. O caminho da busca aparece contornado em azul e os eventos estão na página activity.
2. Opção 3, código 35: encontrado. Repita com 999: inexistente.
3. Opção 1, código 80: recoloração na Rubro-Negra e divisão de nó na 2-3-4.
4. Opção 1, código 85: rotação à esquerda na Rubro-Negra.
5. Opção 1, código 90: novas correções; opção 1, código 35: duplicata recusada.
6. Opção 2, código 3: fusões na 2-3-4. Exclua também 5 e 7: a exclusão de 7 gera rotação na Rubro-Negra e empréstimo na 2-3-4. Exclua 10.
7. Opção 4: mostre 30 códigos finais, as árvores reorganizadas e a mensagem de integridade.

Caso o tempo seja curto, `java -cp out App demo` executa a mesma sequência automaticamente. O menu não é a árvore: App só chama as classes que guardam os nós.

## 11:00–12:00 — Comparação e conclusão

- Rubro-Negra: uma chave por nó real, até dois filhos, balanceamento por cores e rotações.
- 2-3-4: várias chaves por nó, até quatro filhos, balanceamento por divisão, empréstimo e fusão.
- k-D: separa pontos multidimensionais por eixos; pode usar reconstrução por medianas.
- As duas árvores implementadas oferecem operações fundamentais O(log n). A verificação e impressão completas do protótipo custam O(n).
- Repetidos: o código é identificador único; o cadastro é recusado sem alterar a estrutura.

## Perguntas que podem aparecer

**O que é um nó?** Um objeto que guarda dados e referências a outros nós.

**Por que não usar ArrayList para tudo?** Uma busca sequencial custa O(n). As árvores balanceadas descartam regiões em cada etapa. O ArrayList é usado apenas para registrar eventos e listar resultados.

**O que é NIL?** Um nó sentinela preto compartilhado que representa filhos vazios e simplifica as regras de correção.

**O que é rotação?** Uma reorganização local de pai e filho que mantém a ordenação das chaves.

**Por que 2-3-4?** O nome corresponde às quantidades possíveis de filhos de um nó interno.

**Por que as folhas 2-3-4 estão no mesmo nível?** Divisões, empréstimos e fusões mantêm a altura uniforme; uma divisão da raiz aumenta a altura de toda a árvore.

**Qual a relação entre as árvores?** Uma Rubro-Negra pode representar uma 2-3-4 agrupando cada nó preto com seus filhos vermelhos. A representação binária não precisa ser idêntica à árvore 2-3-4 construída separadamente.

**Por que duas árvores no projeto?** Para comparar técnicas de indexação com as mesmas operações, não porque um sistema real precise necessariamente de dois índices iguais.

**As árvores foram testadas?** Sim: além do exemplo inicial, o programa de teste faz operações pseudoaleatórias e verifica a ordenação, as cores, a altura negra, a ocupação dos nós, a profundidade das folhas e a correspondência de conteúdo.

**Qual a limitação?** Os dados são fictícios, em memória; não há persistência. A k-D é demonstração estática, não parte do programa Java.
