import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Year;

public class Ventana {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTextField txtCodigo;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JComboBox<String> cboCategoria;
    private JSpinner spiAnio;
    private JButton bntRegistrar;
    private JTextField txtBuscarCodi;
    private JTextField txtBuscarcoinci;

    private CatalogoLibros catalogo = new CatalogoLibros();

    // Controles nuevos: no se modifica el modelo de los compañeros.
    private final int anioActual = Year.now().getValue();
    private final String[] generos = {"Seleccione un género", "Programación",
            "Bases de datos", "Redes", "Seguridad", "Sistemas", "Software", "IA", "Gestión"};
    private final JLabel lblTotal = new JLabel();
    private final DefaultListModel<Libro> modeloResultados = new DefaultListModel<>();
    private final JList<Libro> listaResultados = new JList<>(modeloResultados);
    private final JLabel lblCoincidencias = new JLabel("Coincidencias: 0");
    private final JTextField txtEditarCodigo = new JTextField(20);
    private final JTextField txtEditarTitulo = new JTextField(20);
    private final JTextField txtEditarAutor = new JTextField(20);
    private final JComboBox<String> cboEditarGenero = new JComboBox<>(generos);
    private final JSpinner spiEditarAnio = new JSpinner(new SpinnerNumberModel(anioActual, 1, anioActual, 1));
    private final JLabel lblMeGusta = new JLabel("Me gusta: —");
    private final JButton btnGuardar = new JButton("Guardar cambios");
    private final JButton btnMeGusta = new JButton("Me gusta +1");
    private final JButton btnBuscarCodigo = new JButton("Buscar por código");
    private final JButton btnBuscarTexto = new JButton("Buscar coincidencias");

    // Guardamos la última consulta válida para recalcularla tras un cambio.
    private enum TipoBusqueda { NINGUNA, CODIGO, TEXTO }
    private TipoBusqueda tipoBusqueda = TipoBusqueda.NINGUNA;
    private int codigoBuscado;
    private String textoBuscado = "";

    // Pestaña Ranking (R6).
    private final DefaultListModel<Libro> modeloRanking = new DefaultListModel<>();
    private final JList<Libro> listaRanking = new JList<>(modeloRanking);
    private final JButton btnActualizar = new JButton("Actualizar");



