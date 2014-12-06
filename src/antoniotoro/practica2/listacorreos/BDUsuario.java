package antoniotoro.practica2.listacorreos;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class BDUsuario {
	private static final String PERSISTENCE_UNIT_NAME = "antoniotoro.practica2";
	private static EntityManagerFactory factoria = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

	public static void insertar(Usuario usuario) {
		if (!existeEmail(usuario.getEmail())) {
			EntityManager em = factoria.createEntityManager();
			em.getTransaction().begin();

			em.persist(usuario);
			
			em.getTransaction().commit();
			em.close();
		}
	}
	
	public static void actualizar(Usuario usuario) {
		if (existeEmail(usuario.getEmail())) {
			EntityManager em = factoria.createEntityManager();
			
			Query q = em.createQuery(
					"SELECT u from Usuario u WHERE u.email LIKE :emailUsuario")
					.setParameter("emailUsuario", usuario.getEmail());
			
			Usuario antiguo = (Usuario) q.getSingleResult();

			em.getTransaction().begin();
			
			antiguo.setNombre(usuario.getNombre());
			antiguo.setApellido(usuario.getApellido());

//			em.persist(antiguo);
			
			em.getTransaction().commit();
			em.close();
		}
	}
	
	public static void eliminar(Usuario usuario) {
		if (existeEmail(usuario.getEmail())) {
			EntityManager em = factoria.createEntityManager();

			em.getTransaction().begin();

			Query q = em.createQuery(
					"SELECT u from Usuario u WHERE u.email LIKE :emailUsuario")
					.setParameter("emailUsuario", usuario.getEmail());
			
			Usuario almacenado = (Usuario) q.getSingleResult();
			
			em.remove(almacenado);
			
			em.getTransaction().commit();
			em.close();
		}
	}
	
	public static Usuario seleccionarUsuario(String email) {
		if (existeEmail(email)) {
			EntityManager em = factoria.createEntityManager();
			Query q = em.createQuery(
					"SELECT u from Usuario u WHERE u.email LIKE :emailUsuario")
					.setParameter("emailUsuario", email);
			
			Usuario usuario = (Usuario) q.getSingleResult();
			
			em.close();
			
			return usuario;
		}
		return null;
	}
	
	public static boolean existeEmail(String email) {
		EntityManager em = factoria.createEntityManager();
		Query q = em.createQuery(
				"SELECT u from Usuario u WHERE u.email LIKE :emailUsuario")
				.setParameter("emailUsuario", email);
		
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		} finally {
			em.close();
		}
	}
	
}
