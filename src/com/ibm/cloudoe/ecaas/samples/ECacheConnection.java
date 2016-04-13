package com.ibm.cloudoe.ecaas.samples;

import java.util.ArrayList;
import java.util.List;

import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;

/**
 * Define the elastic caching Operation, mainly in order to program operation.
 * 
 * You can refer to the Elastic Caching Java Native API Specification
 * http://pic.dhe.ibm.com/infocenter/wdpxc/v2r5/index.jsp?topic=%2Fcom.ibm.websphere.datapower.xc.doc%2Fcxslibertyfeats.html
 */
public class ECacheConnection {

	// define the map name of the stored keys
	private static final String keysMapName = "sample2.NONE.P";
	// define the key name of the stored keys
	private static final String keyNameOfKeysMap = "keys.store";

	/**
	 * Get value of this key in mapName
	 * 
	 * @param mapName
	 * @param key
	 * @return
	 * @throws ObjectGridException
	 */
	public static Object getData(ObjectGrid og, String mapName, String key) throws ObjectGridException {
		Session ogSession = null;
		try {
			ogSession = og.getSession();
			ObjectMap map = ogSession.getMap(mapName);
			return map.get(key);
		} finally {
			if (ogSession != null) {
				ogSession.close();
			}
		}
	}

	/**
	 * Update or insert this value in mapName
	 * 
	 * @param mapName
	 * @param key
	 * @param newValue
	 * @throws ObjectGridException
	 */
	public static void postData(ObjectGrid og, String mapName, String key, String newValue) throws ObjectGridException {
		Session ogSession = null;
		try {
			ogSession = og.getSession();
			ObjectMap map = ogSession.getMap(mapName);
			map.upsert(key, newValue);
			postKeyTemp(og, ogSession, key);
		} finally {
			if (ogSession != null) {
				ogSession.close();
			}
		}
	}

	/**
	 * Delete this key/value in mapName
	 * 
	 * @param mapName
	 * @param key
	 * @throws ObjectGridException
	 */
	public static void deleteData(ObjectGrid og, String mapName, String key) throws ObjectGridException {
		Session ogSession = null;
		try {
			ogSession = og.getSession();
			ObjectMap map = ogSession.getMap(mapName);
			map.remove(key);
			deleteKeyTemp(og, ogSession, key);
		} finally {
			if (ogSession != null) {
				ogSession.close();
			}
		}
	}

	/**
	 * Get all ECache Object in mapName
	 * 
	 * @param mapName
	 * @return
	 * @throws ObjectGridException
	 */
	public static List<ECache> getAllData(ObjectGrid og, String mapName) throws ObjectGridException {
		Session ogSession = null;
		try {
			ogSession = og.getSession();
			ObjectMap map = ogSession.getMap(mapName);
			List<String> keys = getAllKeys(og, ogSession, keysMapName);
			List<String> values = map.getAll(keys);
			return getECaches(keys, values);
		} finally {
			if (ogSession != null) {
				ogSession.close();
			}
		}
	}

	/**
	 * Get all keys in mapName
	 * 
	 * @param map
	 * @return
	 * @throws ObjectGridException
	 */
	private static List<String> getAllKeys(ObjectGrid og, Session session, String mapName) throws ObjectGridException {
		ObjectMap keysMap = session.getMap(mapName);
		List<String> keys = keysMap.get(keyNameOfKeysMap) != null ? (List<String>) keysMap.get(keyNameOfKeysMap)
				: new ArrayList<String>();
		return keys;
	}

	/**
	 * Add this key in temp keys map
	 * 
	 * @param key
	 * @throws ObjectGridException
	 */
	private static void postKeyTemp(ObjectGrid og, Session session, String key) throws ObjectGridException {
		ObjectMap keysMap = session.getMap(keysMapName);
		List<String> keys = getAllKeys(og, session, keysMapName);
		if (!keys.contains(key))
			keys.add(key);
		keysMap.upsert(keyNameOfKeysMap, keys);
	}

	/**
	 * Delete this key/value in temp keys map
	 * 
	 * @param key
	 * @throws ObjectGridException
	 */
	private static void deleteKeyTemp(ObjectGrid og, Session session, String key) throws ObjectGridException {
		ObjectMap keysMap = session.getMap(keysMapName);
		List<String> keys = getAllKeys(og, session, keysMapName);
		if (keys.contains(key))
			keys.remove(key);
		keysMap.upsert(keyNameOfKeysMap, keys);
	}

	/**
	 * Get all ECache Object
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	private static List<ECache> getECaches(List<String> keys, List<String> values) {
		List<ECache> res = new ArrayList<ECache>();
		for (int i = 0; i < keys.size(); i++) {
			res.add(new ECache(keys.get(i), values.get(i)));
		}
		return res;
	}

}