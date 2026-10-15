public class AVLTree<K extends Comparable<K>, V> {

    private class Nodo {
        K clave;
        V valor;
        Nodo izq, der;
        int altura;

        Nodo(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.altura = 1;
        }
    }

    private Nodo raiz;

    private int altura(Nodo n) {
        return n == null ? 0 : n.altura;
    }

    private void actualizarAltura(Nodo n) {
        n.altura = 1 + Math.max(altura(n.izq), altura(n.der));
    }

    private int factorBalance(Nodo n) {
        return n == null ? 0 : altura(n.izq) - altura(n.der);
    }

    private Nodo rotarDerecha(Nodo y) {
        Nodo x = y.izq;
        Nodo t = x.der;
        x.der = y;
        y.izq = t;
        actualizarAltura(y);
        actualizarAltura(x);
        return x;
    }

    private Nodo rotarIzquierda(Nodo x) {
        Nodo y = x.der;
        Nodo t = y.izq;
        y.izq = x;
        x.der = t;
        actualizarAltura(x);
        actualizarAltura(y);
        return y;
    }

    private Nodo balancear(Nodo n) {
        actualizarAltura(n);
        int fb = factorBalance(n);

        // Left-Left
        if (fb > 1 && factorBalance(n.izq) >= 0)
            return rotarDerecha(n);

        // Left-Right
        if (fb > 1 && factorBalance(n.izq) < 0) {
            n.izq = rotarIzquierda(n.izq);
            return rotarDerecha(n);
        }

        // Right-Right
        if (fb < -1 && factorBalance(n.der) <= 0)
            return rotarIzquierda(n);

        // Right-Left
        if (fb < -1 && factorBalance(n.der) > 0) {
            n.der = rotarDerecha(n.der);
            return rotarIzquierda(n);
        }

        return n;
    }

    public void insertar(K clave, V valor) {
        raiz = insertar(raiz, clave, valor);
    }

    private Nodo insertar(Nodo n, K clave, V valor) {
        if (n == null) return new Nodo(clave, valor);

        int cmp = clave.compareTo(n.clave);
        if      (cmp < 0) n.izq = insertar(n.izq, clave, valor);
        else if (cmp > 0) n.der = insertar(n.der, clave, valor);
        else              n.valor = valor;

        return balancear(n);
    }

    public V obtener(K clave) {
        Nodo n = raiz;
        while (n != null) {
            int cmp = clave.compareTo(n.clave);
            if      (cmp < 0) n = n.izq;
            else if (cmp > 0) n = n.der;
            else              return n.valor;
        }
        return null;
    }

    public boolean contiene(K clave) {
        return obtener(clave) != null;
    }

    public interface Visitante<K, V> {
        void visitar(K clave, V valor);
    }

    public void enOrden(Visitante<K, V> visitante) {
        enOrden(raiz, visitante);
    }

    private void enOrden(Nodo n, Visitante<K, V> visitante) {
        if (n == null) return;
        enOrden(n.izq, visitante);
        visitante.visitar(n.clave, n.valor);
        enOrden(n.der, visitante);
    }

    public int tamanio() {
        return tamanio(raiz);
    }

    private int tamanio(Nodo n) {
        return n == null ? 0 : 1 + tamanio(n.izq) + tamanio(n.der);
    }
}
