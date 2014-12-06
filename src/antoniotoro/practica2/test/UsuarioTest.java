package antoniotoro.practica2.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import antoniotoro.practica2.listacorreos.Usuario;

public class UsuarioTest {
	Usuario usuario;
	String nombreOriginal = "Jose",
	       apellidoOriginal = "Martinez",
	       emailOriginal = "josem@email.com";

	@Before
	public void setUp() throws Exception {
		usuario = new Usuario();
		usuario.setNombre(nombreOriginal);
		usuario.setApellido(apellidoOriginal);
		usuario.setEmail(emailOriginal);
	}

//	@Test
//	public void testGetId() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetId() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testGetNombre() {
		assertEquals(usuario.getNombre(), nombreOriginal);
	}

	@Test
	public void testSetNombre() {
		String nuevoNombre = "Juan";
		usuario.setNombre(nuevoNombre);
		assertEquals(usuario.getNombre(), nuevoNombre);
	}

	@Test
	public void testGetApellido() {
		assertEquals(usuario.getApellido(), apellidoOriginal);
	}

	@Test
	public void testSetApellido() {
		String nuevoApellido = "Gonzalez";
		usuario.setApellido(nuevoApellido);
		assertEquals(usuario.getApellido(), nuevoApellido);
	}

	@Test
	public void testGetEmail() {
		assertEquals(usuario.getEmail(), emailOriginal);
	}

	@Test
	public void testSetEmail() {
		String nuevoEmail = "josemartinez@email.com";
		usuario.setEmail(nuevoEmail);
		assertEquals(usuario.getEmail(), nuevoEmail);
	}

}
