package dev.sabri.foldermonitor.config;

import dev.sabri.foldermonitor.domain.Visitors;
import dev.sabri.foldermonitor.repositories.VisitorsRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class DbWriter implements ItemWriter<Visitors> {

    private final VisitorsRepository visitorsRepository;

    public DbWriter(VisitorsRepository visitorsRepository) {
        this.visitorsRepository = visitorsRepository;
    }

    @Override
    public void write(Chunk<? extends Visitors> chunk) throws Exception {
        visitorsRepository.saveAll(chunk.getItems());
    }
}