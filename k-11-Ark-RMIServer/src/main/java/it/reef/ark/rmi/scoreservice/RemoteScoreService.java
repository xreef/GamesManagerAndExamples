package it.reef.ark.rmi.scoreservice;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteScoreService  extends Remote  {
	public void setScore(Punteggio p) throws RemoteException;
	public List<Punteggio> getListaScore() throws RemoteException;
}
