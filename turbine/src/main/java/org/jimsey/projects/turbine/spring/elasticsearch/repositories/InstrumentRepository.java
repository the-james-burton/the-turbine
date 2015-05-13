package org.jimsey.projects.turbine.spring.elasticsearch.repositories;

import org.jimsey.projects.turbine.spring.domain.Instrument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface InstrumentRepository extends ElasticsearchRepository<Instrument, Long> {

}
