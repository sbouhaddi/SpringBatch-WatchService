package dev.sabri.foldermonitor.dto;

import lombok.Builder;

@Builder
public record VisitorsDto(
        Long visitorId,
        String firstName,
        String lastName,
        String emailAddress,
        String phoneNumber,
        String address,
        String visitDate) {
}
