package com.nhaarman.ellie.internal;

import com.nhaarman.ellie.Model;

import java.util.List;

public interface RepositoryHolder {

    String IMPL_CLASS_PACKAGE = "com.nhaarman.ellie";
    String IMPL_CLASS_NAME = "RepositoryHolderImpl";
    String IMPL_CLASS_FQCN = IMPL_CLASS_PACKAGE + "." + IMPL_CLASS_NAME;

    <T extends Model> ModelRepository<T> getModelRepository(Class<? extends Model> cls);

    List<? extends ModelRepository> getModelRepositories();

}
