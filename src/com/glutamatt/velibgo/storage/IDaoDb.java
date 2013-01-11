package com.glutamatt.velibgo.storage;

import java.util.List;

public interface IDaoDb<ModelClass> {
	abstract public void save(ModelClass model);
	abstract public ModelClass find(int id);
	abstract public List<ModelClass> getAll();
	abstract public void delete(ModelClass model);
}
