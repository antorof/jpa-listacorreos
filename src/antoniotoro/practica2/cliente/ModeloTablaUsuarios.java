package antoniotoro.practica2.cliente;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import antoniotoro.practica2.listacorreos.Usuario;

/**
 * Modelo de tabla personalizado para una tabla con los usuarios.
 */
public class ModeloTablaUsuarios extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	/** Lista con los usuarios */
	private List<Usuario> lista;
	/** Nombres de las columnas */
    private String[] columnNames = { "Nombre", "Apellido", "Email", "Acci\u00F3n" };

    public ModeloTablaUsuarios(List<Usuario> usuarios){
        this.lista = usuarios;
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	if (columnIndex == 2) {
			return false;
		}
    	return true;
    }

    /**
     * Aniade un usuario a la tabla.
     * @param usuario Usuario que se quiere aniadir
     */
    public void add(Usuario usuario) {
        lista.add(usuario);
        fireTableDataChanged();
    }

    /**
     * Elimina un usuario de la tabla.
     * @param usuario Usuario a eliminar
     */
    public void remove(Usuario usuario) {
        if (lista.contains(usuario)) {
            lista.remove(usuario);
            fireTableDataChanged();
        }
    }
    
    /**
     * Elimina una fila de la tabla.
     * @param rowIndez Fila que se quiere eliminar
     */
    public void removeRow(int rowIndex) {
    	lista.remove(rowIndex);
        fireTableDataChanged();
    }

    /**
     * Devuelve el usuario correspondiente a una fila.
     * @param rowIndex Fila correspondiente al usuario que se quiere
     * @return
     */
    public Usuario getUsuarioAt(int rowIndex) {
    	return lista.get(rowIndex);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Usuario usuario = lista.get(rowIndex);
        switch (columnIndex){
            case 0:
                return usuario.getNombre();
            case 1:
                return usuario.getApellido();
            case 2:
                return usuario.getEmail();
            case 3: 
            	return "Borrar";
        }
        return "";
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
    	Usuario usuario = lista.get(rowIndex);
        switch (columnIndex){
	        case 0:
	            usuario.setNombre((String) value);
				break;
	        case 1:
	            usuario.setApellido((String) value);
				break;
	        case 2:
	            usuario.setEmail((String) value);
				break;
        }
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
}
