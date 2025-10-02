package util;

// --- IMPORTS AÑADIDOS PARA OPENPDF ---
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
// --- FIN DE IMPORTS AÑADIDOS ---

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Exportador {

    /**
     * Exporta los datos de un JTable a un archivo CSV.
     * @param table La tabla cuyos datos se van a exportar.
     * @param parentComponent El componente padre para mostrar el diálogo de guardado.
     */
    public static void exportarACSV(JTable table, Component parentComponent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));

        int userSelection = fileChooser.showSaveDialog(parentComponent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter csvWriter = new FileWriter(fileToSave)) {
                TableModel model = table.getModel();
                // Escribir cabeceras
                for (int i = 0; i < model.getColumnCount(); i++) {
                    csvWriter.append(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");

                // Escribir datos de las filas
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        csvWriter.append(model.getValueAt(i, j).toString());
                        if (j < model.getColumnCount() - 1) {
                            csvWriter.append(",");
                        }
                    }
                    csvWriter.append("\n");
                }
                JOptionPane.showMessageDialog(parentComponent, "Datos exportados a CSV con éxito!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentComponent, "Error al exportar a CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Exporta los datos de un JTable a un archivo PDF.
     * @param table La tabla cuyos datos se van a exportar.
     * @param parentComponent El componente padre para mostrar el diálogo de guardado.
     * @param titulo El título que aparecerá en el documento PDF.
     */
    public static void exportarAPDF(JTable table, Component parentComponent, String titulo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));
        
        int userSelection = fileChooser.showSaveDialog(parentComponent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
             if (!fileToSave.getAbsolutePath().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            Document document = new Document(PageSize.A4.rotate());
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Añadir título
                document.add(new Paragraph(titulo));
                document.add(new Paragraph(" ")); // Espacio

                // Crear tabla PDF
                PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
                // Añadir cabeceras
                for (int i = 0; i < table.getColumnCount(); i++) {
                    pdfTable.addCell(table.getColumnName(i));
                }
                // Añadir filas de datos
                for (int rows = 0; rows < table.getRowCount(); rows++) {
                    for (int cols = 0; cols < table.getColumnCount(); cols++) {
                        pdfTable.addCell(table.getModel().getValueAt(rows, cols).toString());
                    }
                }
                document.add(pdfTable);
                JOptionPane.showMessageDialog(parentComponent, "Datos exportados a PDF con éxito!");
            } catch (DocumentException | IOException e) {
                 JOptionPane.showMessageDialog(parentComponent, "Error al exportar a PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (document.isOpen()) {
                    document.close();
                }
            }
        }
    }
}