    public Ventana() {
        construirInterfaz();
        int anioActual = Year.now().getValue();
        spiAnio.setModel(new SpinnerNumberModel(anioActual, 1, anioActual, 1));

        bntRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int codigo = Integer.parseInt(txtCodigo.getText().trim());
                    String titulo = txtTitulo.getText();
                    String autor = txtAutor.getText();
                    String cat = leerGenero(cboCategoria);
                    int anio = leerAnio(spiAnio);

                    catalogo.registrar(new Libro(codigo, titulo.trim(), autor.trim(), cat, anio, 0));
                    txtCodigo.setText("");
                    txtTitulo.setText("");
                    txtAutor.setText("");
                    actualizarTotal();
                    refrescarConsulta(codigoSeleccionado());
                    actualizarRanking();
                    JOptionPane.showMessageDialog(panel1, "Agregado nuevo libro. Total: " + catalogo.getTotalLibros());
                } catch (NumberFormatException ex) {
                    error("El código debe ser un número entero positivo");
                } catch (Exception ex) {
                    error(ex.getMessage());
                }
            }
        });
        conectarEventosNuevos();
        actualizarTotal();
        limpiarEdicion();
        actualizarRanking();
    }

    // INTEGRANTE 4: inicialización manual de los controles existentes.
    private void construirInterfaz() {
        panel1 = new JPanel(new BorderLayout());
        tabbedPane1 = new JTabbedPane();
        txtCodigo = new JTextField(20);
        txtTitulo = new JTextField(20);
        txtAutor = new JTextField(20);
        cboCategoria = new JComboBox<>(generos);
        spiAnio = new JSpinner();
        bntRegistrar = new JButton("Registrar libro");
        txtBuscarCodi = new JTextField(10);
        txtBuscarcoinci = new JTextField(18);
        tabbedPane1.addTab("Registro", crearRegistro());
        tabbedPane1.addTab("Búsquedas y edición", crearBusquedas());
        tabbedPane1.addTab("Ranking", crearRanking());
        panel1.add(tabbedPane1, BorderLayout.CENTER);
    }

    // INTEGRANTE 4: estructura y formulario de registro.
    private JPanel crearRegistro() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JPanel formulario = new JPanel(new GridBagLayout());
        agregarCampo(formulario, 0, "Código:", txtCodigo);
        agregarCampo(formulario, 1, "Título:", txtTitulo);
        agregarCampo(formulario, 2, "Autor:", txtAutor);
        agregarCampo(formulario, 3, "Género:", cboCategoria);
        agregarCampo(formulario, 4, "Año:", spiAnio);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(bntRegistrar);
        acciones.add(lblTotal);
        agregarCampo(formulario, 5, "", acciones);
        panel.add(new JLabel("Registrar un nuevo libro"), BorderLayout.NORTH);
        panel.add(formulario, BorderLayout.CENTER);
        return panel;
    }

    // INTEGRANTE 5: pantallas de búsquedas, edición y ranking.
    private JPanel crearBusquedas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel consultas = new JPanel(new GridBagLayout());
        JPanel porCodigo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        porCodigo.add(txtBuscarCodi);
        porCodigo.add(btnBuscarCodigo);
        JPanel porTexto = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        porTexto.add(txtBuscarcoinci);
        porTexto.add(btnBuscarTexto);
        agregarCampo(consultas, 0, "Código:", porCodigo);
        agregarCampo(consultas, 1, "Título o autor:", porTexto);
        panel.add(consultas, BorderLayout.NORTH);

        listaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaResultados.setVisibleRowCount(6);
        JPanel resultados = new JPanel(new BorderLayout(5, 5));
        resultados.add(lblCoincidencias, BorderLayout.NORTH);
        resultados.add(new JScrollPane(listaResultados), BorderLayout.CENTER);
        panel.add(resultados, BorderLayout.CENTER);

        JPanel edicion = new JPanel(new GridBagLayout());
        edicion.setBorder(BorderFactory.createTitledBorder("Datos del libro seleccionado"));
        txtEditarCodigo.setEditable(false);
        agregarCampo(edicion, 0, "Código:", txtEditarCodigo);
        agregarCampo(edicion, 1, "Título:", txtEditarTitulo);
        agregarCampo(edicion, 2, "Autor:", txtEditarAutor);
        agregarCampo(edicion, 3, "Género:", cboEditarGenero);
        agregarCampo(edicion, 4, "Año:", spiEditarAnio);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnGuardar);
        acciones.add(btnMeGusta);
        acciones.add(lblMeGusta);
        agregarCampo(edicion, 5, "", acciones);
        panel.add(edicion, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearRanking() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(new JLabel("Libros ordenados por popularidad (mayor a menor)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(listaRanking), BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnActualizar);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    // Método auxiliar para colocar etiquetas y controles sin GUI Designer.
    private void agregarCampo(JPanel panel, int fila, String etiqueta, Component campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(4, 5, 4, 5);
        panel.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
    }

    private void conectarEventosNuevos() {
        btnBuscarCodigo.addActionListener(e -> buscarPorCodigo());
        txtBuscarCodi.addActionListener(e -> buscarPorCodigo());
        btnBuscarTexto.addActionListener(e -> buscarPorTexto());
        txtBuscarcoinci.addActionListener(e -> buscarPorTexto());
        listaResultados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarSeleccion();
        });
        btnGuardar.addActionListener(e -> guardarCambios());
        btnMeGusta.addActionListener(e -> darMeGusta());
        btnActualizar.addActionListener(e -> actualizarRanking());
        tabbedPane1.addChangeListener(e -> {
            if (tabbedPane1.getSelectedIndex() == 2) actualizarRanking();
        });
    }

    private void buscarPorCodigo() {
        reiniciarConsulta();
        try {
            int codigo = leerCodigo(txtBuscarCodi);
            int indice = catalogo.buscarIndicePorCodigo(codigo);
            if (indice == -1) {
                informar("No se encontró un libro con el código " + codigo + ".");
                return;
            }
            tipoBusqueda = TipoBusqueda.CODIGO;
            codigoBuscado = codigo;
            refrescarConsulta(codigo);
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void buscarPorTexto() {
        reiniciarConsulta();
        try {
            String texto = txtBuscarcoinci.getText().trim();
            ArrayList<Libro> resultados = catalogo.buscarCoincidencias(texto);
            tipoBusqueda = TipoBusqueda.TEXTO;
            textoBuscado = texto;
            mostrarResultados(resultados, null);
            if (resultados.isEmpty()) informar("No se encontraron coincidencias.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void reiniciarConsulta() {
        tipoBusqueda = TipoBusqueda.NINGUNA;
        mostrarResultados(new ArrayList<>(), null);
    }

    private void mostrarResultados(ArrayList<Libro> libros, Integer conservarCodigo) {
        listaResultados.clearSelection();
        modeloResultados.clear();
        limpiarEdicion();
        int indiceSeleccion = -1;
        for (Libro libro : libros) {
            modeloResultados.addElement(libro);
            if (conservarCodigo != null && libro.getCodigo() == conservarCodigo) {
                indiceSeleccion = modeloResultados.size() - 1;
            }
        }
        lblCoincidencias.setText("Coincidencias: " + libros.size());
        if (indiceSeleccion >= 0) {
            listaResultados.setSelectedIndex(indiceSeleccion);
            listaResultados.ensureIndexIsVisible(indiceSeleccion);
        }
    }

    private void refrescarConsulta(Integer conservarCodigo) {
        ArrayList<Libro> resultados = new ArrayList<>();
        try {
            if (tipoBusqueda == TipoBusqueda.CODIGO) {
                int indice = catalogo.buscarIndicePorCodigo(codigoBuscado);
                if (indice >= 0) resultados.add(catalogo.getLibros().get(indice));
            } else if (tipoBusqueda == TipoBusqueda.TEXTO) {
                resultados = catalogo.buscarCoincidencias(textoBuscado);
            }
            mostrarResultados(resultados, conservarCodigo);
        } catch (Exception ex) {
            reiniciarConsulta();
            error(ex.getMessage());
        }
    }

    private Integer codigoSeleccionado() {
        Libro libro = listaResultados.getSelectedValue();
        return libro == null ? null : libro.getCodigo();
    }

    private void mostrarSeleccion() {
        Libro libro = listaResultados.getSelectedValue();
        if (libro == null) {
            limpiarEdicion();
            return;
        }
        txtEditarCodigo.setText(String.valueOf(libro.getCodigo()));
        txtEditarTitulo.setText(libro.getTitulo());
        txtEditarAutor.setText(libro.getAutor());
        cboEditarGenero.setSelectedItem(libro.getGenero());
        spiEditarAnio.setValue(libro.getAnio());
        lblMeGusta.setText("Me gusta: " + libro.getMeGusta());
        habilitarEdicion(true);
    }

    private void limpiarEdicion() {
        txtEditarCodigo.setText("");
        txtEditarTitulo.setText("");
        txtEditarAutor.setText("");
        cboEditarGenero.setSelectedIndex(0);
        spiEditarAnio.setValue(anioActual);
        lblMeGusta.setText("Me gusta: —");
        habilitarEdicion(false);
    }

    private void habilitarEdicion(boolean habilitada) {
        txtEditarTitulo.setEnabled(habilitada);
        txtEditarAutor.setEnabled(habilitada);
        cboEditarGenero.setEnabled(habilitada);
        spiEditarAnio.setEnabled(habilitada);
        // Los botones permanecen activos para poder mostrar el aviso sin selección.
    }

    private void guardarCambios() {
        Integer codigo = codigoSeleccionado();
        if (codigo == null) {
            informar("Seleccione un libro para editar.");
            return;
        }
        try {
            catalogo.editarLibro(codigo, txtEditarTitulo.getText(), txtEditarAutor.getText(),
                    leerGenero(cboEditarGenero), leerAnio(spiEditarAnio));
            refrescarConsulta(codigo);
            actualizarRanking();
            informar("Cambios guardados correctamente.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void darMeGusta() {
        Integer codigo = codigoSeleccionado();
        if (codigo == null) {
            informar("Seleccione un libro para darle Me gusta.");
            return;
        }
        try {
            catalogo.darMeGusta(codigo);
            refrescarConsulta(codigo);
            actualizarRanking();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void actualizarTotal() {
        lblTotal.setText("Total de libros: " + catalogo.getTotalLibros());
    }

    private void actualizarRanking() {
        modeloRanking.clear();
        for (Libro libro : catalogo.obtenerRanking()) modeloRanking.addElement(libro);
    }

    private int leerCodigo(JTextField campo) throws Exception {
        try {
            int codigo = Integer.parseInt(campo.getText().trim());
            if (codigo > 0) return codigo;
        } catch (NumberFormatException ex) {
            // Unificamos el mensaje para letras, vacío o valores fuera del rango int.
        }
        throw new Exception("El código debe ser un número entero positivo.");
    }

    private int leerAnio(JSpinner spinner) throws Exception {
        try {
            spinner.commitEdit();
        } catch (java.text.ParseException ex) {
            throw new Exception("El año debe ser un entero entre 1 y " + anioActual + ".");
        }
        int anio = (Integer) spinner.getValue();
        if (anio < 1 || anio > anioActual) {
            throw new Exception("El año debe estar entre 1 y " + anioActual + ".");
        }
        return anio;
    }

    private String leerGenero(JComboBox<String> combo) throws Exception {
        if (combo.getSelectedIndex() <= 0) throw new Exception("Seleccione un género válido.");
        return (String) combo.getSelectedItem();
    }

    private void informar(String mensaje) {
        JOptionPane.showMessageDialog(panel1, mensaje, "BiblioLikes", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(panel1, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ventana - BiblioLikes");
            frame.setContentPane(new Ventana().panel1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setMinimumSize(new Dimension(780, 580));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
