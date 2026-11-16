import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Year;

public class Ventana {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTextField txtCodigo;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JComboBox cboCategoria;
    private JSpinner spiAnio;
    private JButton bntRegistrar;
    private JTextField txtBuscarCodi;
    private JTextField txtBuscarcoinci;

    private CatalogoLibros catalogo = new CatalogoLibros();

    public Ventana() {
        int anioActual = Year.now().getValue();
        spiAnio.setModel(new SpinnerNumberModel(anioActual, 1, anioActual, 1));

        bntRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int codigo = Integer.parseInt(txtCodigo.getText().trim());
                    String titulo = txtTitulo.getText();
                    String autor = txtAutor.getText();
                    String cat = cboCategoria.getSelectedItem().toString();
                    int anio = Integer.parseInt(spiAnio.getValue().toString());

                    catalogo.registrar(new Libro(codigo, titulo.trim(), autor.trim(), cat, anio, 0));
                    txtCodigo.setText("");
                    txtTitulo.setText("");
                    txtAutor.setText("");
                    JOptionPane.showMessageDialog(null, "Agregado nuevo libro. Total: " + catalogo.getTotalLibros());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El código debe ser un número entero positivo");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ventana");
        frame.setContentPane(new Ventana().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}