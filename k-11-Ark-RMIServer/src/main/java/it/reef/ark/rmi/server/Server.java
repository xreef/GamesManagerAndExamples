package it.reef.ark.rmi.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import it.reef.ark.rmi.scoreservice.RemoteScoreService;
import it.reef.ark.rmi.server.stub.ScoreService;
import it.reef.ark.rmi.util.HibernateUtil;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
        	String nomeServerScore = "ScoreServer";
        	RemoteScoreService rss = new ScoreService();
        	Registry registry = LocateRegistry.getRegistry();
        	registry.rebind(nomeServerScore, rss);
        	//HibernateUtil.getSessionFactory().openSession();
        }catch (Exception e) {
            System.err.println("Server exception:");
            e.printStackTrace();
        }
	}

}
