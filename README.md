# TuneTree — Biblioteca musical

Trabalho de Teoria de Grafos — árvores de busca balanceadas.

## Problema

Uma biblioteca musical precisa localizar, cadastrar e remover faixas por um código identificador. Este protótipo em memória mantém dois índices independentes sobre os mesmos códigos inteiros positivos: uma árvore Rubro-Negra e uma árvore 2-3-4. O objetivo é comparar as estratégias de balanceamento, não substituir um serviço de streaming completo. Dados fictícios; não há banco nem persistência.

## Executar (Java 17 ou superior)

Abra esta pasta no VS Code e, no terminal integrado, execute:

```sh
javac -encoding UTF-8 -d out src/*.java
java -cp out Interface
```

A interface Swing é inspirada em media centers de 2010, com tipografia leve, gradientes cinza-azulados, cartões de álbuns abstratos e menu lateral. As páginas library, rubro-negra, 2-3-4, activity e k-D example mostram o catálogo, os índices, o histórico e o exemplo conceitual. Cadastro com título, artista, álbum, gênero, ano e duração em segundos. Um HashMap guarda os metadados; a busca por código é executada nos dois índices antes de consultar o registro. Selecionar uma faixa preenche o código; clicar num cartão executa a busca. Os indicadores mostram altura em arestas (vazia = -1), nós visitados na busca e integridade. P significa preto e V vermelho; contornos azuis destacam o caminho da busca. Duplicatas são recusadas. As músicas e capas são fictícias; não há reprodução de áudio nem persistência. A divisão preventiva na 2-3-4 faz parte da inserção.

Modo alternativo pelo terminal: `java -cp out App`.

No menu, 1 cadastra, 2 exclui, 3 busca, 4 mostra as árvores e 0 encerra. São carregados 31 códigos fictícios no início. O cadastro de um código existente é recusado; não há duplicatas em nenhum índice. As duas árvores são atualizadas na mesma operação e verificadas após mudanças.

Demonstração automática, sempre com a mesma sequência:

```sh
java -cp out App demo
```

A visualização usa texto indentado: E/D indicam filhos esquerdo/direito; V/P indicam vermelho/preto. Na 2-3-4, F0–F3 indicam filhos na ordem das chaves. A cada operação são mostrados os eventos de balanceamento. Nenhuma biblioteca de árvores é usada.

## Organização

- `src/Interface.java`: interface gráfica e cadastro em memória.
- `src/PainelArvore.java` e `src/NoVisual.java`: desenho e representação visual separada dos algoritmos.
- `src/App.java`: menu, dados de exemplo, operação nos dois índices, impressão e checagem de integridade.
- `src/RubroNegra.java`: nó com chave, cor, pai e dois filhos. Usa sentinela NIL preta para folhas ausentes, rotações e recoloração.
- `src/Arvore234.java`: nó com até três chaves em vetor e até quatro referências de filhos. Divide nós cheios antes de descer; na exclusão redistribui ou funde antes de descer quando necessário. Raiz vazia é substituída por seu filho quando há um.
- `test/TesteArvores.java`: testes automatizados, usando HashSet apenas como referência externa para verificar conteúdo, não para implementar árvores. Compara resultados e valida invariantes após operações.
- `ROTEIRO.md`: explicação da k-D com 21 pontos, exemplos e roteiro de apresentação.

## Testes

```sh
javac -encoding UTF-8 -d out src/*.java test/*.java
java -cp out TesteArvores
```

Resultado observado com Java 21: 48007 operações verificadas, incluindo esvaziamento completo das duas árvores. Isso é evidência de teste, não prova formal de ausência de erros.

## Métodos e propriedades

Rubro-Negra: busca binária por chave; na inserção o novo nó é vermelho e as violações são corrigidas por rotações/recoloração. Na remoção, a retirada de um nó preto pode reduzir a altura negra de um caminho; a correção usa o irmão, suas cores e rotações. As folhas NIL são pretas, a raiz é preta, não há pai e filho vermelhos e todos os caminhos de um nó a NIL têm a mesma quantidade de nós pretos.

2-3-4: chaves ordenadas em cada nó; um nó interno com m chaves tem m+1 filhos. Todas as folhas reais estão no mesmo nível. Na inserção, divide o nó cheio promovendo a chave mediana. Na exclusão, evita descer para um nó de uma chave sem antes reforçá-lo quando necessário, por empréstimo via pai ou fusão. Ambas têm busca, inserção e remoção O(log n) para o núcleo dos algoritmos. A impressão, a checagem completa de integridade e a comparação entre índices percorrem os dados e têm custo O(n), portanto não são parte desse custo logarítmico.

A k-D é apresentada conceitualmente, conforme o enunciado, sem implementação obrigatória. A construção por medianas alternadas oferece uma árvore espacial balanceada inicialmente. Os pontos representam duas características musicais; o roteiro usa ano relativo a 1990 e duração em dezenas de segundos.

## Referências conceituais

- Yale University — Notes on Data Structures and Programming Techniques: https://cs.yale.edu/homes/aspnes/classes/223/notes.html
- University of Hawaii — Balanced Trees: https://courses.ics.hawaii.edu/ics311_f26/Notes/Topic-11.html
- University of Wisconsin — How Red and Black Trees Work: https://pages.stat.wisc.edu/~jinc/
- Princeton University — Kd-Trees Assignment (conceitos de busca espacial): https://www.cs.princeton.edu/courses/archive/spring26/cos226/assignments/kdtree/specification.php

Código preparado como base de estudo com assistência de IA. Revise, execute, personalize o contexto e explique as decisões antes de entregar. Observe as regras da disciplina sobre uso de IA e autoria.

### Tipografia

A interface seleciona faces ultrafinas e leves disponíveis no sistema (por exemplo, Helvetica Neue UltraLight ou Avenir Next Ultra Light no macOS; Inter Display Thin ou Lato Thin em outros sistemas). Não é necessário instalar fontes para executar; se nenhuma face preferida estiver disponível, o Java usa uma fonte alternativa. A aparência exata depende das fontes instaladas.

### Interface Fluid

Painéis transparentes compartilham o gradiente de fundo. Menu e capas têm realce suave ao passar o mouse; a troca de páginas usa uma transição de opacidade. A visualização destaca os nós visitados progressivamente e interpola as posições dos nós que permanecem entre duas operações; nós novos aparecem e removidos desaparecem. São transições entre estados finais, não uma reprodução de cada passo interno de rotação ou fusão. O histórico de balanceamento está na página activity. As animações não modificam os índices e são implementadas com Swing Timer e Graphics2D.
