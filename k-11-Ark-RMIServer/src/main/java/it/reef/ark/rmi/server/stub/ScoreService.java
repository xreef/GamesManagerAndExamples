package it.reef.ark.rmi.server.stub;

import it.reef.ark.rmi.scoreservice.Punteggio;
import it.reef.ark.rmi.scoreservice.RemoteScoreService;
import it.reef.ark.rmi.server.DAO.PunteggioHome;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ScoreService extends UnicastRemoteObject implements RemoteScoreService {

	public ScoreService() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setScore(Punteggio p) throws RemoteException {
		PunteggioHome ph = new PunteggioHome();

		if (p.getNome() != null && !p.getNome().equals("")
				&& p.getDataInserimento() != null && p.getPunteggio() > 0) {
			try {
				ph.getSession().beginTransaction();
				ph.persist(p);
				ph.getSession().getTransaction().commit();
			} catch (Exception e) {
				ph.getSession().getTransaction().rollback();
				throw new RemoteException("Problemi sul server del punteggio. Il punteggio sarà perso.");
			}
		}
	}

	public List<Punteggio> getListaScore() throws RemoteException {
		PunteggioHome ph = new PunteggioHome();
		try {
			ph.getSession().beginTransaction();
			List<Punteggio> l = ph.listaMiglioriPunteggi(15);
			ph.getSession().getTransaction().commit();
			return l;
		} catch (Exception e) {
			ph.getSession().getTransaction().rollback();
		}
		return null;
	}

}
