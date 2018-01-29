package hoshisugi.rukoru.app.services.rds;

import java.util.List;

import hoshisugi.rukoru.app.models.rds.RDSInstance;

public interface RDSService {

	List<RDSInstance> listInstances();
}
