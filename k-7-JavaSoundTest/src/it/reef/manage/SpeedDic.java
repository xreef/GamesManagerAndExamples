package it.reef.manage;

import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.JLabel;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class SpeedDic extends Dictionary<Integer, JLabel> {

	Hashtable labelTable = new Hashtable();
	
	public SpeedDic(){
		super();
		labelTable.put( new Integer( 0 ), new JLabel("x0.5") );
		labelTable.put( new Integer( 1 ), new JLabel("x1") );
		labelTable.put( new Integer( 2 ), new JLabel("x2") );
		labelTable.put( new Integer( 3 ), new JLabel("x3") );
		labelTable.put( new Integer( 4 ), new JLabel("x4") );
	}
	
	
	public Enumeration<JLabel> elements() {
		
		return labelTable.elements();
	}

	
	public JLabel get(Object key) {
		// TODO Auto-generated method stub
		return (JLabel) labelTable.get((Integer)key);
	}

	
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return labelTable.isEmpty();
	}

	
	public Enumeration<Integer> keys() {
		// TODO Auto-generated method stub
		return labelTable.keys();
	}

	
	public JLabel put(Integer key, JLabel value) {
		// TODO Auto-generated method stub
		return (JLabel) labelTable.put(key, value);
	}

	
	public JLabel remove(Object key) {
		// TODO Auto-generated method stub
		return (JLabel) labelTable.remove(key);
	}

	
	public int size() {
		// TODO Auto-generated method stub
		return labelTable.size();
	}

}
