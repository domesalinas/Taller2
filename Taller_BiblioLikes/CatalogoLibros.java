import java.util.ArrayList;

public class CatalogoLibros {
    private final ArrayList<Libro> libros;

    public CatalogoLibros() {
        // R1: Carga única de los 20 registros iniciales en memoria
        this.libros = DatosIniciales.cargar();
    }

    public int getTotalLibros() {
        return libros.size();
    }

    public ArrayList<Libro> getLibros() {
        return libros;
    }

    public void ordenarPorCodigoBurbuja() {
        int n = libros.size();
        for (int i = 0; i < n - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (libros.get(j).getCodigo() > libros.get(j + 1).getCodigo()) {
                    Libro temp = libros.get(j);
                    libros.set(j, libros.get(j + 1));
                    libros.set(j + 1, temp);
                    huboCambio = true;
                }
            }
            if (!huboCambio) break;
        }
    }

    public ArrayList<Libro> obtenerRanking() {
        ArrayList<Libro> copia = new ArrayList<>(libros);
        int n = copia.size();
        for (int i = 0; i < n - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (copia.get(j).getMeGusta() < copia.get(j + 1).getMeGusta()) {
                    Libro temp = copia.get(j);
                    copia.set(j, copia.get(j + 1));
                    copia.set(j + 1, temp);
                    huboCambio = true;
                }
            }
            if (!huboCambio) break;
        }
        return copia;
    }

}