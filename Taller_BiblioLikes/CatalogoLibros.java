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
}