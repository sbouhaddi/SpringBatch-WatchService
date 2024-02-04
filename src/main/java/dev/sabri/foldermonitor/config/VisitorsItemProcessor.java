package dev.sabri.foldermonitor.config;

import dev.sabri.foldermonitor.domain.Visitors;
import dev.sabri.foldermonitor.dto.VisitorsDto;
import dev.sabri.foldermonitor.mapper.VisitorsMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class VisitorsItemProcessor implements ItemProcessor<VisitorsDto, Visitors> {
    private final VisitorsMapper visitorsMapper;

    public VisitorsItemProcessor(VisitorsMapper visitorsMapper) {
        this.visitorsMapper = visitorsMapper;
    }

    @Override
    public Visitors process(final VisitorsDto visitorsDto) throws Exception {
        return visitorsMapper.toVisitors(visitorsDto);
    }
}