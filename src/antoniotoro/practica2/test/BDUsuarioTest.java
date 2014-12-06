package antoniotoro.practica2.test;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import antoniotoro.practica2.listacorreos.BDUsuario;
import antoniotoro.practica2.listacorreos.Usuario;

public class BDUsuarioTest {
	private static final String PERSISTENCE_UNIT_NAME = "antoniotoro.practica2";
	private static EntityManagerFactory factoria;
	static String nombreOriginal = "Nombre",
	              apellidoOriginal = "Familia",
	              emailOriginal = "nombrefamilia@email.dominio";
	static String prefijo_nuevo = "nuevo_",
	              prefijo_actualizar = "actualizar_";

	@Before
	public void setUp() throws Exception {
		factoria = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager em = factoria.createEntityManager();

		Query q = em.createQuery("SELECT u from Usuario u WHERE u.email LIKE :email")
				.setParameter("email", emailOriginal);
		
		if (q.getResultList().size() == 0) {
			em.getTransaction().begin();
	
			Usuario usuario = new Usuario();
			usuario.setNombre(nombreOriginal);
			usuario.setApellido(apellidoOriginal);
			usuario.setEmail(emailOriginal);
			
			em.persist(usuario);
		
			em.getTransaction().commit();
			em.close();
		}
	}

	@Test
	public void testInsertar() {
		
		Usuario usuario = new Usuario();
		usuario.setNombre(prefijo_nuevo+nombreOriginal);
		usuario.setApellido(prefijo_nuevo+apellidoOriginal);
		usuario.setEmail(prefijo_nuevo+emailOriginal);
		
		BDUsuario.insertar(usuario);

		assertTrue(BDUsuario.existeEmail(prefijo_nuevo+emailOriginal));
	}

	@Test
	public void testActualizar() {
		Usuario usuario = new Usuario();
		usuario.setNombre(prefijo_actualizar+nombreOriginal);
		usuario.setApellido(prefijo_actualizar+apellidoOriginal);
		usuario.setEmail(emailOriginal);
		
		BDUsuario.actualizar(usuario);
		
		usuario = BDUsuario.seleccionarUsuario(emailOriginal);
		assertEquals(usuario.getNombre(), prefijo_actualizar+nombreOriginal);
		assertEquals(usuario.getApellido(), prefijo_actualizar+apellidoOriginal);
	}

	@Test
	public void testEliminar() {
		Usuario usuario = new Usuario();
		usuario.setNombre(nombreOriginal);
		usuario.setApellido(apellidoOriginal);
		usuario.setEmail(emailOriginal);
		
		BDUsuario.eliminar(usuario);

		assertFalse(BDUsuario.existeEmail(emailOriginal));
	}

	@Test
	public void testSeleccionarUsuario() {
		Usuario usuario = BDUsuario.seleccionarUsuario(emailOriginal);
		assertNotNull(usuario);
	}

	@Test
	public void testExisteEmail() {
		assertTrue(BDUsuario.existeEmail(emailOriginal));
		assertFalse(BDUsuario.existeEmail(emailOriginal+"!"));
	}
	
	@After
	public void cleanUp() {
		factoria = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager em = factoria.createEntityManager();

		em.getTransaction().begin();

		Query q = em.createQuery("DELETE FROM Usuario u "
				+ "WHERE u.email LIKE ?1 "
				+ "OR u.email LIKE ?2 "
				+ "OR u.email LIKE ?3")
				.setParameter(1, emailOriginal)
				.setParameter(2, prefijo_actualizar+emailOriginal)
				.setParameter(3, prefijo_nuevo+emailOriginal);
		
		q.executeUpdate();
		em.getTransaction().commit();
		em.close();		
	}

}
