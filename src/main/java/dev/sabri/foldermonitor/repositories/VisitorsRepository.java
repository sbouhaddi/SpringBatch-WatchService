package dev.sabri.foldermonitor.repositories;

import dev.sabri.foldermonitor.domain.Visitors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorsRepository extends JpaRepository<Visitors, Long> {
}