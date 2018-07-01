package it.reef.ark.rmi.test;


import it.reef.ark.rmi.scoreservice.Punteggio;
import it.reef.ark.rmi.server.DAO.PunteggioHome;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestRMIServer {

	private static SessionFactory localSessionFactory;
	@Before
	public void setUp() throws Exception {
		localSessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
	}

	@After
	public void tearDown() throws Exception {
		localSessionFactory.close();
	}

	@Test
	public void testSalvataggio(){
		PunteggioHome ph = new PunteggioHome();
		ph.setSession(localSessionFactory.openSession());
		ph.getSession().beginTransaction();
		Punteggio p = new Punteggio();
		p.setDataInserimento(new java.util.Date());
		p.setNome("Test case nome");
		p.setPunteggio(1001);
		ph.persist(p);
		ph.getSession().getTransaction().commit();
		ph.getSession().close();
		Assert.assertTrue(true);
	}
}
