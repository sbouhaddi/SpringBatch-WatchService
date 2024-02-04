package dev.sabri.foldermonitor.config;

import dev.sabri.foldermonitor.dto.VisitorsDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class VisitorsFieldSetMapper implements FieldSetMapper<VisitorsDto> {
    @Override
    public VisitorsDto mapFieldSet(final FieldSet fieldSet) throws BindException {
        return VisitorsDto.builder()
                .id(fieldSet.readLong("id"))
                .firstName(fieldSet.readString("firstName"))
                .lastName(fieldSet.readString("lastName"))
                .emailAddress(fieldSet.readString("emailAddress"))
                .phoneNumber(fieldSet.readString("phoneNumber"))
                .address(fieldSet.readString("address"))
                .visitDate(fieldSet.readString("visitDate"))
                .build();
    }
}
