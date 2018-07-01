package it.reef.ark.rmi.util;

/* TODO:unused
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;*/
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil
{
	private static final Log log = LogFactory.getLog(HibernateUtil.class);
	
	private static SessionFactory localSessionFactory;
	
    static
    {
        try
        {
        	log.info("Inizialize localSessionFactory");
        	localSessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
        	log.info("localSessionFactory inizialized!");
        }
        catch (HibernateException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    
    public static SessionFactory getSessionFactory() {
			return localSessionFactory;
	}

    public static Session getCurrentSession(){
    	log.info("Get current session!");
    	Session s = localSessionFactory.getCurrentSession();
    	log.info("Current session returned!");
    	return s;
    	
    }
	
}