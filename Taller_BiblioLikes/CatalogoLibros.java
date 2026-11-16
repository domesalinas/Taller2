import java.time.Year;
import java.util.ArrayList;

public class CatalogoLibros {
    private final ArrayList<Libro> libros;

    public CatalogoLibros() {
        this.libros = new ArrayList<>(DatosIniciales.cargar());
        ordenarPorCodigoBurbuja();
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

    public int buscarIndicePorCodigo(int codigo) {
        if (codigo <= 0 || libros.isEmpty()) {
            return -1;
        }
        int inicio = 0;
        int fin = libros.size() - 1;
        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            int codigoMedio = libros.get(medio).getCodigo();
            if (codigoMedio == codigo) {
                return medio;
            } else if (codigoMedio < codigo) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return -1;
    }

    public ArrayList<Libro> buscarCoincidencias(String texto) throws Exception {
        if (texto == null || texto.trim().isEmpty()) {
            throw new Exception("Ingrese un texto válido para buscar");
        }
        String filtro = texto.trim().toLowerCase();
        ArrayList<Libro> resultados = new ArrayList<>();
        for (int i = 0; i < libros.size(); i++) {
            Libro actual = libros.get(i);
            if (actual.getTitulo().toLowerCase().contains(filtro) || actual.getAutor().toLowerCase().contains(filtro)) {
                resultados.add(actual);
            }
        }
        return resultados;
    }

    public void registrar(Libro nuevo) throws Exception {
        if (nuevo == null) {
            throw new Exception("El libro no puede ser nulo");
        }
        validarDatos(nuevo.getCodigo(), nuevo.getTitulo(), nuevo.getAutor(), nuevo.getGenero(), nuevo.getAnio());
        if (buscarIndicePorCodigo(nuevo.getCodigo()) != -1) {
            throw new Exception("Ya existe un libro registrado con el código " + nuevo.getCodigo());
        }
        libros.add(nuevo);
        ordenarPorCodigoBurbuja();
    }

    public void editarLibro(int codigo, String nuevoTitulo, String nuevoAutor, String nuevoGenero, int nuevoAnio) throws Exception {
        int indice = buscarIndicePorCodigo(codigo);
        if (indice == -1) {
            throw new Exception("No se encontró un libro con el código " + codigo);
        }
        validarDatos(codigo, nuevoTitulo, nuevoAutor, nuevoGenero, nuevoAnio);
        Libro libro = libros.get(indice);
        libro.setTitulo(nuevoTitulo.trim());
        libro.setAutor(nuevoAutor.trim());
        libro.setGenero(nuevoGenero.trim());
        libro.setAnio(nuevoAnio);
    }

    public void darMeGusta(int codigo) throws Exception {
        int indice = buscarIndicePorCodigo(codigo);
        if (indice == -1) {
            throw new Exception("No se encontró el libro seleccionado");
        }
        libros.get(indice).incrementarMeGusta();
    }

    private void validarDatos(int codigo, String titulo, String autor, String genero, int anio) throws Exception {
        if (codigo <= 0) {
            throw new Exception("El código debe ser un número entero positivo");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new Exception("El título no puede estar vacío");
        }
        if (autor == null || autor.trim().isEmpty()) {
            throw new Exception("El autor no puede estar vacío");
        }
        if (genero == null || genero.trim().isEmpty()) {
            throw new Exception("Seleccione una categoría válida");
        }
        int anioActual = Year.now().getValue();
        if (anio <= 0 || anio > anioActual) {
            throw new Exception("El año debe estar entre 1 y " + anioActual);
        }
    }
}