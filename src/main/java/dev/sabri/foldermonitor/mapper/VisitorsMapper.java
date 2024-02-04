package dev.sabri.foldermonitor.mapper;

import dev.sabri.foldermonitor.domain.Visitors;
import dev.sabri.foldermonitor.dto.VisitorsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisitorsMapper {

    Visitors toVisitors(VisitorsDto visitorsDto);
}
