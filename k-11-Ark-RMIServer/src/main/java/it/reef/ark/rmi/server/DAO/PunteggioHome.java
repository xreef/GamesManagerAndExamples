package it.reef.ark.rmi.server.DAO;

// Generated 18-feb-2011 14.27.12 by Hibernate Tools 3.4.0.Beta1

import static org.hibernate.criterion.Example.create;
import it.reef.ark.rmi.scoreservice.Punteggio;
import it.reef.ark.rmi.util.HibernateUtil;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

/**
 * Home object for domain model class Punteggio.
 * @see it.reef.ark.rmi.scoreservice.Punteggio
 * @author Hibernate Tools
 */
public class PunteggioHome {

	private static final Log log = LogFactory.getLog(PunteggioHome.class);

	private Session session = null;
	
	public Session getSession() {
		if (session==null) session = HibernateUtil.getCurrentSession();
		return session;
	}

	public void setSession(Session session) {
		log.debug("set session");
		this.session = session;
	}


	public void persist(Punteggio transientInstance) {
		log.debug("persisting Punteggio instance");
		try {
			getSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Punteggio instance) {
		log.debug("attaching dirty Punteggio instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Punteggio instance) {
		log.debug("attaching clean Punteggio instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Punteggio persistentInstance) {
		log.debug("deleting Punteggio instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Punteggio merge(Punteggio detachedInstance) {
		log.debug("merging Punteggio instance");
		try {
			Punteggio result = (Punteggio) getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Punteggio findById(java.lang.Integer id) {
		log.debug("getting Punteggio instance with id: " + id);
		try {
			Punteggio instance = (Punteggio) getSession()
					.get("it.reef.ark.rmi.scoreservice.Punteggio", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Punteggio> findByExample(Punteggio instance) {
		log.debug("finding Punteggio instance by example");
		try {
			List<Punteggio> results = (List<Punteggio>) getSession()
					.createCriteria("it.reef.ark.rmi.scoreservice.Punteggio")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	public List<Punteggio> listaMiglioriPunteggi(int size){
		log.debug("finding top "+size+" of Punteggio ");
		try {
			List<Punteggio> results = (List<Punteggio>) getSession()
					.createCriteria("it.reef.ark.rmi.scoreservice.Punteggio")
					.setMaxResults(size).addOrder(Order.asc("punteggio")).list();
			log.debug("find top "+size+" of Punteggio successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
