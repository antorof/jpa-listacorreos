package antoniotoro.practica2.cliente;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import antoniotoro.practica2.listacorreos.BDUsuario;
import antoniotoro.practica2.listacorreos.Usuario;

import javax.swing.JToolBar;
import javax.swing.JButton;

import java.awt.Dialog.ModalExclusionType;
import java.awt.FlowLayout;

public class Cliente extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private JPanel panelContenido;
	private JTable table;
	private JScrollPane scroll;
	
//	private Object[][] datosTabla;
//	private Object[]   columnasTabla;
	
	private JToolBar toolBar;
	private JButton btnAddUser;

	private JPanel panelAniadir;
	private JLabel lblNombre;
	private JTextField tfNombre;
	private JLabel lblApellido;
	private JTextField tfApellido;
	private JLabel lblEmail;
	private JTextField tfEmail;

	public static String urlString = "http://localhost:8080/antoniotoro.practica2/ListaCorreosServlet";
	private JPanel botonera;
	private JButton btnAniadir;
	private JButton btnCancelar;

	private List<Usuario> usuarios;

	private ModeloTablaUsuarios modeloTablaUsuarios;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try { // Para que no se vea con el look normal de Swing sino con el del sistema
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{ System.err.println("Unable to load System look and feel"); }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cliente frame = new Cliente();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Cliente() {
		setTitle("Pr\u00E1ctica 2 - Antonio Toro");
		setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 300);
		setLocationRelativeTo(null);
		panelContenido = new JPanel();
		panelContenido.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panelContenido);
		panelContenido.setLayout(new BorderLayout(0, 0));
		
		obtenerDatos();

		modeloTablaUsuarios = new ModeloTablaUsuarios(usuarios);
		table = new JTable(modeloTablaUsuarios) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			public void editingStopped(ChangeEvent e) {
//		        super.editingStopped(e);
		        TableCellEditor editor = getCellEditor();
		        if (editor != null) {
		            Object value = editor.getCellEditorValue();
		            Usuario usuario = modeloTablaUsuarios.getUsuarioAt(editingRow);
		            switch (editingColumn) {
			            case 0:
			                usuario.setNombre((String) value);
							break;
			            case 1:
			                usuario.setApellido((String) value);
							break;
						default:
							break;
					}
		            if (editingColumn < 2) {
						String peticion = "action=actualizarUsuario";
						URL url;
						try {
							url = new URL(Cliente.urlString);
							HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
							conexion.setUseCaches(false);
							conexion.setRequestMethod("POST");
							conexion.setDoOutput(true);
							
							OutputStream output = conexion.getOutputStream();

							String query = String.format("nombre=%s&apellido=%s&email=%s", 
									URLEncoder.encode(usuario.getNombre()), 
									URLEncoder.encode(usuario.getApellido()), 
									URLEncoder.encode(usuario.getEmail()));
							
							output.write((peticion+"&"+query).getBytes());
							output.flush();
							output.close();
							
							ObjectInputStream respuesta = new ObjectInputStream(conexion.getInputStream());
							int codigo = respuesta.readInt();
							String mensaje = (String) respuesta.readObject();
							System.out.println(codigo);
							System.out.println(mensaje);
							
							switch (codigo) {
								case 0:
						            setValueAt(value, editingRow, editingColumn);
									break;
								default:
									break;
							}
							
						} catch (MalformedURLException | ProtocolException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
					}
		            removeEditor();
		        }
		    }
		};
