package com.example.notes.metrics;

import com.example.notes.repository.NoteRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

  private final MeterRegistry registry;
  private final NoteRepository noteRepository;

  public MetricsConfig(MeterRegistry registry, NoteRepository noteRepository) {
    this.registry = registry;
    this.noteRepository = noteRepository;
  }

  public static final String METRIC_NOTES_CREATED = "notes.created.count";
  public static final String METRIC_NOTES_LENGTH = "notes.content.length";

  public static Counter notesCreatedCounter;
  public static DistributionSummary notesLengthSummary;

  @PostConstruct
  public void init() {
    notesLengthSummary =
        DistributionSummary.builder(METRIC_NOTES_LENGTH)
            .baseUnit("chars")
            .description("Length of note content")
            .publishPercentileHistogram()
            .register(registry);

    notesCreatedCounter =
        Counter.builder(METRIC_NOTES_CREATED).description("Total created notes").register(registry);

    Gauge.builder("notes.total.gauge", noteRepository, repo -> repo.count())
        .description("Current number of notes in DB")
        .register(registry);
  }
}
