# TF-IDF Search Engine in Java

A document search engine based on TF-IDF, built from scratch in Java. The whole index is built on a **custom AVL tree**, with no standard Java collections (`HashMap`, `TreeMap`, `ArrayList`, etc.).

## Features

- Term index backed by a self-balancing AVL tree (`AVLTree.java`).
- Document ranking by relevance using TF-IDF and the similarity between the query and each document (`SearchEngine.java`).
- Document norms precomputed at indexing time, so each query runs faster.
- Results verified against Python simulations.

## Project Structure

| File | Description |
|---|---|
| `AVLTree.java` | Generic AVL tree with insertion, search, and rebalancing |
| `SearchEngine.java` | Document indexing and TF-IDF ranking |
| `Main.java` | Entry point: <describe what it does, e.g. loads documents and runs queries> |

## Build and Run

```bash
javac -d bin *.java
java -cp bin Main <arguments, if any>
```

## Example

<Paste an example query here and the top results it returns.>

## Technical Notes

- Lookup and insertion in the index run in O(log n) thanks to the AVL tree.


Kenneth Galván Flores