//		table = new JTable();
		Action borrarFila = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
		        int modelRow = Integer.valueOf( e.getActionCommand() );
		        Usuario usuario = modeloTablaUsuarios.getUsuarioAt(modelRow);
		        
		        int resultadoDialogo = JOptionPane.showConfirmDialog(
		        		Cliente.this, 
		        		"¿Estás seguro de que quieres elinimar el usuario <"+usuario.getEmail()+">?",
		        		"Eliminar usuario",
		        		JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		        if (resultadoDialogo == JOptionPane.YES_OPTION) {
					String peticion = "action=eliminarUsuario";
					URL url;
					try {
						url = new URL(Cliente.urlString);
						HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
						conexion.setUseCaches(false);
						conexion.setRequestMethod("POST");
						conexion.setDoOutput(true);
						
						OutputStream output = conexion.getOutputStream();
	
						String query = String.format("email=%s", 
								URLEncoder.encode(usuario.getEmail()));
						
						output.write((peticion+"&"+query).getBytes());
						output.flush();
						output.close();
						
						ObjectInputStream respuesta = new ObjectInputStream(conexion.getInputStream());
						int codigo = respuesta.readInt();
						String mensaje = (String) respuesta.readObject();
						System.out.println(codigo);
						System.out.println(mensaje);
						
						switch (codigo) {
							case 0:
						        modeloTablaUsuarios.removeRow(modelRow);
								break;
							default:
								break;
						}
						
					} catch (MalformedURLException | ProtocolException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
			    }
		    }
		};
		 
		new ButtonColumn(table, borrarFila, 3);
		table.putClientProperty("terminateEditOnFocusLost", true);
		scroll = new JScrollPane(table);
		panelContenido.add(scroll, BorderLayout.CENTER);
		
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		panelContenido.add(toolBar, BorderLayout.NORTH);
		
		btnAddUser = new JButton("A\u00F1adir Usuario");
		btnAddUser.setActionCommand("ADDUSER");
		btnAddUser.addActionListener(this);
		toolBar.add(btnAddUser);
		
		panelAniadir = new JPanel();
		panelAniadir.setVisible(false);
		panelContenido.add(panelAniadir, BorderLayout.EAST);
		GridBagLayout gbl_panelAniadir = new GridBagLayout();
		gbl_panelAniadir.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panelAniadir.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelAniadir.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_panelAniadir.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panelAniadir.setLayout(gbl_panelAniadir);
		
		lblNombre = new JLabel("Nombre: ");
		lblNombre.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNombre = new GridBagConstraints();
		gbc_lblNombre.insets = new Insets(0, 0, 5, 5);
		gbc_lblNombre.anchor = GridBagConstraints.EAST;
		gbc_lblNombre.gridx = 0;
		gbc_lblNombre.gridy = 0;
		panelAniadir.add(lblNombre, gbc_lblNombre);
		
		tfNombre = new JTextField();
		GridBagConstraints gbc_tfNombre = new GridBagConstraints();
		gbc_tfNombre.insets = new Insets(0, 0, 5, 5);
		gbc_tfNombre.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfNombre.gridx = 1;
		gbc_tfNombre.gridy = 0;
		panelAniadir.add(tfNombre, gbc_tfNombre);
		tfNombre.setColumns(10);
		
		lblApellido = new JLabel("Apellido: ");
		lblApellido.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblApellido = new GridBagConstraints();
		gbc_lblApellido.anchor = GridBagConstraints.EAST;
		gbc_lblApellido.insets = new Insets(0, 0, 5, 5);
		gbc_lblApellido.gridx = 0;
		gbc_lblApellido.gridy = 1;
		panelAniadir.add(lblApellido, gbc_lblApellido);
		
		tfApellido = new JTextField();
		GridBagConstraints gbc_tfApellido = new GridBagConstraints();
		gbc_tfApellido.insets = new Insets(0, 0, 5, 5);
		gbc_tfApellido.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfApellido.gridx = 1;
		gbc_tfApellido.gridy = 1;
		panelAniadir.add(tfApellido, gbc_tfApellido);
		tfApellido.setColumns(10);
		
		lblEmail = new JLabel("Email: ");
		lblEmail.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.EAST;
		gbc_lblEmail.insets = new Insets(0, 0, 5, 5);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 2;
		panelAniadir.add(lblEmail, gbc_lblEmail);
		
		tfEmail = new JTextField();
		GridBagConstraints gbc_tfEmail = new GridBagConstraints();
		gbc_tfEmail.insets = new Insets(0, 0, 5, 5);
		gbc_tfEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfEmail.gridx = 1;
		gbc_tfEmail.gridy = 2;
		panelAniadir.add(tfEmail, gbc_tfEmail);
		tfEmail.setColumns(10);
		
		botonera = new JPanel();
		GridBagConstraints gbc_botonera = new GridBagConstraints();
		gbc_botonera.gridwidth = 2;
		gbc_botonera.insets = new Insets(0, 0, 0, 5);
		gbc_botonera.fill = GridBagConstraints.BOTH;
		gbc_botonera.gridx = 0;
		gbc_botonera.gridy = 7;
		panelAniadir.add(botonera, gbc_botonera);
		botonera.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnAniadir = new JButton("A\u00F1adir");
		btnAniadir.setActionCommand("EXEC_ANIADIR");
		btnAniadir.addActionListener(this);
		botonera.add(btnAniadir);
		
		btnCancelar = new JButton("Cancelar");
		btnCancelar.setActionCommand("CANCELAR");
		btnCancelar.addActionListener(this);
		botonera.add(btnCancelar);
	}
	
	/**
	 * Obtiene la lista de usuarios del servlet y la almacena en la variable
	 * de clase <tt>usuarios</tt>.
	 */
	@SuppressWarnings("unchecked")
	private void obtenerDatos() {
		try {
//			String peticion = "action=listarUsuarios";
//			URL url = new URL(urlString);
//			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
//			conexion.setUseCaches(false);
//			conexion.setRequestMethod("POST");
//			conexion.setDoOutput(true);
//			
//			OutputStream output = conexion.getOutputStream();
//			output.write(peticion.getBytes());
//			output.flush();
//			output.close();
//			
//			ObjectInputStream respuesta = new ObjectInputStream(conexion.getInputStream());
			
			Map<String,String> parametros = new HashMap<String, String>();
			parametros.put("action", "listarUsuarios");
			
			ObjectInputStream respuesta = new ObjectInputStream(realizarPeticionPost(urlString, parametros));
			
			usuarios = (List<Usuario>) respuesta.readObject();
		} catch (MalformedURLException | ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public InputStream realizarPeticionPost(String url_str, Map<String,String> parametros) {
		String cadenaParametros = "";
		boolean primerPar = true;
		
		for (Map.Entry<String, String> entry : parametros.entrySet()) {
//		    System.out.println(entry.getKey() + "/" + );
			if (!primerPar) {
				cadenaParametros += "&";
			} else {
				primerPar = false;
			}
		    String parDeParametro = String.format("%s=%s", 
					URLEncoder.encode(entry.getKey()), 
					URLEncoder.encode(entry.getValue()));
		    cadenaParametros += parDeParametro;
		}
		try {
			URL url = new URL(url_str);
			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
			conexion.setUseCaches(false);
			conexion.setRequestMethod("POST");
			conexion.setDoOutput(true);
			
			OutputStream output = conexion.getOutputStream();
			output.write(cadenaParametros.getBytes());
			output.flush();
			output.close();
			
			return conexion.getInputStream();
			
		} catch (MalformedURLException | ProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ADDUSER")) {
			panelAniadir.setVisible(true);
		} 
		else if (e.getActionCommand().equals("EXEC_ANIADIR")) {
			String peticion = "action=aniadirUsuario";
			URL url;
			try {
				url = new URL(Cliente.urlString);
				HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
				conexion.setUseCaches(false);
				conexion.setRequestMethod("POST");
				conexion.setDoOutput(true);
				
				OutputStream output = conexion.getOutputStream();

				String query = String.format("nombre=%s&apellido=%s&email=%s", 
						URLEncoder.encode(tfNombre.getText()), 
						URLEncoder.encode(tfApellido.getText()), 
						URLEncoder.encode(tfEmail.getText()));
				
				output.write((peticion+"&"+query).getBytes());
				output.flush();
				output.close();
				
				ObjectInputStream respuesta = new ObjectInputStream(conexion.getInputStream());
				int codigo = respuesta.readInt();
				String mensaje = (String) respuesta.readObject();
				System.out.println(codigo);
				System.out.println(mensaje);
				
				switch (codigo) {
				case 0:
					Usuario usuario = new Usuario();
					usuario.setNombre(tfNombre.getText());
					usuario.setApellido(tfApellido.getText());
					usuario.setEmail(tfEmail.getText());
					modeloTablaUsuarios.add(usuario);
					tfNombre.setText("");
					tfApellido.setText("");
					tfEmail.setText("");
					break;

				default:
					break;
				}
				
			} catch (MalformedURLException | ProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		} 
		else if (e.getActionCommand().equals("CANCELAR")) {
			tfNombre.setText("");
			tfApellido.setText("");
			tfEmail.setText("");
			panelAniadir.setVisible(false);
		}
	}
}
