package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.spring.domain.Instrument;
import org.jimsey.projects.turbine.spring.elasticsearch.repositories.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchService {

  @Autowired
  @NotNull
  private InstrumentRepository instrumentRepository;
  
  @Autowired
  @NotNull
  protected DomainObjectGenerator rdog;
  
  @PostConstruct
  public void test() {
    System.out.println("TEST");
    Instrument instrument = rdog.newInstrument();
    
    instrumentRepository.save(instrument);
    
    Iterable<Instrument> instruments = instrumentRepository.findAll();
    for (Instrument item : instruments) {
      System.out.println(item.toString());
    }
  }
  
}
